package org.renci.gate;

public class SiteScoreInfo {

    private Double score;

    private String message;

    public SiteScoreInfo() {
        super();
    }

    public SiteScoreInfo(Double score, String message) {
        super();
        this.score = score;
        this.message = message;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "SiteScoreInfo [score=" + score + ", message=" + message + "]";
    }

}
