package rabbitmq;


public class Main {
	
	public Main() throws Exception{
		
		MyConsumer consumer = new MyConsumer("log2");
		Thread consumerThread = new Thread(consumer);
		consumerThread.start();
		
//		Producer producer = new Producer("log2", "ewallet2");
//		
//		for (int i = 0; i < 1000000000; i++) {
//			HashMap message = new HashMap();
//			message.put("message number", i);
//			producer.sendMessage(message);
//			System.out.println("Producer Number "+ i +" sent.");
//		}
	}
	
	public static void main(String[] args) throws Exception{
	  new Main();
	}
}