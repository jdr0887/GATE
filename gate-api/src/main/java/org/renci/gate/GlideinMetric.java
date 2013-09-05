package org.renci.gate;

public class GlideinMetric {

    private String siteName;

    private String queueName;

    private int running;

    private int pending;

    public GlideinMetric(String siteName, String queueName, int running, int pending) {
        super();
        this.siteName = siteName;
        this.queueName = queueName;
        this.running = running;
        this.pending = pending;
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

    public int getRunning() {
        return running;
    }

    public void incrementRunning() {
        ++this.running;
    }

    public void incrementPending() {
        ++this.pending;
    }

    public void setRunning(int running) {
        this.running = running;
    }

    public int getTotal() {
        return this.running + this.pending;
    }

    public int getPending() {
        return pending;
    }

    public void setPending(int pending) {
        this.pending = pending;
    }

    @Override
    public String toString() {
        return String.format("GlideinMetric [siteName=%s, queueName=%s, running=%s, pending=%s]", siteName, queueName,
                running, pending);
    }

}
