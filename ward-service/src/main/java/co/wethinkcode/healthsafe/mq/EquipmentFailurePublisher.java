package co.wethinkcode.healthsafe.mq;

import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.JMSContext;
import javax.jms.Queue;

import org.apache.activemq.ActiveMQConnectionFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Publishes equipment failure alerts to the durable ActiveMQ queue.
 */
public final class EquipmentFailurePublisher {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private EquipmentFailurePublisher() {
    }

    public static void publish(String wardId, String equipment, String message) {
        ConnectionFactory factory = new ActiveMQConnectionFactory(MqConfig.BROKER_URL);

        try (JMSContext context = factory.createContext()) {
            Queue queue = context.createQueue(MqConfig.QUEUE);

            Map<String, String> alert = new LinkedHashMap<>();
            alert.put("wardId", wardId);
            alert.put("equipment", equipment);
            alert.put("message", message);

            String body = toJson(alert);

            // PERSISTENT means the broker stores the message instead of treating
            // it as a temporary message. It can therefore survive a consumer
            // that is temporarily stopped.
            context.createProducer()
                    .setDeliveryMode(DeliveryMode.PERSISTENT)
                    .send(queue, body);

            System.out.println("Published equipment failure: " + body);
        }
    }

    private static String toJson(Map<String, String> alert) {
        try {
            return MAPPER.writeValueAsString(alert);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Could not create equipment alert", e);
        }
    }
}
