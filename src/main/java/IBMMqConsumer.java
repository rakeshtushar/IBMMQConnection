import com.ibm.mq.jms.MQConnectionFactory;
import com.ibm.mq.jms.MQQueue;
import com.ibm.msg.client.wmq.compat.jms.internal.JMSC;

import javax.jms.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class IBMMqConsumer {
    static volatile boolean isConnected=false;
    static MQConnectionFactory mqQueueConnectionFactoryFactory;
    static Connection connection;
    static Executor executor= Executors.newScheduledThreadPool(1);
    static volatile boolean isReconnected=false;
    public static void main(String[] args) {


        try {
            init();
            //mqQueueConnectionFactoryFactory.setTransportType(JMSC.MQJMS_TP_CLIENT_MQ_TCPIP);
            create();
            start();


            while (true)
            {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void init()
    {
        try {
            mqQueueConnectionFactoryFactory = new MQConnectionFactory();
            mqQueueConnectionFactoryFactory.setHostName("localhost");
            mqQueueConnectionFactoryFactory.setPort(1414);
            mqQueueConnectionFactoryFactory.setChannel("DEV.Req");
            mqQueueConnectionFactoryFactory.setQueueManager("QM1000");
        } catch (JMSException e) {
            System.out.println("Error while initializig property for connection factory "+e.getMessage());
        }
    }
    public static void create()
    {
        try {
            if(!isConnected) {
                connection = mqQueueConnectionFactoryFactory.createConnection();
                connection.setExceptionListener(new ExceptionListenerImpl());
                Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                Queue queue = new MQQueue("MUNI.Req");
                MessageConsumer messageConsumer = session.createConsumer(queue);
                messageConsumer.setMessageListener(new MessageListenerIbmImpl());
                System.out.println("Successfully created session");

            }
        } catch (JMSException e) {
            System.out.println("Error while creating connection "+e.getMessage());
            if(!isReconnected) {
                reconnect();
            }
        }
    }
    public static void start()
    {
        if(connection!=null && !isConnected)
        {
            try {
                connection.start();
                System.out.println("Successfully started connection");
                isConnected=true;
            } catch (JMSException e) {
                System.out.println("Error while starting connection "+e.getMessage());
                isConnected=false;
            }
        }

    }

    public static void reconnect()
    {
        final Runnable runnable=new Runnable() {
            public void run() {
                isReconnected = true;
                IBMMqConsumer.isConnected = false;
                try {
                    if (connection != null) {
                        IBMMqConsumer.connection.close();
                    }
                } catch (JMSException e1) {
                    System.out.println("Error while closing connection due to exception " + e1.getMessage());
                }
                IBMMqConsumer.connection = null;
                while (!IBMMqConsumer.isConnected) {
                    System.out.println("Reconnecting........");
                    try {
                        TimeUnit.SECONDS.sleep(2);
                        IBMMqConsumer.create();
                        IBMMqConsumer.start();
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }

                }
            }
        } ;

        Executor executor=Executors.newScheduledThreadPool(1);
        executor.execute(runnable);
    }
}
class MessageListenerIbmImpl implements MessageListener
{

    public void onMessage(Message message) {
        TextMessage textMessage=(TextMessage)message;
        try {
            System.out.println("Received message: "+textMessage.getText());
        } catch (JMSException e) {
            System.out.println("Error while receiving message "+e.getMessage());
        }

    }



}

class ExceptionListenerImpl implements ExceptionListener
{

    public void onException(JMSException e) {
        System.out.println("Under on exception "+e.getMessage());
        IBMMqConsumer.isReconnected=false;
        IBMMqConsumer.reconnect();
        /*IBMMqConsumer.isConnected=false;
        try {
            IBMMqConsumer.connection.close();
        } catch (JMSException e1) {
            System.out.println("Error while closing connection due to exception "+e.getMessage());
        }
        IBMMqConsumer.connection=null;
        while(!IBMMqConsumer.isConnected)
        {
            System.out.println("Reconnecting........");
            try {
                TimeUnit.SECONDS.sleep(2);
                IBMMqConsumer.create();
                IBMMqConsumer.start();
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }

        }*/
    }
}
