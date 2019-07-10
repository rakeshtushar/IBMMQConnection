import org.apache.activemq.command.ActiveMQQueue;

import javax.jms.*;

public class MQReciever{

    public static void main(String[] args) {

        MQConnectionImpl mqConnection=new MQConnectionImpl();
        Connection connection=mqConnection.getConnection();

        try {
            Session session=connection.createSession(false,Session.AUTO_ACKNOWLEDGE);
            Queue queue=new ActiveMQQueue(mqConnection.getQueueName());
            MessageConsumer messageConsumer=session.createConsumer(queue);
            messageConsumer.setMessageListener(new MessageListenerImpl());
            connection.start();

            while(true)
            {

            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

}
class MessageListenerImpl implements MessageListener,ExceptionListener
{

    public void onMessage(Message message) {
        TextMessage textMessage=(TextMessage)message;
        try {
            System.out.println("Received message: "+textMessage.getText());
        } catch (JMSException e) {
            e.printStackTrace();
        }

    }

    public void onException(JMSException e) {
        e.printStackTrace();
    }
}


