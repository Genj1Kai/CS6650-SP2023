package org.example;

import io.swagger.client.ApiException;
import io.swagger.client.ApiResponse;
import io.swagger.client.api.SwipeApi;
import io.swagger.client.model.SwipeDetails;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;

public class RequestProcessor implements Runnable{

//  public static final String BASE_URL = "http://localhost:8080/hw1_servlet_war_exploded/";
  public static final String BASE_URL = "http://35.86.101.55:8080/hw1_servlet_war";
  public static final Integer NUMBER_TOTAL_TRY = 5;
  public static final Integer SWIPE_START = 0;
  public static final Integer SWIPE_END = 1;
  public static final Integer SWIPER_START = 1;
  public static final Integer SWIPER_END = 5000;
  public static final Integer SWIPEE_START = 1;
  public static final Integer SWIPEE_END = 1000000;
  public static final Integer COMMENT_START = 0;
  public static final Integer COMMENT_END = 51;
  public static final Integer COMMENT_LENGTH = 256;

  private CountDownLatch countDownLatch;
  private RequestCounter requestCounter;
  public RequestProcessor(CountDownLatch countDownLatch, RequestCounter requestCounter) {
    this.countDownLatch = countDownLatch;
    this.requestCounter = requestCounter;
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
    SwipeApi apiInstance = new SwipeApi();
    apiInstance.getApiClient().setBasePath(BASE_URL);
    String swipe = swipeGenerator();
    SwipeDetails swipeDetails = swipeDetailsGenerator();
    for (int i = 0; i < Client.NUMBER_REQUESTS/Client.NUMBER_THREADS; i++){
      for (int j = 0; j < NUMBER_TOTAL_TRY; j++) {
        if (j == NUMBER_TOTAL_TRY -1) {
          requestCounter.incrementNumberFailedRequest();
        }
        try {
//          System.out.println(swipeDetails);
//          System.out.println(swipe);

          ApiResponse<Void> res = apiInstance.swipeWithHttpInfo(swipeDetails, swipe);
//          System.out.println(res);
          requestCounter.incrementNumberSuccessfulRequest();
          break;
        } catch (ApiException e) {
          e.printStackTrace();
//          throw new RuntimeException(e);
        }
      }
      countDownLatch.countDown();
    }
  }

  public String swipeGenerator() {
    String[] swipe = new String[]{"left", "right"};
    int swipeIndex = ThreadLocalRandom.current().nextInt(SWIPE_START,SWIPE_END+1);
    return swipe[swipeIndex];
  }

  public SwipeDetails swipeDetailsGenerator() {
    SwipeDetails swipeDetails = new SwipeDetails();
    Integer swiper = ThreadLocalRandom.current().nextInt(SWIPER_START,SWIPER_END+1);
    Integer swipee = ThreadLocalRandom.current().nextInt(SWIPEE_START,SWIPEE_END+1);

    String letters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    StringBuilder stringBuilder = new StringBuilder();
    for(int i =0; i < COMMENT_LENGTH; i++) {
      int commentIndex = ThreadLocalRandom.current().nextInt(COMMENT_START,COMMENT_END+1);
      stringBuilder.append(letters.charAt(commentIndex));
    }
    String comment = stringBuilder.toString();

    swipeDetails.setSwiper(String.valueOf(swiper));
    swipeDetails.setSwipee(String.valueOf(swipee));
    swipeDetails.setComment(comment);
    return swipeDetails;
  }
}
