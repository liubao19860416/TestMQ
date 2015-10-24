package rabbitmq;

import java.io.IOException;
import java.io.Serializable;

import org.apache.commons.lang.SerializationUtils;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;

public class Producer {
	
	private Channel channel;
	
	private String queue;
	
	private String exchange;
	
	public Producer(String queue, String exchange) throws IOException{
		channel = RabbitUtil.getChannel();		
		this.queue = queue;
		this.exchange = exchange;
	}

	public void sendMessage(Serializable object) throws IOException {
		channel.exchangeDeclare("ewallet2", "fanout", true);
	    channel.basicPublish(exchange, queue, MessageProperties.PERSISTENT_TEXT_PLAIN, SerializationUtils.serialize(object));
	}	
}
