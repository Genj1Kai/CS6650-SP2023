package org.example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Client {

//    public static final String BASE_URL = "http://localhost:8080/hw2_servlet_war_exploded";
  public static final String BASE_URL = "http://35.93.143.230:8080/hw2-servlet_war";
  public static final Integer NUMBER_THREADS = 200;
  public static final Integer NUMBER_REQUESTS = 500000;

  private static ArrayBlockingQueue<Long> getLatency = new ArrayBlockingQueue<>(50000);
  public static void main(String[] args) throws InterruptedException {
    CountDownLatch countDownLatch = new CountDownLatch(NUMBER_REQUESTS);
    RequestCounter requestCounter = new RequestCounter();
    ExecutorService executorService = Executors.newFixedThreadPool(NUMBER_THREADS);

    long startTime = System.currentTimeMillis();
    for (int i=0; i < NUMBER_THREADS; i++) {
      RequestProcessor requestProcessor = new RequestProcessor(countDownLatch,requestCounter);
      executorService.execute(requestProcessor);
    }

    Thread getThread = new Thread(new GetThread(getLatency));
    getThread.start();
    getThread.join();

    countDownLatch.await();
    executorService.shutdown();

    List<Long> resultList = RecordWriter.analyzeResponseTime();

    long endTime = System.currentTimeMillis();
    long totalTime = endTime - startTime;


    List<Long> getLatencyList = new ArrayList<>();
    getLatency.drainTo(getLatencyList);
    int successfulMatchesCount = Integer.parseInt(
        String.valueOf(GetThread.getNumberSuccessfulMatches()));
    int successfulStatsCount = Integer.parseInt(
        String.valueOf(GetThread.getNumberSuccessfulStats()));
    Long minLatency = 0L;
    Long maxLatency = 0L;
    Long meanLatency = 0L;
    if (getLatencyList.size() != 0) {
      minLatency = Collections.min(getLatencyList);
      maxLatency = Collections.max(getLatencyList);
//      meanLatency = 0L;
      for (Long latency : getLatencyList) {
        meanLatency += latency;
      }
      meanLatency /= getLatencyList.size();
    }

    System.out.println("Finished!");
    System.out.println("Number of successful requests: " + requestCounter.getNumberSuccessfulRequest());
    System.out.println("Number of failed requests: " + requestCounter.getNumberFailedRequest());
    System.out.println("Total wall time: " + totalTime + " millisecond");
    System.out.println("Throughput: " + (int)(Integer.parseInt(
        String.valueOf(requestCounter.getNumberSuccessfulRequest())) / (double)(totalTime / 1000)) + " requests/second");

    System.out.println("Mean response time: " + resultList.get(0) + " millisecond");
    System.out.println("Median response time: " + resultList.get(1) + " millisecond");
    System.out.println("P99 (99th percentile) response time: " + resultList.get(2) + " millisecond");
    System.out.println("Min response time: " + resultList.get(3) + " millisecond");
    System.out.println("Max response time: " + resultList.get(4) + " millisecond");

    System.out.println("Number of matches GET requests: " + successfulMatchesCount);
    System.out.println("Number of stats GET requests: " + successfulStatsCount);
    System.out.println("GET request min latency: " + minLatency + " millisecond");
    System.out.println("GET request max latency: " + maxLatency + " millisecond");
    System.out.println("GET request mean latency: " + meanLatency + " millisecond");

    System.exit(0);
  }

}
