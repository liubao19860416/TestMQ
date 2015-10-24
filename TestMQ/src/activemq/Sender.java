package activemq;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * ActiveMq发送消息端，生产者
 * @author Liubao
 * @2014年11月23日
 * 
 * 发送端 后面启动
 * 
 * Number Of Pending Messages 等待消费的消息 
 * Messages Enqueued 进入队列的消息
 *  Messages Dequeued 出了队列的消息 ...
 * 
 * 默认密码：admin activemq
 * activemq.username=system
 * activemq.password=manager
 * guest.password=password
 *
 */
public class Sender {
    private static Logger logger = LoggerFactory.getLogger(Sender.class);
    
    //发送消息条数
    private static final int SEND_NUMBER = 5;

    public static void main(String[] args) {
        // ConnectionFactory ：连接工厂，JMS 用它创建连接，JMS 客户端到JMS
        ConnectionFactory connectionFactory; 
        // Provider 的连接
        Connection connection = null; 
        // Session： 一个发送或接收消息的线程
        Session session; 
        // Destination ：消息的目的地;消息发送给谁.
        Destination destination; 
        // MessageProducer：消息发送者
        MessageProducer producer; 
        // TextMessage message;
        // 构造ConnectionFactory实例对象，此处采用ActiveMq的实现jar，默认端口号为61616
        connectionFactory = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_USER,ActiveMQConnection.DEFAULT_PASSWORD, "tcp://localhost:61616");
        try { // 从工厂得到连接对象
            connection = connectionFactory.createConnection();
            // 启动
            connection.start();
            // 获取操作连接
            session = connection.createSession(Boolean.TRUE,Session.AUTO_ACKNOWLEDGE);
            
            // 获取session注意参数值xingbo.xu-queue是一个服务器的queue，须在在ActiveMq的console配置
            destination = session.createQueue("FirstQueue");
            
            // 得到消息生成者【发送者】
            producer = session.createProducer(destination);
            // 设置不持久化，此处学习，实际根据项目决定
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
            //producer.setDeliveryMode(DeliveryMode.PERSISTENT);
            // 构造消息，此处写死，项目就是参数，或者方法获取
            sendMessage(session, producer);
            session.commit();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("发送消息失败。。。",e);
        } finally {
            try {
                if (null != connection){
                    connection.close();
                }
            } catch (Throwable e) {
                e.printStackTrace();
                logger.error("流关闭失败。。。",e);
            }finally{
                connection=null;
            }
        }
    }

    /**
     * 构造消息的方法
     */
    public static void sendMessage(Session session, MessageProducer producer)
            throws Exception {
        for (int i = 1; i <= SEND_NUMBER; i++) {
            TextMessage message = session.createTextMessage("这是ActiveMq 正在发送的消息：" + i);
            logger.info("发送消息为：" + message );
            //每10秒，发送一条消息
            Thread.sleep(10000);
            //logger.info("toString：" + session.toString());
            //logger.info("getAcknowledgeMode：" + session.getAcknowledgeMode());
            // 发送消息到目的地方
            producer.send(message);
        }
    }
}
