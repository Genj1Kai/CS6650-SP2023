public class StatsResponse {
  private String numLikes;
  private String numDislikes;

  public StatsResponse(String numLikes, String numDislikes) {
    this.numLikes = numLikes;
    this.numDislikes = numDislikes;
  }

  public String getNumLikes() {
    return numLikes;
  }

  public void setNumLikes(String numLikes) {
    this.numLikes = numLikes;
  }

  public String getNumDislikes() {
    return numDislikes;
  }

  public void setNumDislikes(String numDislikes) {
    this.numDislikes = numDislikes;
  }
}
