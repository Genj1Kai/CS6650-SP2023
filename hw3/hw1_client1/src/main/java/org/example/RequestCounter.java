package org.example;

import java.util.concurrent.atomic.AtomicInteger;

public class RequestCounter {
  private AtomicInteger numberSuccessfulRequest;
  private AtomicInteger numberFailedRequest;
  public RequestCounter() {
    this.numberSuccessfulRequest = new AtomicInteger(0);
    this.numberFailedRequest = new AtomicInteger(0);
  }


  public AtomicInteger getNumberSuccessfulRequest() {
    return numberSuccessfulRequest;
  }

  public void incrementNumberSuccessfulRequest() {
    this.numberSuccessfulRequest.addAndGet(1);
  }

  public AtomicInteger getNumberFailedRequest() {
    return numberFailedRequest;
  }

  public void incrementNumberFailedRequest() {
    this.numberFailedRequest.addAndGet(1);
  }
}
