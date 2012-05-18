package org.renci.gate;

public class GlideinMetrics {

    private int running;

    private int total;

    private int pending;

    public GlideinMetrics() {
        super();
    }

    public GlideinMetrics(int running, int pending) {
        super();
        this.running = running;
        this.pending = pending;
        this.total = this.running + this.pending;
    }

    public GlideinMetrics(int running, int total, int pending) {
        super();
        this.running = running;
        this.total = total;
        this.pending = pending;
    }

    public int getRunning() {
        return running;
    }

    public void setRunning(int running) {
        this.running = running;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getPending() {
        return pending;
    }

    public void setPending(int pending) {
        this.pending = pending;
    }

    @Override
    public String toString() {
        return "GlideinMetrics [running=" + running + ", total=" + total + ", pending=" + pending + "]";
    }

}
