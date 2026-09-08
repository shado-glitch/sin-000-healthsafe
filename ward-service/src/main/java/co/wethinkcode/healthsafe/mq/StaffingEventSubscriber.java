package co.wethinkcode.healthsafe.mq;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.jms.ConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.Topic;

import org.apache.activemq.ActiveMQConnectionFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Subscribes to "staffing-events-topic" and keeps the most recent
 * StaffingUpdate for each ward in memory.
 *
 * This replaces what would otherwise be a new synchronous HTTP call from
 * ward-service to staffing-service every time someone wants to know a
 * ward's current staffing level. Instead, ward-service learns about changes
 * passively, as they happen.
 *
 * Why a topic (pub/sub, fire-and-forget) fits this case:
 *  - staffing-service doesn't need to know or care who's listening
 *  - we only ever care about the LATEST value per ward, so an occasional
 *    missed message isn't a correctness problem — the next change will
 *    arrive and overwrite it anyway
 *  - other services could subscribe later (e.g. a dashboard) with zero
 *    changes to staffing-service
 * Compare this to stage 4's equipment-failure-queue, where a single
 * failure report must be delivered and processed exactly once — that's a
 * queue (guaranteed, single consumer), not a topic.
 */
public final class StaffingEventSubscriber {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    // wardId -> most recent staffing update received from the topic.
    private static final Map<String, StaffingUpdate> latestByWard = new ConcurrentHashMap<>();

    private static JMSContext context;

    private StaffingEventSubscriber() {
    }

    /** Call once at startup. Keeps a background thread alive listening for messages. */
    public static void start() {
        ConnectionFactory factory = new ActiveMQConnectionFactory(MqConfig.BROKER_URL);

        // Not try-with-resources on purpose: we want this connection to
        // stay open for the whole lifetime of the app, not just one call.
        context = factory.createContext();
        Topic topic = context.createTopic(MqConfig.TOPIC);
        JMSConsumer consumer = context.createConsumer(topic);

        consumer.setMessageListener(message -> {
            try {
                String body = message.getBody(String.class);
                StaffingUpdate update = MAPPER.readValue(body, StaffingUpdate.class);
                latestByWard.put(update.getWardId(), update);

                System.out.println("ward-service received staffing update: " + body);
            } catch (Exception e) {
                // One malformed message should never kill the listener —
                // log it and keep waiting for the next one.
                System.err.println("Could not process staffing event: " + e.getMessage());
            }
        });

        context.start();
        System.out.println("ward-service subscribed to " + MqConfig.TOPIC);
    }

    /** Returns the latest known staffing update for a ward, or null if none has arrived yet. */
    public static StaffingUpdate getLatest(String wardId) {
        if (wardId == null) {
            return null;
        }
        return latestByWard.get(wardId.trim().toUpperCase());
    }

    /** Call on shutdown to close the JMS connection cleanly. */
    public static void stop() {
        if (context != null) {
            context.close();
        }
    }
}
