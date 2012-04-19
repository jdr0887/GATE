package org.renci.gate;

public class SiteInfo {

    private String condorCollectorHost;

    private String submitHost;

    private int maxIdleCount;

    private int maxMultipleJobs;

    private int maxNoClaimTime;

    private int maxQueueTime;

    private int maxRunTime;

    private int maxTotalCount;

    private int multiplier;

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

    public int getMaxTotalCount() {
        return maxTotalCount;
    }

    public void setMaxTotalCount(int maxTotalCount) {
        this.maxTotalCount = maxTotalCount;
    }

    public int getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(int multiplier) {
        this.multiplier = multiplier;
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

}
