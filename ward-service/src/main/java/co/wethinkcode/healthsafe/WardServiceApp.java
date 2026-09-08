package co.wethinkcode.healthsafe;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import co.wethinkcode.healthsafe.mq.StaffingEventSubscriber;
import co.wethinkcode.healthsafe.mq.StaffingUpdate;

import io.javalin.Javalin;

public class WardServiceApp {

    private  static  String uri = "http://localhost:7030/wards" ;

    public static void main(String[] args) {
        Javalin app = Javalin.create().start(7031);

        HttpClient client = createHttpClient();

        StaffingEventSubscriber.start();
        Runtime.getRuntime().addShutdownHook(new Thread(StaffingEventSubscriber::stop));


        app.get("/health", ctx -> ctx.result("OK"));

        // TODO (Provides lists of wards and departments.)
        // Add domain endpoints for ward-service here.
        app.get("/wards", ctx -> {

        HttpRequest request = createHttpRequest(uri);

        HttpResponse<String> response = sendHttpRequest(client, request);

        if (response == null) {
            ctx.status(502).result("Ingestion service unavailable");
                return;
        }

        if (response.statusCode() != 200) {
                ctx.status(502).result("Ingestion service returned an error");
                return;
        }

        List<Ward> wards = deserializeJson(response);

        ctx.json(wards);

        });

        app.get("/wards/{wardId}", ctx -> {

            String wardId = ctx.pathParam("wardId");
            HttpRequest request =createHttpRequest(uri);

            HttpResponse<String> response =sendHttpRequest(client, request);

            

            if (response == null) {
                ctx.status(502).result("Ingestion service unavailable");
                return;
            }

            if (response.statusCode() != 200) {
                ctx.status(502).result("Ingestion service returned an error");
                return;
            }


            List<Ward> wards = deserializeJson(response);

            for (Ward ward : wards) {

                if (ward.getWardId().equalsIgnoreCase(wardId)) {
                    ctx.json(ward);
                    return;
                }
            }

             ctx.status(404).result("Ward not found");
           
        });

        app.get("/wards/{wardId}/staffing", ctx -> {

            String wardId = ctx.pathParam("wardId");
            StaffingUpdate update = StaffingEventSubscriber.getLatest(wardId);

            if (update == null) {
                ctx.status(404).result(
                        "No staffing update received yet for ward '" + wardId + "'");
                return;
            }

            ctx.json(update);
        });


        app.get("/departments", ctx -> {

            HttpRequest request = createHttpRequest(uri);

            HttpResponse<String> response = sendHttpRequest(client, request);

            if (response == null) {
                ctx.status(502).result("Ingestion service unavailable");
                return;
            }

            if (response.statusCode() != 200) {
                ctx.status(502).result("Ingestion service returned an error");
                return;
            }

            List<Ward> wards = deserializeJson(response);
            List<String> departments = new ArrayList<String>();

            for(Ward ward:wards){
                if(ward.getDepartment() != null && !(departments.contains(ward.getDepartment()))){
                departments.add(ward.getDepartment());
                }
            }
            ctx.json(departments);
        });
            

    }


    private static List<Ward> deserializeJson(HttpResponse<String> response){
        ObjectMapper mapper = new ObjectMapper();

        try {
            return mapper.readValue(response.body(),new TypeReference<List<Ward>>() {});

        } catch (JsonProcessingException e) {

            throw new RuntimeException("Could not deserialize wards JSON", e);

        }

    }


    private static HttpClient createHttpClient(){

        return HttpClient.newHttpClient();

    }

    private static  HttpRequest createHttpRequest(String uri){
      return HttpRequest.newBuilder()
        .uri(URI.create(uri)) 
        .GET()
        .build();
    }

    private static  HttpResponse<String> sendHttpRequest(HttpClient client,HttpRequest request){

        HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());

            return  response;

        } catch (IOException e) {
             return null;

        }catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }
    } 
}

// MQ TODO: subscribes to ActiveMQ topic MqConfig.TOPIC at MqConfig.BROKER_URL (see co.wethinkcode.healthsafe.mq.MqConfig)
// MQ TODO: publishes to ActiveMQ queue MqConfig.QUEUE when it detects an equipment failure on one of its wards.
