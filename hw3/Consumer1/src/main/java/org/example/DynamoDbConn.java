package org.example;

import java.util.HashMap;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.SystemPropertyCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeAction;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.AttributeValueUpdate;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemResponse;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemResponse;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import java.util.ArrayList;
import java.util.Collections;


public class DynamoDbConn {
  private DynamoDbClient client;
  public DynamoDbConn() {
//    AwsCredentialsProvider credentialsProvider = SystemPropertyCredentialsProvider.create();

    Region region = Region.US_WEST_2;
    client = DynamoDbClient.builder()
        .region(region)
        .build();
  }
  public void putItemInTable(
      String tableName,
      String key,
      String keyVal){

    HashMap<String,AttributeValue> itemValues = new HashMap<>();
    itemValues.put(key, AttributeValue.builder().s(keyVal).build());
    itemValues.put("right", AttributeValue.builder().s("0").build());
    itemValues.put("left", AttributeValue.builder().s("0").build());

    PutItemRequest request = PutItemRequest.builder()
        .tableName(tableName)
        .item(itemValues)
        .build();

    try {
      PutItemResponse response = client.putItem(request);
//      System.out.println(tableName +" was successfully updated. The request id is "+response.responseMetadata().requestId());

    } catch (ResourceNotFoundException e) {
      System.err.format("Error: The Amazon DynamoDB table \"%s\" can't be found.\n", tableName);
      System.err.println("Be sure that it exists and that you've typed its name correctly!");
      System.exit(1);
    } catch (DynamoDbException e) {
      System.err.println(e.getMessage());
      System.exit(1);
    }
  }


  public void updateTableItem(
      String tableName,
      String key,
      String keyVal,
      Boolean isTrue,
      String updateVal){

    HashMap<String,AttributeValue> itemKey = new HashMap<>();
    itemKey.put(key, AttributeValue.builder()
        .s(keyVal)
        .build());

    HashMap<String, AttributeValueUpdate> updatedValues = new HashMap<>();
    if(isTrue) {
      updatedValues.put("right", AttributeValueUpdate.builder()
          .value(AttributeValue.builder().s(updateVal).build())
          .action(AttributeAction.PUT)
          .build());
    } else {
      updatedValues.put("left", AttributeValueUpdate.builder()
          .value(AttributeValue.builder().s(updateVal).build())
          .action(AttributeAction.PUT)
          .build());
    }

    UpdateItemRequest request = UpdateItemRequest.builder()
        .tableName(tableName)
        .key(itemKey)
        .attributeUpdates(updatedValues)
        .build();

    try {
      client.updateItem(request);
    } catch (DynamoDbException e) {
      System.err.println(e.getMessage());
      System.exit(1);
    }
//    System.out.println("The Amazon DynamoDB table was updated!");
  }

  public static DynamoDbConn createDynamoDbConn() {
    return new DynamoDbConn();
  }
}

