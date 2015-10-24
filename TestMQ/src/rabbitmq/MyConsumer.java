package rabbitmq;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.SerializationUtils;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer;


public class MyConsumer implements Runnable {
	
	private Channel channel;
	
	private String queue;
	
	public MyConsumer(String queue) throws IOException{
		channel = RabbitUtil.getChannel();		
		this.queue = queue;
	}
	
	public void run() {
		try {
			QueueingConsumer consumer = new QueueingConsumer(channel);
			channel.exchangeDeclare("ewallet2", "fanout", true);
			channel.queueDeclare(queue, true, false, false, null);
			channel.queueBind(queue, "ewallet2", "");
			channel.basicQos(1);
			channel.basicConsume(queue, false, consumer);
			while (true) {
				QueueingConsumer.Delivery delivery = consumer.nextDelivery();
				Map map = (HashMap)SerializationUtils.deserialize(delivery.getBody());
				System.out.println("Consumer Number "+ map.get("message number") + " received.");
			    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

//	public void handleDelivery(String consumerTag, Envelope env,
//			BasicProperties props, byte[] body) throws IOException {
//		Map map = (HashMap)SerializationUtils.deserialize(body);
//	    System.out.println("Consumer Number "+ map.get("message number") + " received.");
//	}
//	
//	public void handleConsumeOk(String consumerTag) {}
//	public void handleCancel(String consumerTag) {}
//	public void handleCancelOk(String consumerTag) {}
//	public void handleRecoverOk(String consumerTag) {}
//	public void handleShutdownSignal(String consumerTag, ShutdownSignalException arg1) {}
}
