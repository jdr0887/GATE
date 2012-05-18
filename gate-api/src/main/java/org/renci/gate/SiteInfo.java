package org.renci.gate;

public class SiteInfo {

    private String condorCollectorHost;

    private String submitHost;

    private int maxIdleCount;

    private int maxMultipleJobs;

    private int maxNoClaimTime;

    private int maxQueueTime;

    private int maxRunTime;

    private int maxRunningCount;

    private String name;

    private String project;

    private String queue;

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

    public int getMaxIdleCount() {
        return maxIdleCount;
    }

    public void setMaxIdleCount(int maxIdleCount) {
        this.maxIdleCount = maxIdleCount;
    }

    public int getMaxMultipleJobs() {
        return maxMultipleJobs;
    }

    public void setMaxMultipleJobs(int maxMultipleJobs) {
        this.maxMultipleJobs = maxMultipleJobs;
    }

    public int getMaxNoClaimTime() {
        return maxNoClaimTime;
    }

    public void setMaxNoClaimTime(int maxNoClaimTime) {
        this.maxNoClaimTime = maxNoClaimTime;
    }

    public int getMaxQueueTime() {
        return maxQueueTime;
    }

    public void setMaxQueueTime(int maxQueueTime) {
        this.maxQueueTime = maxQueueTime;
    }

    public int getMaxRunTime() {
        return maxRunTime;
    }

    public void setMaxRunTime(int maxRunTime) {
        this.maxRunTime = maxRunTime;
    }

    public int getMaxRunningCount() {
        return maxRunningCount;
    }

    public void setMaxRunningCount(int maxRunningCount) {
        this.maxRunningCount = maxRunningCount;
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

    public String getQueue() {
        return queue;
    }

    public void setQueue(String queue) {
        this.queue = queue;
    }

    @Override
    public String toString() {
        return "SiteInfo [condorCollectorHost=" + condorCollectorHost + ", submitHost=" + submitHost
                + ", maxIdleCount=" + maxIdleCount + ", maxMultipleJobs=" + maxMultipleJobs + ", maxNoClaimTime="
                + maxNoClaimTime + ", maxQueueTime=" + maxQueueTime + ", maxRunTime=" + maxRunTime
                + ", maxRunningCount=" + maxRunningCount + ", name=" + name + ", project=" + project + ", queue="
                + queue + "]";
    }

}
