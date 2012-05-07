package org.renci.gate;

public class SiteScoreInfo {

    private Integer score;

    private String message;

    public SiteScoreInfo() {
        super();
    }

    public SiteScoreInfo(Integer score, String message) {
        super();
        this.score = score;
        this.message = message;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
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
