package rabbitmq;

import java.io.IOException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class RabbitUtil {

	private static Connection connection;
	private static ConnectionFactory factory = new ConnectionFactory();
	
	static {
		try {
	        factory.setHost("172.17.253.217");
	        
	        factory.setPort(6666);
	        
//	        factory.setUsername("ewallet");
//	        
//	        factory.setPassword("1234qwer");
//	        
//	        factory.setVirtualHost("ewallet");
			
	        connection = factory.newConnection();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static Channel getChannel() throws IOException {
			return connection.createChannel();
	}
}
