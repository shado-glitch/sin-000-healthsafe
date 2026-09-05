package co.wethinkcode.healthsafe;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import io.javalin.Javalin;

public class WardServiceApp {

    public static void main(String[] args) {
        Javalin app = Javalin.create().start(7031);

        /*1. Create a HttpClient */

        HttpClient client = creaHttpClient();

        /*2. create request */

        HttpRequest request = creatHttpRequest("http://localhost:7030/wards");

        HttpResponse<String> response = sendHttpRequest(client, request);

        app.get("/health", ctx -> ctx.result("OK"));

        // TODO (Provides lists of wards and departments.)
        // Add domain endpoints for ward-service here.
        app.get("/wards", ctx -> ctx.result(response.body()));

    }


    private static HttpClient creaHttpClient(){

        return HttpClient.newHttpClient();

    }

    private static  HttpRequest creatHttpRequest(String uri){
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

        } catch (IOException | InterruptedException e) {
           
        } 
            return  null ;   
    }
}

// MQ TODO: subscribes to ActiveMQ topic MqConfig.TOPIC at MqConfig.BROKER_URL (see co.wethinkcode.healthsafe.mq.MqConfig)
// MQ TODO: publishes to ActiveMQ queue MqConfig.QUEUE when it detects an equipment failure on one of its wards.
