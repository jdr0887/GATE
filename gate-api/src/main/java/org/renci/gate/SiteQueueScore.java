package org.renci.gate;

public class SiteQueueScore {

    private String siteName;

    private String queueName;

    private Integer score = 0;

    private String message;

    public SiteQueueScore() {
        super();
    }

    public SiteQueueScore(String siteName, String queueName, Integer score, String message) {
        super();
        this.siteName = siteName;
        this.queueName = queueName;
        this.score = score;
        this.message = message;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
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
        return String.format("SiteQueueScore [siteName=%s, queueName=%s, score=%s, message=%s]", siteName, queueName,
                score, message);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((message == null) ? 0 : message.hashCode());
        result = prime * result + ((queueName == null) ? 0 : queueName.hashCode());
        result = prime * result + ((score == null) ? 0 : score.hashCode());
        result = prime * result + ((siteName == null) ? 0 : siteName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SiteQueueScore other = (SiteQueueScore) obj;
        if (message == null) {
            if (other.message != null)
                return false;
        } else if (!message.equals(other.message))
            return false;
        if (queueName == null) {
            if (other.queueName != null)
                return false;
        } else if (!queueName.equals(other.queueName))
            return false;
        if (score == null) {
            if (other.score != null)
                return false;
        } else if (!score.equals(other.score))
            return false;
        if (siteName == null) {
            if (other.siteName != null)
                return false;
        } else if (!siteName.equals(other.siteName))
            return false;
        return true;
    }

}
