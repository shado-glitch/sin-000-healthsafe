package co.wethinkcode.healthsafe;

import co.wethinkcode.healthsafe.controller.WardService;
import co.wethinkcode.healthsafe.service.ReadCsv;
import io.javalin.Javalin;
import com.fasterxml.jackson.databind.ObjectMapper;




public class IngestionServiceApp {

    public static void main(String[] args) {

        WardService twst = new WardService(new ReadCsv("wards-outdated.csv"));
        ObjectMapper objectMapper = new ObjectMapper();

        Javalin app = Javalin.create().start(7030);

        app.get("/health", ctx -> ctx.result("OK"));

        // TODO: read and clean src/main/resources/wards-outdated.csv (wards, wings, specialist departments data —
        // trim whitespace, fix casing, normalize dates/booleans) and expose the
        // cleaned records here for the other services to consume.
        
        app.get("/wards", ctx -> {

            try{
                String json =
                        objectMapper.writeValueAsString(
                                wardService.getWards()
                        );

                ctx.contentType("application/json");
                ctx.result(json);
                }catch(Exception e){
                    ctx.status(500);
                    ctx.result("could not create Json");                }
        });

    }
}
