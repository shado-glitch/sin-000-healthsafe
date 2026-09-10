package co.wethinkcode.healthsafe;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.fasterxml.jackson.databind.ObjectMapper;

import co.wethinkcode.healthsafe.mq.EquipmentFailureConsumer;
import io.javalin.Javalin;
import io.javalin.json.JavalinJackson;

public class EquipmentAlertServiceApp {

    private static final List<EquipmentFailureAlert> receivedAlerts =
            new CopyOnWriteArrayList<>();

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static void main(String[] args) {

        Javalin app = Javalin.create(config -> {
            config.jsonMapper(new JavalinJackson(MAPPER));
        }).start(7034);

        app.get("/health", ctx -> ctx.result("OK"));

        EquipmentFailureConsumer.start(body -> {

            try {
                EquipmentFailureAlert alert =
                        MAPPER.readValue(body, EquipmentFailureAlert.class);

                receivedAlerts.add(alert);

            } catch (Exception e) {

                System.err.println(
                        "Could not convert equipment alert: "
                        + e.getMessage()
                );
            }
        });

        Runtime.getRuntime().addShutdownHook(
                new Thread(EquipmentFailureConsumer::stop));

        app.get("/alerts", ctx -> ctx.json(receivedAlerts));
    }
}

