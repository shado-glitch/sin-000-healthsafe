package co.wethinkcode.healthsafe;

import co.wethinkcode.healthsafe.service.ReadCsv;
import co.wethinkcode.healthsafe.service.WardService;
import io.javalin.Javalin;




public class IngestionServiceApp {

    public static void main(String[] args) {

        ReadCsv readCsv = new ReadCsv("wards-outdated.csv");
        WardService wardService = new WardService(readCsv);

        Javalin app = Javalin.create().start(7030);

        app.get("/health", ctx -> ctx.result("OK"));
        // Expose cleaned ward records for other services to consume.        
       app.get("/wards", ctx -> ctx.json(wardService.getWards()));
       

    }
}
