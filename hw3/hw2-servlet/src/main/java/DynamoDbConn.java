import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.w3c.dom.Attr;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

public class DynamoDbConn {

  private DynamoDbClient client;
//  private DynamoDbEnhancedClient enhancedClient;
  private HashMap<String, TableSchema> tableSchemas;

  public static final int STATS_PATTERN = 3;
  public static final int MATCHES_PATTERN = 2;
  public DynamoDbConn() {
    System.setProperty("aws.accessKeyId", "ASIAUACATOIXU5ATLDOL");
    System.setProperty("aws.secretAccessKey", "SOvzZ+n7K2Qn7S2PQRO5JJEZfUOLq7tGHc6sim9k");
    System.setProperty("aws.sessionToken", "FwoGZXIvYXdzEPv//////////wEaDJ7i5sNiQLi1RB3gpiLLAZGc2o8gcxbK7zUInpLlRfXxG1MC+yk8GbCmdLtRaMgDtMI8gkNq6ux9wZb7jf9H664LwFclp/4mJIFR7uEYXThd0JbZ8OQKAG3QN5wxkIGUiHhSLyKLWtql6EXGSqLBuTkIAwx4s/KGAzLfh5Zu/a+N72DK9xqTnnN7sSeFhOrR5Fot4raSQmktvWBqjgl2b2YvR/MSXeqvtylv5sIXxgDoawYMQGBU+BrbblDuGGDjJSqvdbqV6AsHnMwqajtGkc+NnCt7jpxtgd/iKJeFp6EGMi3hQwNwiT4ib7nX9sTjt5/daCmXwDHAiiarg2wJuR5qO5wyAiDTR+deuA1bsw8=");
    Region region = Region.US_WEST_2;
    client = DynamoDbClient.builder()
        .region(region)
        .build();
  }

  public MatchesResponse queryTableMatches(String tableName, String partitionKeyName, String partitionKeyVal, String partitionAlias) {

    // Set up an alias for the partition key name in case it's a reserved word.
    HashMap<String,String> attrNameAlias = new HashMap<String,String>();
    attrNameAlias.put(partitionAlias, partitionKeyName);

    // Set up mapping of the partition name with the value.
    HashMap<String, AttributeValue> attrValues = new HashMap<>();

    attrValues.put(":"+partitionKeyName, AttributeValue.builder()
        .s(partitionKeyVal)
        .build());

    QueryRequest queryReq = QueryRequest.builder()
        .tableName(tableName)
        .keyConditionExpression(partitionAlias + " = :" + partitionKeyName)
        .expressionAttributeNames(attrNameAlias)
        .expressionAttributeValues(attrValues)
        .build();

    try {
      QueryResponse response = client.query(queryReq);
//      System.out.println("############");
//      System.out.println(response.items());
//      System.out.println(response.items().get(0));
      Map<String, AttributeValue> res = response.items().get(0);


      String matchedVal = String.valueOf(res.get("swipees"));
      matchedVal = matchedVal.substring(18,matchedVal.length()-1);
//      System.out.println(matchedVal);
      MatchesResponse matchesResponse = new MatchesResponse(matchedVal);

//      System.out.println(res.get("Id"));

      return matchesResponse;

    } catch (DynamoDbException e) {
      System.err.println(e.getMessage());
      System.exit(1);
    }
    return null;
  }

  public StatsResponse queryTableStats(String tableName, String partitionKeyName, String partitionKeyVal, String partitionAlias) {

    // Set up an alias for the partition key name in case it's a reserved word.
    HashMap<String,String> attrNameAlias = new HashMap<String,String>();
    attrNameAlias.put(partitionAlias, partitionKeyName);

    // Set up mapping of the partition name with the value.
    HashMap<String, AttributeValue> attrValues = new HashMap<>();

    attrValues.put(":"+partitionKeyName, AttributeValue.builder()
        .s(partitionKeyVal)
        .build());

    QueryRequest queryReq = QueryRequest.builder()
        .tableName(tableName)
        .keyConditionExpression(partitionAlias + " = :" + partitionKeyName)
        .expressionAttributeNames(attrNameAlias)
        .expressionAttributeValues(attrValues)
        .build();

    try {
      QueryResponse response = client.query(queryReq);
//      System.out.println("############");
//      System.out.println(response.items());
//      System.out.println(response.items().get(0));
      Map<String, AttributeValue> res = response.items().get(0);

      String likeVal = String.valueOf(res.get("right"));
      likeVal = likeVal.substring(17,likeVal.length()-1);
//      System.out.println(likeVal);
      String dislikeVal = String.valueOf(res.get("left"));
      dislikeVal = dislikeVal.substring(17,dislikeVal.length()-1);
//      System.out.println(dislikeVal);
      StatsResponse statsResponse = new StatsResponse(likeVal, dislikeVal);

//      System.out.println(res.get("Id"));

      return statsResponse;

    } catch (DynamoDbException e) {
      System.err.println(e.getMessage());
      System.exit(1);
    }
    return null;
  }

  public static DynamoDbConn createDynamoDbConn() {
    return new DynamoDbConn();
  }

}
