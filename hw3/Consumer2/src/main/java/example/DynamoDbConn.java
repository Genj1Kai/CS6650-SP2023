package example;

import java.util.HashMap;
import java.util.List;
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
    System.setProperty("aws.accessKeyId", "ASIAUACATOIXU5ATLDOL");
    System.setProperty("aws.secretAccessKey", "SOvzZ+n7K2Qn7S2PQRO5JJEZfUOLq7tGHc6sim9k");
    System.setProperty("aws.sessionToken", "FwoGZXIvYXdzEPv//////////wEaDJ7i5sNiQLi1RB3gpiLLAZGc2o8gcxbK7zUInpLlRfXxG1MC+yk8GbCmdLtRaMgDtMI8gkNq6ux9wZb7jf9H664LwFclp/4mJIFR7uEYXThd0JbZ8OQKAG3QN5wxkIGUiHhSLyKLWtql6EXGSqLBuTkIAwx4s/KGAzLfh5Zu/a+N72DK9xqTnnN7sSeFhOrR5Fot4raSQmktvWBqjgl2b2YvR/MSXeqvtylv5sIXxgDoawYMQGBU+BrbblDuGGDjJSqvdbqV6AsHnMwqajtGkc+NnCt7jpxtgd/iKJeFp6EGMi3hQwNwiT4ib7nX9sTjt5/daCmXwDHAiiarg2wJuR5qO5wyAiDTR+deuA1bsw8=");
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
//    itemValues.put("swipees", AttributeValue.builder().ss(new ArrayList<String>()).build());

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
      List<String> updateSwipees){

    HashMap<String,AttributeValue> itemKey = new HashMap<>();
    itemKey.put(key, AttributeValue.builder()
        .s(keyVal)
        .build());

    HashMap<String, AttributeValueUpdate> updatedValues = new HashMap<>();
    updatedValues.put("swipees", AttributeValueUpdate.builder()
        .value(AttributeValue.builder().ss(updateSwipees).build())
        .action(AttributeAction.PUT)
        .build());

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


