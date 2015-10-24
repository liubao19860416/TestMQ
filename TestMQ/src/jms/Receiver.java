package jms;

import java.util.Date;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.MessageConsumer;
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
public class Receiver {
    
    private static Logger logger = LoggerFactory.getLogger(Receiver.class);
    
    //main测试方法
    public static void main(String[] args) {      
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(); 
        Session session=null;
        Connection connection=null;
        try {
            connection = connectionFactory.createConnection();      
            connection.start();      
            session = connection.createSession(Boolean.TRUE, Session.AUTO_ACKNOWLEDGE);      
            Destination destination = session.createQueue("my-queue");      
            MessageConsumer consumer = session.createConsumer(destination);      
            int i=0;      
            while(i<3) {      
                i++;      
                MapMessage message = (MapMessage) consumer.receive(100000);      
                logger.info("第"+i+"次接收到的消息为："+new Date(message.getLong("count")));
                session.commit();      
            }      
           
        } catch (JMSException e) {
            e.printStackTrace();
            logger.error("接受消息失败。。。",e);
        }finally{
            try {
                if(session!=null){
                    session.close();
                }
                if(connection!=null){
                    connection.close();
                }
            } catch (JMSException e) {
                e.printStackTrace();
            }finally{
                session=null;
                connection=null;
            }      
        }      
    }  
}
