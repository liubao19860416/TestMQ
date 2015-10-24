package mqibm;

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
public class MessageByMQ{
    
    private static Logger logger = LoggerFactory.getLogger(MQMain.class);
    
     //定义队列管理器和队列的名称
     private static String qmName; 
     private static String qName;
     private static MQQueueManager qMgr;
     
     static{
         //设置环境:
         //MQEnvironment中包含控制MQQueueManager对象中的环境的构成的静态变量，
         //MQEnvironment的值的设定会在MQQueueManager的构造函数加载的时候起作用，
         //因此必须在建立MQQueueManager对象之前设定MQEnvironment中的值.
         MQEnvironment.hostname="10.24.1.180";          //MQ服务器的IP地址      
         MQEnvironment.channel="S_FENGLB";              //服务器连接的通道
         MQEnvironment.CCSID=1381;                      //服务器MQ服务使用的编码1381代表GBK、1208代表UTF(Coded Character Set Identifier:CCSID)
         MQEnvironment.port=1414;                       //MQ端口
         qmName = "QM_FENGLB";                          //MQ的队列管理器名称
         qName = "testQ";                               //MQ远程队列的名称
         try {
             //定义并初始化队列管理器对象并连接 
             //MQQueueManager可以被多线程共享，但是从MQ获取信息的时候是同步的，任何时候只有一个线程可以和MQ通信。
            qMgr = new MQQueueManager(qmName);
            logger.error("初始化环境成功。。。");
        } catch (MQException e) {
            logger.error("初使化MQ出错。。。",e);
            e.printStackTrace();
        } 
     }
     
     /**
      * 往MQ发送消息
      */
     public static int sendMessage(String message){
         int result=0;
         try{   
             //设置将要连接的队列属性
             // Note. The MQC interface defines all the constants used by the
             //WebSphere MQ Java programming interface 
             //(except for completion code constants and error code constants).
             //MQOO_INPUT_AS_Q_DEF:Open the queue to get messages using the queue-defined default.
             //MQOO_OUTPUT:Open the queue to put messages.
             /*目标为远程队列，所有这里不可以用MQOO_INPUT_AS_Q_DEF属性*/
             //int openOptions = MQC.MQOO_INPUT_AS_Q_DEF | MQC.MQOO_OUTPUT;
             /*以下选项可适合远程队列与本地队列*/
             int openOptions = MQC.MQOO_OUTPUT | MQC.MQOO_FAIL_IF_QUIESCING;
             //连接队列 
             //MQQueue provides inquire, set, put and get operations for WebSphere MQ queues. 
             //The inquire and set capabilities are inherited from MQManagedObject. 
             /*关闭了就重新打开*/
            if(qMgr==null || !qMgr.isConnected()){
                qMgr = new MQQueueManager(qmName);
            }
             MQQueue queue = qMgr.accessQueue(qName, openOptions);          
             //定义一个简单的消息
             MQMessage putMessage = new MQMessage(); 
             //将数据放入消息缓冲区
             putMessage.writeUTF(message);  
             //设置写入消息的属性（默认属性）
             MQPutMessageOptions pmo = new MQPutMessageOptions();           
             //将消息写入队列 
             queue.put(putMessage,pmo); 
             queue.close();
         }catch (MQException e) { 
             e.printStackTrace();
             logger.error("A WebSphere MQ error occurred : Completion code " 
                     + e.completionCode + " Reason code " + e.reasonCode,e);
         }catch (Exception e) { 
             logger.error("An error occurred whilst writing to the message buffer: " , e);
         }finally{
             try {
                 if(qMgr.isConnected()||qMgr.isOpen()){
                     qMgr.disconnect();
                 }
            } catch (MQException e) {
                e.printStackTrace();
                logger.error(e.getMessage(),e);
            }
          }
         return result;
     }
     
     
     /**
      * 从队列中去获取消息，如果队列中没有消息，就会发生异常，不过没有关系，
      * 有TRY...CATCH，如果是第三方程序调用方法，如果无返回则说明无消息
      * 第三方可以将该方法放于一个无限循环的while(true){...}之中，
      * 不需要设置等待，因为在该方法内部在没有消息的时候会自动等待。
      */
     public static String getMessage(){
         String message=null;
         try{            
             //设置将要连接的队列属性
             // Note. The MQC interface defines all the constants used by 
             //the WebSphere MQ Java programming interface 
             //(except for completion code constants and error code constants).
             //MQOO_INPUT_AS_Q_DEF:Open the queue to get messages using the queue-defined default.
             //MQOO_OUTPUT:Open the queue to put messages.
             int openOptions = MQC.MQOO_INPUT_AS_Q_DEF | MQC.MQOO_OUTPUT;
             MQMessage retrieve = new MQMessage();
             //设置取出消息的属性（默认属性）
             //Set the put message options.（设置放置消息选项） 
             MQGetMessageOptions gmo = new MQGetMessageOptions(); 
             //Get messages under sync point control（在同步点控制下获取消息） 
             gmo.options = gmo.options + MQC.MQGMO_SYNCPOINT;
             // Wait if no messages on the Queue（如果在队列上没有消息则等待） 
             gmo.options = gmo.options + MQC.MQGMO_WAIT;  
             // Fail if Qeue Manager Quiescing（如果队列管理器停顿则失败） 
             gmo.options = gmo.options + MQC.MQGMO_FAIL_IF_QUIESCING;
             // Sets the time limit for the wait.（设置等待的毫秒时间限制） 
             gmo.waitInterval = 1000 ;  
             /*关闭了就重新打开*/
            if(qMgr==null || !qMgr.isConnected()){
                qMgr = new MQQueueManager(qmName);
            }
             MQQueue queue = qMgr.accessQueue(qName, openOptions); 
             // 从队列中取出消息
             queue.get(retrieve, gmo);
             message = retrieve.readUTF();  
             logger.info("The message is: " + message);
             queue.close();
         }catch (MQException ex) { 
             logger.info("A WebSphere MQ error occurred : Completion code " 
                     + ex.completionCode + " Reason code " + ex.reasonCode);
         }catch (Exception e){
             e.printStackTrace();
             logger.error(e.getMessage(),e);
         }finally{
             try {
                 if(qMgr.isConnected()||qMgr.isOpen()){
                     qMgr.disconnect();
                 }
            } catch (MQException e) {
                e.printStackTrace();
                logger.error(e.getMessage(),e);
            }
         }
         return message;
     }
     
     
     //main测试方法
     public static void main(String args[]) {
         /*下面两个方法可同时使用，也可以单独使用*/
         sendMessage("this is a test");
         getMessage();
     }
}

