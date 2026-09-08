
package co.wethinkcode.healthsafe.mq;

import javax.jms.ConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.Topic;

import org.apache.activemq.ActiveMQConnectionFactory;

public class StaffingEventPublisher {

    public static void publish(String message) {

        ConnectionFactory factory =new ActiveMQConnectionFactory(MqConfig.BROKER_URL);

        try (JMSContext context = factory.createContext()) {

            Topic topic =context.createTopic(MqConfig.TOPIC);

            context.createProducer().send(topic, message);

            System.out.println( "Published staffing event: " + message);
        }
    }
}