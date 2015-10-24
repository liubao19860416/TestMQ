package activemq;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ActiveMq接收消息端，消费者
 * @author Liubao
 * @2014年11月23日
 * 
 * 接收端 需要首先启动
 * 
 */
public class Receiver {  
    
    private static Logger logger = LoggerFactory.getLogger(Receiver.class);
    
    public static void main(String[] args) {  
        // ConnectionFactory ：连接工厂，JMS 用它创建连接  
        ConnectionFactory connectionFactory;  
        // Connection ：JMS 客户端到JMS Provider 的连接  
        Connection connection = null;  
        // Session： 一个发送或接收消息的线程  
        Session session;  
        // Destination ：消息的目的地;消息发送给谁.  
        Destination destination;  
        // 消费者，消息接收者  
        MessageConsumer consumer;  
        
        //需要设置连接的ip及端口号等信息，默认端口号为61616
        connectionFactory = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_USER,  
                ActiveMQConnection.DEFAULT_PASSWORD, "tcp://localhost:61616");  
        try {  
            // 构造从工厂得到连接对象  
            connection = connectionFactory.createConnection();  
            // 启动  
            connection.start();  
            // 获取操作连接  
            session = connection.createSession(Boolean.FALSE,Session.AUTO_ACKNOWLEDGE);  
            // 获取session注意参数值xingbo.xu-queue是一个服务器的queue，须在在ActiveMq的console配置  
            destination = session.createQueue("FirstQueue");  
            consumer = session.createConsumer(destination);  
            while (true) {  
                // 设置接收者接收消息的维持时间，这里设定为接收消息维持时间为500s  
                TextMessage message = (TextMessage) consumer.receive(500000);  
                //下面是无设置时间的构造方法
                //message = (TextMessage) consumer.receive();  
                if (null != message) {  
                    logger.info("接收到的消息为："+message.getText());
                } else {  
                    break;  
                }  
            }  
        } catch (Exception e) {  
            logger.error("发送消息失败。。。",e);
            e.printStackTrace();  
        } finally {  
            try {  
                if (null != connection){
                    connection.close();  
                }  
            } catch (Throwable e) {  
                logger.error("发送消息失败。。。",e);
            } finally{
                connection=null;
            } 
        }  
    }  
}  