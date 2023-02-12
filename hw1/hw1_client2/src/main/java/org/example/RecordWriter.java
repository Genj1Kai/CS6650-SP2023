package org.example;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class RecordWriter {

  private static final String CSV_HEADER = "start_time,request_type,latency,response_code\n";
  private static final String CSV_FILE = "record.csv";
  private static ConcurrentLinkedQueue<Record> queue = new ConcurrentLinkedQueue<>();

  public static void addRecord(Record record) {
    queue.add(record);
  }

  public static void writeRecordsToCSV() {
    try (FileWriter writer = new FileWriter(CSV_FILE)) {
      writer.append(CSV_HEADER);
      while (!queue.isEmpty()) {
        Record record = queue.poll();
        writer.append(String.valueOf(record.getStartTime()))
            .append(",")
            .append(record.getRequestType())
            .append(",")
            .append(String.valueOf(record.getLatency()))
            .append(",")
            .append(String.valueOf(record.getResponseCode()))
            .append("\n");
      }
    } catch (IOException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }

  public static List<Long> analyzeResponseTime() {
    List<Long> responseTimeList = new ArrayList<>();
    List<Long> resultList = new ArrayList<>();
    for (Record record : queue) {
      responseTimeList.add(record.getLatency());
    }

    long sum = 0;
    for (long responseTime : responseTimeList) {
      sum += responseTime;
    }
    resultList.add(sum / responseTimeList.size());


    Collections.sort(responseTimeList);
    int middle = responseTimeList.size() / 2;
    if (responseTimeList.size() % 2 == 1) {
      resultList.add(responseTimeList.get(middle));
    } else {
      resultList.add((responseTimeList.get(middle - 1) + responseTimeList.get(middle)) / 2);
    }

    int p99Index = (int) Math.ceil(responseTimeList.size() * 0.99) - 1;
    resultList.add(responseTimeList.get(p99Index));

    resultList.add(Collections.min(responseTimeList));
    resultList.add(Collections.max(responseTimeList));

    return resultList;
  }
}
