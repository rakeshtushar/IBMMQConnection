import com.ibm.mq.jms.MQConnectionFactory;
import com.ibm.mq.jms.MQQueue;
import com.ibm.msg.client.wmq.compat.jms.internal.JMSC;

import javax.jms.*;
import javax.xml.soap.Text;

public class IBMMqProducer{

    public static void main(String[] args) {
        MQConnectionFactory mqQueueConnectionFactoryFactory=new MQConnectionFactory();

        try {
            mqQueueConnectionFactoryFactory.setHostName("localhost");
            mqQueueConnectionFactoryFactory.setPort(1414);
            mqQueueConnectionFactoryFactory.setChannel("DEV.APP");
            mqQueueConnectionFactoryFactory.setQueueManager("QM1000");
           // mqQueueConnectionFactoryFactory.setTransportType(JMSC.MQJMS_TP_CLIENT_MQ_TCPIP);
            Connection connection=mqQueueConnectionFactoryFactory.createConnection();
            connection.start();
            Session session=connection.createSession(false,Session.AUTO_ACKNOWLEDGE);
            Queue queue=new MQQueue("MUNI.Req");
            MessageProducer messageProducer=session.createProducer(queue);
            TextMessage textMessage=session.createTextMessage();
            textMessage.setText("Muni Hello 2");
            messageProducer.send(textMessage);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public void onException(JMSException e) {
        e.printStackTrace();
    }
}

