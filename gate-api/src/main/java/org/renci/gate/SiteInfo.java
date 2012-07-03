package org.renci.gate;

import java.util.List;

public class SiteInfo {

    private String condorCollectorHost;

    private String submitHost;

    private int maxTotalPending;

    private int maxTotalRunning;

    private String name;

    private String project;

    private List<QueueInfo> queues;

    public SiteInfo() {
        super();
    }

    public String getCondorCollectorHost() {
        return condorCollectorHost;
    }

    public void setCondorCollectorHost(String condorCollectorHost) {
        this.condorCollectorHost = condorCollectorHost;
    }

    public String getSubmitHost() {
        return submitHost;
    }

    public void setSubmitHost(String submitHost) {
        this.submitHost = submitHost;
    }

    public int getMaxTotalPending() {
        return maxTotalPending;
    }

    public void setMaxTotalPending(int maxTotalPending) {
        this.maxTotalPending = maxTotalPending;
    }

    public int getMaxTotalRunning() {
        return maxTotalRunning;
    }

    public void setMaxTotalRunning(int maxTotalRunning) {
        this.maxTotalRunning = maxTotalRunning;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public List<QueueInfo> getQueues() {
        return queues;
    }

    public void setQueues(List<QueueInfo> queues) {
        this.queues = queues;
    }

    @Override
    public String toString() {
        return "SiteInfo [condorCollectorHost=" + condorCollectorHost + ", submitHost=" + submitHost
                + ", maxTotalPending=" + maxTotalPending + ", maxTotalRunning=" + maxTotalRunning + ", name=" + name
                + ", project=" + project + ", queues=" + queues + "]";
    }

}
