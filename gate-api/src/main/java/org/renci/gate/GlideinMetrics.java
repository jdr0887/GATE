package org.renci.gate;

public class GlideinMetrics {

    private int running;

    private int total;

    private int pending;

    public GlideinMetrics() {
        super();
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

}
