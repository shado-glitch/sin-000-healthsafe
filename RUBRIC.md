# HealthSafe — Reviewer Rubric

**For reviewers only. Do not share this file with candidates.**

Scores against the stages defined in the root [README.md](README.md#your-task).
Use judgment on partial credit — this is a guide for consistency across
reviewers, not a strict checklist.

## Stage 1 — Ingestion (required)

- [ ] Handles inconsistent casing (IDs, names, status values)
- [ ] Trims padding / collapses internal double spaces
- [ ] Detects and handles duplicate records (e.g. `W-05` vs `w-05` with differing fields)
- [ ] Normalizes inconsistent date formats, rejects/flags invalid dates
- [ ] Normalizes missing/placeholder values (`N/A`, `TBD`, blank, `-`, `NaN`) to one representation
- [ ] Handles invalid/non-numeric values in numeric columns (`five`, `full`, negative counts) without crashing
- [ ] Normalizes boolean/flag variants
- [ ] Exposes cleaned records over REST (`GET /wards` or similar) for `ward-service`

**Signal to watch for:** does the candidate handle bad rows gracefully (flag/skip/default)
or does one malformed row crash the whole parse? Robustness here says more than
coverage of every listed issue.

## Stage 2 — REST services (required)

- [ ] `ward-service`: lists wards/departments, sourced from ingestion-service
- [ ] `alert-level-service`: tracks and exposes Emergency Status (0-8), validates range
- [ ] `staffing-service`: computes on-call schedule from ward + current status,
      calling `ward-service` and `alert-level-service` synchronously
- [ ] Sensible HTTP status codes (404 for unknown ward, 400 for invalid input, etc.)
- [ ] Services remain independently runnable (no accidental coupling beyond the
      documented HTTP calls)

**Signal to watch for:** does staffing-service handle a downstream 404/timeout from
ward-service or alert-level-service, or does it assume the happy path?

## Stage 3 — MQ decoupling (stretch)

- [ ] `staffing-service` publishes to `staffing-events-topic` on schedule/status change
- [ ] `ward-service` subscribes and reacts, replacing the direct call it stage 2 required
- [ ] Candidate can articulate *why* a topic (broadcast, fire-and-forget) fits this case

## Stage 4 — Alerting (stretch)

- [ ] `ward-service` publishes to `equipment-failure-queue` on detecting a failure
- [ ] `equipment-alert-service` consumes with guaranteed delivery (ack/persistence,
      not just a bare listener)
- [ ] Candidate can articulate *why* a queue (guaranteed, single-consumer) fits this
      case, in contrast to the topic in stage 3

## Cross-cutting (all stages)

- [ ] Code is readable without a walkthrough
- [ ] Commits/PR history show incremental, reviewable steps rather than one dump
- [ ] Any tests added are meaningful, not padding
- [ ] Candidate flags assumptions/tradeoffs they made (e.g. duplicate-merge strategy)
      rather than silently picking one

Verfication code =      WTC-JAD5EQAG
