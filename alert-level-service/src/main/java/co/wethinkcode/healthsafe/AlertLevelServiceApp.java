package co.wethinkcode.healthsafe;
import java.util.concurrent.atomic.AtomicInteger;
import io.javalin.Javalin;

public class AlertLevelServiceApp {

    private static final int MIN_LEVEL = 0;
    private static final int MAX_LEVEL = 8;

    // Stores the current emergency level.
    // Starting level is 0.
    private static final AtomicInteger currentLevel = new AtomicInteger(0);

    public static void main(String[] args) {
        Javalin app = Javalin.create().start(7032);

        app.get("/health", ctx -> ctx.result("OK"));

        // TODO (Tracks the hospital Emergency Status (0-8, 8 = full Code Blue).)
        // Add domain endpoints for alert-level-service here.

        app.get("/alert-level", ctx -> {

            int level = currentLevel.get();
            ctx.json(new AlertLevelResponse(level));
        });

        app.put("/alert-level", ctx -> {

            try {
                AlertLevelRequest request = ctx.bodyAsClass(AlertLevelRequest.class);

                int level = request.getLevel();

               
                if (level < MIN_LEVEL || level > MAX_LEVEL) {
                    ctx.status(400);
                    ctx.json(new ErrorResponse(
                            "Alert level must be between 0 and 8"
                    ));
                    return;
                }

                currentLevel.set(level);

                ctx.status(200);
                ctx.json(new AlertLevelResponse(level));

            } catch (Exception e) {

                ctx.status(400);
                ctx.json(new ErrorResponse(
                        "Invalid request. Expected JSON such as {\"level\":5}"
                ));
            }
        });
    }
}
