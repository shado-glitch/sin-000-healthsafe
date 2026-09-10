package co.wethinkcode.healthsafe.mq;

import java.util.function.Consumer;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Queue;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;

/**
 * Consumes equipment failure alerts from the queue.
 *
 * The message is acknowledged only after the alert has been
 * successfully processed.
 */
public final class EquipmentFailureConsumer {

    private static Connection connection;

    private EquipmentFailureConsumer() {
    }

    public static void start(Consumer<String> alertHandler) {

        try {
            ConnectionFactory factory =
                    new ActiveMQConnectionFactory(MqConfig.BROKER_URL);

            connection = factory.createConnection();

            Session session = connection.createSession(
                    Session.CLIENT_ACKNOWLEDGE
            );

            Queue queue = session.createQueue(MqConfig.QUEUE);

            MessageConsumer consumer = session.createConsumer(queue);

            consumer.setMessageListener(message -> {

                try {
                    String body = message.getBody(String.class);

                    // Process the alert first.
                    alertHandler.accept(body);

                    // Acknowledge only after successful processing.
                    message.acknowledge();

                    System.out.println(
                            "Equipment failure alert acknowledged: " + body
                    );

                } catch (Exception e) {

                    // Do NOT acknowledge failed messages.
                    // ActiveMQ can redeliver them.
                    System.err.println(
                            "Could not process equipment failure alert: "
                            + e.getMessage()
                    );
                }
            });

            connection.start();

            System.out.println(
                    "equipment-alert-service listening on "
                    + MqConfig.QUEUE
            );

        } catch (Exception e) {
            throw new RuntimeException(
                    "Could not start equipment failure consumer",
                    e
            );
        }
    }

    public static void stop() {

        try {
            if (connection != null) {
                connection.close();
            }
        } catch (Exception e) {
            System.err.println(
                    "Could not stop equipment failure consumer: "
                    + e.getMessage()
            );
        }
    }
}