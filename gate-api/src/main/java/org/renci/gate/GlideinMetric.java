package org.renci.gate;

public class GlideinMetric {

    private int running;

    private int pending;

    private String queue;

    public GlideinMetric() {
        super();
        this.running = 0;
        this.pending = 0;
    }

    public GlideinMetric(String queue) {
        this();
        this.queue = queue;
    }

    public GlideinMetric(int running, int pending, String queue) {
        this(queue);
        this.running = running;
        this.pending = pending;
    }

    public String getQueue() {
        return queue;
    }

    public void setQueue(String queue) {
        this.queue = queue;
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
        return "GlideinMetrics [running=" + running + ", pending=" + pending + ", queue=" + queue + "]";
    }

}
