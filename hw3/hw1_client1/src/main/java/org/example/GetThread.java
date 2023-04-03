package org.example;

import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.ApiResponse;
import io.swagger.client.api.MatchesApi;
import io.swagger.client.api.StatsApi;
import io.swagger.client.model.MatchStats;
import io.swagger.client.model.Matches;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class GetThread implements Runnable {

  public static final Integer NUM_OF_GET_REQUESTS = 5;
  public static final Integer SWIPER_START = 1;
  public static final Integer SWIPER_END = 500;

  private static final AtomicInteger numberSuccessfulMatches = new AtomicInteger(0);
  private static final AtomicInteger numberSuccessfulStats = new AtomicInteger(0);

  private ArrayBlockingQueue<Long> getLatency;

  public GetThread(ArrayBlockingQueue<Long> getLatency) {
    this.getLatency = getLatency;
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
    MatchesApi matchApi = new MatchesApi();
    ApiClient apiClientMatches;
    apiClientMatches = new ApiClient();
    matchApi = new MatchesApi(apiClientMatches);
    apiClientMatches.setBasePath(Client.BASE_URL);

    StatsApi statsApi = new StatsApi();
    ApiClient apiClientStats;
    apiClientStats = new ApiClient();
    statsApi = new StatsApi(apiClientStats);
    apiClientStats.setBasePath(Client.BASE_URL);

    int requestCount = 0;

    while(true){
      try {
        for (int i =0; i < NUM_OF_GET_REQUESTS; i++){
          String swiperId = String.valueOf(
              ThreadLocalRandom.current().nextInt(SWIPER_START,SWIPER_END+1));
          long threadStartTime = System.currentTimeMillis();
          try {
            if (requestCount % 2 == 0) {
              ApiResponse<Matches> responseMatch = matchApi.matchesWithHttpInfo(swiperId);
              if (responseMatch.getStatusCode() == 200) {
                numberSuccessfulMatches.incrementAndGet();
              }
            } else {
              ApiResponse<MatchStats> responseStat = statsApi.matchStatsWithHttpInfo(swiperId);
              if (responseStat.getStatusCode() == 200) {
                numberSuccessfulStats.incrementAndGet();
              }
            }
          } catch (ApiException e){
            e.printStackTrace();
          }
          requestCount ++;
          long threadEndTime = System.currentTimeMillis();
          getLatency.add(threadEndTime - threadStartTime);
        }
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    }

  }
  public static AtomicInteger getNumberSuccessfulMatches() {
    return numberSuccessfulMatches;
  }
  public static AtomicInteger getNumberSuccessfulStats() {
    return numberSuccessfulStats;
  }
}



