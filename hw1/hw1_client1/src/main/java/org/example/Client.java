package org.example;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Client {
  public static final Integer NUMBER_THREADS = 100;
  public static final Integer NUMBER_REQUESTS = 500000;
  public static void main(String[] args) throws InterruptedException {
    CountDownLatch countDownLatch = new CountDownLatch(NUMBER_REQUESTS);
    RequestCounter requestCounter = new RequestCounter();
    ExecutorService executorService = Executors.newFixedThreadPool(NUMBER_THREADS);

    long startTime = System.currentTimeMillis();
    for (int i=0; i < NUMBER_THREADS; i++) {
      RequestProcessor requestProcessor = new RequestProcessor(countDownLatch,requestCounter);
      executorService.execute(requestProcessor);
    }
    countDownLatch.await();
    executorService.shutdown();

    long endTime = System.currentTimeMillis();
    long totalTime = endTime - startTime;

    System.out.println("Finished!");
    System.out.println("Number of successful requests: " + requestCounter.getNumberSuccessfulRequest());
    System.out.println("Number of failed requests: " + requestCounter.getNumberFailedRequest());
    System.out.println("Total wall time: " + totalTime + " millisecond");
    System.out.println("Throughput: " + (int)(Integer.parseInt(
        String.valueOf(requestCounter.getNumberSuccessfulRequest())) / (double)(totalTime / 1000)) + " requests/second");
  }
}
