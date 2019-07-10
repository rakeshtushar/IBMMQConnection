import org.apache.activemq.command.ActiveMQQueue;

import javax.jms.*;
import java.util.concurrent.TimeUnit;

public class MQSender {

    public static void main(String[] args) {
        MQConnectionImpl mqConnection=new MQConnectionImpl();
        Connection connection=mqConnection.getConnection();
        try {
            connection.start();
            Session session=connection.createSession(false,Session.AUTO_ACKNOWLEDGE);
            Queue queue=new ActiveMQQueue(mqConnection.getQueueName());
            MessageProducer messageProducer=session.createProducer(queue);
            TextMessage textMessage=session.createTextMessage();
            int i=0;
            while(true) {
                TimeUnit.SECONDS.sleep(2);
                textMessage.setText("Muni Message "+i);
                messageProducer.send(textMessage);
                System.out.println("send message: " +i+" "+ textMessage.getText());
                i++;
            }
        } catch (JMSException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }
}
