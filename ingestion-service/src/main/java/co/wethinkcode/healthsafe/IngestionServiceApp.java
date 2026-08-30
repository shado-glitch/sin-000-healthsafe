package co.wethinkcode.healthsafe;

import co.wethinkcode.healthsafe.controller.WardService;
import co.wethinkcode.healthsafe.service.ReadCsv;
import io.javalin.Javalin;




public class IngestionServiceApp {

    public static void main(String[] args) {

        ReadCsv readCsv = new ReadCsv("wards-outdated.csv");
        WardService wardService = new WardService(readCsv);

        Javalin app = Javalin.create().start(7030);

        app.get("/health", ctx -> ctx.result("OK"));

        // TODO: read and clean src/main/resources/wards-outdated.csv (wards, wings, specialist departments data —
        // trim whitespace, fix casing, normalize dates/booleans) and expose the
        // cleaned records here for the other services to consume.
        
       app.get("/wards", ctx -> ctx.json(wardService.getWards()));
       

    }
}
