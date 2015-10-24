package mqibm;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.mq.MQC;
import com.ibm.mq.MQEnvironment;
import com.ibm.mq.MQException;
import com.ibm.mq.MQGetMessageOptions;
import com.ibm.mq.MQMessage;
import com.ibm.mq.MQPutMessageOptions;
import com.ibm.mq.MQQueue;
import com.ibm.mq.MQQueueManager;

@SuppressWarnings("deprecation")
public class MQMain {
    
    private static Logger logger = LoggerFactory.getLogger(MQMain.class);
    
    public static void main(String[] args) throws Exception {  
        //发送消息
        sendMsg();
        //接收消息
        getMsg();  
    }  
  
    static void getMsg() throws MQException, IOException {  
        MQEnvironment.hostname =  "172.25.1.69";  
        MQEnvironment.port = 1414;  
        MQEnvironment.CCSID = 1381;  
        MQEnvironment.channel = "ch1";  
        MQQueueManager qMgr = new MQQueueManager("QM_APPLE");  
        int openOptions = MQC.MQOO_INPUT_AS_Q_DEF | MQC.MQOO_OUTPUT | MQC.MQOO_INQUIRE;  
        MQQueue queue = qMgr.accessQueue("Q1_Local", openOptions, null, null, null);  
          
        // 要读的队列的消息  
        MQMessage msg = new MQMessage();
        MQGetMessageOptions gmo = new MQGetMessageOptions( );  
        gmo.options = MQC.MQGMO_SYNCPOINT;  
          
        int currDepth = queue.getCurrentDepth();  
        logger.info("queue.getCurrentDepth()："+currDepth);
          
        if (currDepth > 0) {  
            queue.get(msg, gmo);  
            qMgr.commit();  
            qMgr.close();  
            logger.info("获取的消息为："+msg.readStringOfCharLength(msg.getDataLength()));
        }  
    }  
  
    static void sendMsg() throws MQException, IOException {  
        MQEnvironment.hostname =  "172.25.1.69";  
        MQEnvironment.port = 1414;  
        MQEnvironment.CCSID = 1381;  
        MQEnvironment.channel = "ch1";  
        MQQueueManager qMgr = new MQQueueManager("QM_APPLE");  
  
        int openOptions = MQC.MQOO_OUTPUT | MQC.MQOO_FAIL_IF_QUIESCING;  
  
        MQQueue queue = qMgr.accessQueue("Q1_Local", openOptions, null, null, null);  
        MQMessage msg = new MQMessage();// 要写入队列的消息  
        //将消息写入消息对象中  
        msg.writeString("111111111111111111111111"); 
        MQPutMessageOptions pmo = new MQPutMessageOptions();  
        pmo.options = MQC.MQPMO_SYNCPOINT;  
        // 设置消息用不过期  
        msg.expiry = -1; 
        // 将消息放入队列  
        queue.put(msg, pmo);
        qMgr.commit();  
        logger.info("发送的消息为："+msg.readStringOfCharLength(msg.getDataLength()));
    } 
}
