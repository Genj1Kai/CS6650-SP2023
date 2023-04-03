package example;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConsumerThread implements Runnable{
  private final static Integer SWIPE = 0;
  private final static Integer SWIPER = 1;
  private final static Integer SWIPEE = 2;
  private final static Integer COMMENT = 3;
  private DynamoDbConn dynamoDbConn;

  private ConcurrentHashMap<String, List<String>> record;
  private Connection connection;

  public ConsumerThread(ConcurrentHashMap<String, List<String>> record, Connection connection) {
    this.record = record;
    this.connection = connection;
    this.dynamoDbConn = DynamoDbConn.createDynamoDbConn();
  }

  /**
   * When an object implementing interface <code>Runnable</code> is used to create a thread,
   * starting the thread causes the object's
   * <code>run</code> method to be called in that separately executing
   * thread.
   * <p>
   * The general contract of the method <code>run</code> is that it may take any action whatsoever.
   *
   * @see Thread#run()
   */
  @Override
  public void run() {
    try {
      final Channel channel = connection.createChannel();
      channel.queueDeclare(RecvMT.QUEUE_NAME, true, false, false, null);
      // max one message per receiver
      channel.basicQos(1);
//      System.out.println(" [*] Thread waiting for messages. To exit press CTRL+C");
      DeliverCallback deliverCallback = (consumerTag, delivery) -> {
        String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
        channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
//        System.out.println( "Callback thread ID = " + Thread.currentThread().getId() + " Received '" + message + "'");

        String[] swipeDetails = message.split("/");
        if(!record.containsKey(swipeDetails[SWIPER])) {
          record.put(swipeDetails[SWIPER], new ArrayList<>());
          dynamoDbConn.putItemInTable("SwipeMatch", "Id", swipeDetails[SWIPER]);
        }

        if(swipeDetails[SWIPE].equals("right") && record.get(swipeDetails[SWIPER]).size() < 10 && !record.get(swipeDetails[SWIPER]).contains(swipeDetails[SWIPEE])) {
          record.get(swipeDetails[SWIPER]).add(swipeDetails[SWIPEE]);
          dynamoDbConn.updateTableItem("SwipeMatch", "Id", swipeDetails[SWIPER],
              record.get(swipeDetails[SWIPER]));
        }

//        System.out.println("Thread finishing processing!");
      };
      // process messages
      channel.basicConsume(RecvMT.QUEUE_NAME, false, deliverCallback, consumerTag -> { });

    } catch (IOException e) {
      throw new RuntimeException(e);
    }

  }
}
