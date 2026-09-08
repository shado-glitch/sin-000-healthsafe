package co.wethinkcode.healthsafe;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.javalin.Javalin;

public class StaffingServiceApp {

    private static final String WARD_SERVICE ="http://localhost:7031/wards/";
    private static final String ALERT_LEVEL_SERVICE ="http://localhost:7032/alert-level";
    private static final HttpClient CLIENT =HttpClient.newHttpClient();
    private static final ObjectMapper MAPPER =new ObjectMapper();

    public static void main(String[] args) {
        Javalin app = Javalin.create().start(7033);

        app.get("/health", ctx -> ctx.result("OK"));

        // TODO (Provides on-call schedules for doctors based on ward and status.)
        // Add domain endpoints for staffing-service here.

         app.get("/staffing/{wardId}", ctx -> {

            String wardId = ctx.pathParam("wardId");
            HttpResponse<String> wardResponse =createHttpRequest(WARD_SERVICE + wardId);

            if (wardResponse == null) {
                ctx.status(502).json(error("Ward service unavailable"));
                return;
            }

            if (wardResponse.statusCode() == 404) {
                ctx.status(404).json(error("Ward not found"));
                return;
            }

            if (wardResponse.statusCode() != 200) {
                ctx.status(502).json(error("Ward service returned an error"));
                return;
            }

            JsonNode ward = MAPPER.readTree(wardResponse.body());
            HttpResponse<String> alertResponse =createHttpRequest(ALERT_LEVEL_SERVICE);

            if (alertResponse == null) {
                ctx.status(502).json(error("Alert level service unavailable"));
                return;
            }

            if (alertResponse.statusCode() != 200) {
                ctx.status(502).json(error("Alert level service returned an error"));
                return;
            }

            JsonNode alert = MAPPER.readTree(alertResponse.body());
            JsonNode levelNode = alert.get("level");

            if (levelNode == null || !levelNode.isInt()) {
                ctx.status(502).json(error("Invalid alert level response"));
                return;
            }

            int level = levelNode.asInt();
            int doctorsRequired = calculateDoctors(level);

            
            Map<String, Object> result =new LinkedHashMap<>();

            result.put("wardId", ward.get("wardId").asText());
            result.put("wing", ward.get("wing").asText());
            result.put("department", ward.get("department").asText());
            result.put("alertLevel", level);
            result.put("doctorsRequired", doctorsRequired);

            ctx.json(result);
        });
    }

    /**
     * Calculates the number of doctors required based on the alert level.
     *
     * Alert levels:
     * 0-2 -> 1 doctor
     * 3-4 -> 2 doctors
     * 5-6 -> 3 doctors
     * 7-8 -> 4 doctors
     *
     * This staffing policy is an implementation assumption.
     */
    static int calculateDoctors(int alertLevel) {
        if (alertLevel <= 2) return 1;
        if (alertLevel <= 4) return 2;
        if (alertLevel <= 6) return 3;
        return 4;
    }

    private static HttpResponse<String> createHttpRequest(String url) {

        try {

            HttpRequest request =HttpRequest.newBuilder().uri(URI.create(url)).GET().build();

            return CLIENT.send(request,HttpResponse.BodyHandlers.ofString());

        } catch (IOException e) {

            return null;

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();
            return null;
        }
    }

    private static Map<String, String> error(String message) {

        Map<String, String> error =new LinkedHashMap<>();

        error.put("error", message);

        return error;
    }
}

// MQ TODO: publishes to ActiveMQ topic MqConfig.TOPIC at MqConfig.BROKER_URL (see co.wethinkcode.healthsafe.mq.MqConfig)
