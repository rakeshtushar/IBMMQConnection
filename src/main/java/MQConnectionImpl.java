import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;

public class MQConnectionImpl implements ExceptionListener {

    private static String url = ActiveMQConnection.DEFAULT_BROKER_URL;

    private static String queueName = "MUNI_CORP";

    private Connection connection;
    public MQConnectionImpl()
    {
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
        try {
             connection = connectionFactory.createConnection();
             connection.setExceptionListener(this);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
    public Connection getConnection()
    {
        return connection;
    }
    public String getQueueName()
    {
        return queueName;
    }


    public void onException(JMSException e) {
        e.printStackTrace();
    }
}
