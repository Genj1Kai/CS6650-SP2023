import java.util.List;

public class MatchesResponse{
  private List<String> matchList;
  public MatchesResponse(String matchList) {
    this.matchList = List.of(matchList.substring(1, matchList.length() - 1).split(", "));
  }

  public List<String> getMatchList() {
    return matchList;
  }

  public void setMatchList(List<String> matchList) {
    this.matchList = matchList;
  }
}
