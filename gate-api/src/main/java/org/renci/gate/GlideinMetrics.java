package org.renci.gate;

public class GlideinMetrics {

    private int running;

    private int pending;

    private String queue;

    public GlideinMetrics() {
        super();
    }

    public GlideinMetrics(int running, int pending, String queue) {
        super();
        this.running = running;
        this.pending = pending;
        this.queue = queue;
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
