# HealthSafe root Makefile
#
# Every service here is an independent Maven module (no parent/aggregator
# pom), and there's no orchestrator wiring them together — this Makefile is
# that glue for local dev. Run `make help` for the full target list.

SHELL := /bin/bash

INGESTION := ingestion-service
WARD      := ward-service
ALERT     := alert-level-service
STAFFING  := staffing-service
EQUIPMENT := equipment-alert-service

ALL_SERVICES := $(INGESTION) $(WARD) $(ALERT) $(STAFFING) $(EQUIPMENT)

PID_DIR := .pids
LOG_DIR := logs

# port lookup shared by shell recipes below — usage: port=$$(port_of svc-name)
define PORT_OF_FN
port_of() { case "$$1" in \
	ingestion-service) echo 7030 ;; \
	ward-service) echo 7031 ;; \
	alert-level-service) echo 7032 ;; \
	staffing-service) echo 7033 ;; \
	equipment-alert-service) echo 7034 ;; \
	esac; }
endef

.PHONY: help build test clean \
        run-ingestion run-ward run-alert run-staffing run-equipment \
        run-all run-stage1 run-stage2 run-stage3 run-stage4 \
        stop-all status mq-up mq-down mq-logs \
        verify-stage1 verify-stage2 verify-stage3 verify-stage4

help:
	@echo "HealthSafe — common targets:"
	@echo "  make build            build every module (mvn package)"
	@echo "  make test             run tests in every module (mvn test)"
	@echo "  make mq-up            start the ActiveMQ broker (common/)"
	@echo "  make mq-down          stop the ActiveMQ broker"
	@echo "  make run-stage1       build+run ingestion-service only"
	@echo "  make run-stage2       build+run ingestion, ward, alert, staffing"
	@echo "  make run-stage3       run-stage2 + mq-up (topic decoupling active)"
	@echo "  make run-stage4       run-stage3 + equipment-alert-service"
	@echo "  make run-all          alias for run-stage4 (everything)"
	@echo "  make status           curl /health on every service"
	@echo "  make stop-all         kill every service started by this Makefile"
	@echo "  make verify-stage1    inspect cleaned ingestion output"
	@echo "  make verify-stage2    exercise ward/alert/staffing REST endpoints"
	@echo "  make verify-stage3    flip alert level, confirm topic delivery"
	@echo "  make verify-stage4    placeholder check for the equipment queue"
	@echo "  make clean            mvn clean in every module + wipe pids/logs"

# --- build / test -----------------------------------------------------------

build:
	@for s in $(ALL_SERVICES); do \
		echo "==> building $$s"; \
		(cd $$s && mvn -q package) || exit 1; \
	done

test:
	@for s in $(ALL_SERVICES); do \
		echo "==> testing $$s"; \
		(cd $$s && mvn -q test) || exit 1; \
	done

clean:
	@for s in $(ALL_SERVICES); do \
		(cd $$s && mvn -q clean); \
	done
	@rm -rf $(PID_DIR) $(LOG_DIR)

# --- MQ broker (stage 3/4) ---------------------------------------------------

mq-up:
	cd common && docker compose up -d
	@echo "ActiveMQ web console: http://localhost:8161 (admin/admin)"

mq-down:
	cd common && docker compose down

mq-logs:
	cd common && docker compose logs -f

# --- run one service in the background --------------------------------------
# Builds if the jar is missing, launches with nohup, waits for /health.

define run_service
	@mkdir -p $(PID_DIR) $(LOG_DIR); \
	$(PORT_OF_FN); \
	svc="$(1)"; \
	port=$$(port_of $$svc); \
	if [ -f $(PID_DIR)/$$svc.pid ] && kill -0 $$(cat $(PID_DIR)/$$svc.pid) 2>/dev/null; then \
		echo "$$svc already running (pid $$(cat $(PID_DIR)/$$svc.pid))"; \
	else \
		if [ ! -f $$svc/target/$$svc.jar ]; then \
			echo "==> building $$svc (jar not found)"; \
			(cd $$svc && mvn -q package) || exit 1; \
		fi; \
		echo "==> starting $$svc on port $$port"; \
		cd $$svc; \
		nohup java -jar target/$$svc.jar > ../$(LOG_DIR)/$$svc.log 2>&1 & \
		pid=$$!; \
		cd ..; \
		echo $$pid > $(PID_DIR)/$$svc.pid; \
		ok=0; \
		for i in $$(seq 1 20); do \
			if curl -sf --max-time 2 http://localhost:$$port/health >/dev/null 2>&1; then ok=1; break; fi; \
			sleep 0.5; \
		done; \
		if [ $$ok -eq 1 ]; then \
			echo "    $$svc healthy on :$$port"; \
		else \
			echo "    WARNING: $$svc did not become healthy — check $(LOG_DIR)/$$svc.log"; \
			tail -n 20 $(LOG_DIR)/$$svc.log 2>/dev/null || true; \
		fi; \
	fi
endef

run-ingestion:
	$(call run_service,$(INGESTION))

run-ward:
	$(call run_service,$(WARD))

run-alert:
	$(call run_service,$(ALERT))

run-staffing:
	$(call run_service,$(STAFFING))

run-equipment:
	$(call run_service,$(EQUIPMENT))

# --- staged run targets, in dependency order --------------------------------

run-stage1: run-ingestion

run-stage2: run-stage1 run-ward run-alert run-staffing

run-stage3: mq-up run-stage2
	@echo "==> Stage 3 ready: staffing-service publishes, ward-service subscribes via staffing-events-topic"

run-stage4: run-stage3 run-equipment
	@echo "==> Stage 4 ready: equipment-alert-service consuming equipment-failure-queue"

run-all: run-stage4

# --- status / stop ------------------------------------------------------------

status:
	@$(PORT_OF_FN); \
	for s in $(ALL_SERVICES); do \
		port=$$(port_of $$s); \
		if curl -sf --max-time 2 http://localhost:$$port/health >/dev/null 2>&1; then \
			echo "$$s  :$$port  UP"; \
		else \
			echo "$$s  :$$port  down"; \
		fi; \
	done

stop-all:
	@for s in $(ALL_SERVICES); do \
		if [ -f $(PID_DIR)/$$s.pid ]; then \
			pid=$$(cat $(PID_DIR)/$$s.pid); \
			if kill -0 $$pid 2>/dev/null; then \
				echo "stopping $$s (pid $$pid)"; \
				kill $$pid; \
			fi; \
			rm -f $(PID_DIR)/$$s.pid; \
		fi; \
	done

# --- verification helpers -----------------------------------------------------
# These mirror the manual curl checks used to review each stage.

verify-stage1:
	@echo "--- cleaned ward records (ingestion-service :7030) ---"
	@curl -s --max-time 5 http://localhost:7030/wards | (command -v jq >/dev/null && jq . || cat)
	@echo
	@echo "Check by eye: W-05 appears once with a duplicate note, casing/spacing"
	@echo "normalized, non-numeric beds flagged (null + note), placeholders unified."

verify-stage2:
	@echo "--- ward-service (:7031) ---"
	@curl -s --max-time 5 http://localhost:7031/wards | (command -v jq >/dev/null && jq . || cat); echo
	@curl -s --max-time 5 http://localhost:7031/wards/W-05 | (command -v jq >/dev/null && jq . || cat); echo
	@echo "-> expect 404 for an unknown ward:"
	@curl -i -s --max-time 5 http://localhost:7031/wards/DOES-NOT-EXIST | head -n 1
	@curl -s --max-time 5 http://localhost:7031/departments | (command -v jq >/dev/null && jq . || cat); echo
	@echo "--- alert-level-service (:7032) ---"
	@curl -s --max-time 5 http://localhost:7032/alert-level | (command -v jq >/dev/null && jq . || cat); echo
	@curl -i -s --max-time 5 -X PUT http://localhost:7032/alert-level -H 'Content-Type: application/json' -d '{"level":5}' | head -n 1
	@curl -s --max-time 5 http://localhost:7032/alert-level | (command -v jq >/dev/null && jq . || cat); echo
	@echo "-> expect 400 for an out-of-range level:"
	@curl -i -s --max-time 5 -X PUT http://localhost:7032/alert-level -H 'Content-Type: application/json' -d '{"level":99}' | head -n 1
	@echo "--- staffing-service (:7033) ---"
	@curl -s --max-time 5 http://localhost:7033/staffing/W-05 | (command -v jq >/dev/null && jq . || cat); echo
	@echo "-> expect 404, propagated from ward-service:"
	@curl -i -s --max-time 5 http://localhost:7033/staffing/DOES-NOT-EXIST | head -n 1

verify-stage3:
	@echo "--- flipping alert level to trigger a staffing change ---"
	@curl -s --max-time 5 -X PUT http://localhost:7032/alert-level -H 'Content-Type: application/json' -d '{"level":6}' >/dev/null
	@echo "-> GET /staffing/W-05 triggers staffing-service to publish if doctorsRequired changed:"
	@curl -s --max-time 5 http://localhost:7033/staffing/W-05 | (command -v jq >/dev/null && jq . || cat); echo
	@sleep 1
	@echo "-> ward-service should now reflect this via the topic subscription, not a direct call:"
	@curl -s --max-time 5 http://localhost:7031/wards/W-05/staffing | (command -v jq >/dev/null && jq . || cat); echo
	@echo "ActiveMQ console: http://localhost:8161 (admin/admin) — check staffing-events-topic"

verify-stage4:
	@echo "--- equipment-alert-service (:7034) ---"
	@curl -s --max-time 5 http://localhost:7034/health; echo
	@echo "-> reporting an equipment failure from ward-service:"
	@curl -s --max-time 5 -X POST http://localhost:7031/wards/W-05/equipment-failure \
		-H 'Content-Type: application/json' \
		-d '{"equipment":"Ventilator-12","message":"Ventilator stopped responding"}' \
		| (command -v jq >/dev/null && jq . || cat); echo
	@sleep 1
	@echo "-> alerts consumed by equipment-alert-service:"
	@curl -s --max-time 5 http://localhost:7034/alerts \
		| (command -v jq >/dev/null && jq . || cat); echo
	@echo "The producer uses PERSISTENT delivery and the consumer uses CLIENT_ACKNOWLEDGE."
	@echo "To demonstrate persistence, stop equipment-alert-service, POST another failure, restart it, and GET /alerts."
