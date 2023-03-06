package example;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class RecvMT {

  public final static String QUEUE_NAME = "Consumer2";
  private final static Integer NUMBER_THREADS = 250;
  private static ConcurrentHashMap<String, List<String>> record = new ConcurrentHashMap<>();

  public static void main(String[] argv) throws Exception {
    ConnectionFactory factory = new ConnectionFactory();
//    factory.setHost("localhost");
    factory.setHost("54.71.21.38");
    factory.setUsername("thebigrabbit"); //hidden
    factory.setPassword("thebigrabbit"); //hidden
    factory.setVirtualHost("cherry_broker");

    final Connection connection = factory.newConnection();

    // start threads and block to receive messages
    ExecutorService executorService = Executors.newFixedThreadPool(NUMBER_THREADS);

    long startTime = System.currentTimeMillis();
    for (int i=0; i < NUMBER_THREADS; i++) {
      ConsumerThread consumerThread = new ConsumerThread(record, connection);
      executorService.execute(consumerThread);
    }
    executorService.awaitTermination(500, TimeUnit.SECONDS);
    executorService.shutdown();

    System.out.println("Finish processing!");
  }
}
