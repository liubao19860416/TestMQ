package jms;

import java.util.Date;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.MapMessage;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 同上面的activemq
 * @author Liubao
 * @2014年11月23日
 *
 */
public class Sender {
    
    private static Logger logger = LoggerFactory.getLogger(Sender.class);
    
    public static void main(String[] args) throws Exception {      
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory();      
        Connection connection = connectionFactory.createConnection();      
        connection.start();      
        Session session = connection.createSession(Boolean.TRUE, Session.AUTO_ACKNOWLEDGE);      
        Destination destination = session.createQueue("my-queue");      
        MessageProducer producer = session.createProducer(destination);      
        for(int i=0; i<3; i++) {      
            MapMessage message = session.createMapMessage(); 
            long count = new Date().getTime();
            message.setLong("count", count);      
            Thread.sleep(1000);      
            logger.info("第"+i+"次接发送的消息count为："+count);
            //通过消息生产者发出消息      
            producer.send(message); 
        }      
        session.commit();      
        session.close();      
        connection.close();      
    } 
}
