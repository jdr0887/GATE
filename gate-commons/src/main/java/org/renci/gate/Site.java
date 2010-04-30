package org.renci.gate;

/**
 * Representation of a CE on OSG
 */
public class Site {

    private String uniqueId;

    private int currentMatches;

    private int maxMatches;

    private int successRate;

    boolean siteVerified;

    private String gridType;

    private String gatekeeper;

    private int heldCount;

    private int idleCount;

    private String jobmanager;

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

    private int runningCount;

    private int totalCount;

    private String scoreComment;

    private int jobsSubmitting;

    private int jobsPending;

    private int jobsRunning;

    private int jobsStaging;

    private int jobsFailed;

    private int jobsOther;

    private String requirementsJobSuccessRates;

    private ClassAd classAd;

    public Site() {
        this.uniqueId = "";
        this.currentMatches = 0;
        this.maxMatches = 0;
        this.successRate = 0;
        this.siteVerified = false;
        this.name = "";
        this.multiplier = 1;
        this.gatekeeper = "";
        this.jobmanager = "";
        this.totalCount = 0;
        this.heldCount = 0;
        this.idleCount = 0;
        this.runningCount = 0;
        this.gridType = "gt2";
        this.scoreComment = null;
        this.jobsSubmitting = 0;
        this.jobsPending = 0;
        this.jobsRunning = 0;
        this.jobsStaging = 0;
        this.jobsFailed = 0;
        this.jobsOther = 0;
        this.requirementsJobSuccessRates = "";
    }

    /**
     * @return the uniqueId
     */
    public String getUniqueId() {
        return uniqueId;
    }

    /**
     * @param uniqueId
     *            the uniqueId to set
     */
    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    /**
     * @return the currentMatches
     */
    public int getCurrentMatches() {
        return currentMatches;
    }

    /**
     * @param currentMatches
     *            the currentMatches to set
     */
    public void setCurrentMatches(int currentMatches) {
        this.currentMatches = currentMatches;
    }

    /**
     * @return the maxMatches
     */
    public int getMaxMatches() {
        return maxMatches;
    }

    /**
     * @param maxMatches
     *            the maxMatches to set
     */
    public void setMaxMatches(int maxMatches) {
        this.maxMatches = maxMatches;
    }

    /**
     * @return the successRate
     */
    public int getSuccessRate() {
        return successRate;
    }

    /**
     * @param successRate
     *            the successRate to set
     */
    public void setSuccessRate(int successRate) {
        this.successRate = successRate;
    }

    /**
     * @return the siteVerified
     */
    public boolean isSiteVerified() {
        return siteVerified;
    }

    /**
     * @param siteVerified
     *            the siteVerified to set
     */
    public void setSiteVerified(boolean siteVerified) {
        this.siteVerified = siteVerified;
    }

    /**
     * @return the gridType
     */
    public String getGridType() {
        return gridType;
    }

    /**
     * @param gridType
     *            the gridType to set
     */
    public void setGridType(String gridType) {
        this.gridType = gridType;
    }

    /**
     * @return the gatekeeper
     */
    public String getGatekeeper() {
        return gatekeeper;
    }

    /**
     * @param gatekeeper
     *            the gatekeeper to set
     */
    public void setGatekeeper(String gatekeeper) {
        this.gatekeeper = gatekeeper;
    }

    /**
     * @return the heldCount
     */
    public int getHeldCount() {
        return heldCount;
    }

    /**
     * @param heldCount
     *            the heldCount to set
     */
    public void setHeldCount(int heldCount) {
        this.heldCount = heldCount;
    }

    /**
     * @return the idleCount
     */
    public int getIdleCount() {
        return idleCount;
    }

    /**
     * @param idleCount
     *            the idleCount to set
     */
    public void setIdleCount(int idleCount) {
        this.idleCount = idleCount;
    }

    /**
     * @return the jobmanager
     */
    public String getJobmanager() {
        return jobmanager;
    }

    /**
     * @param jobmanager
     *            the jobmanager to set
     */
    public void setJobmanager(String jobmanager) {
        this.jobmanager = jobmanager;
    }

    /**
     * @return the maxIdleCount
     */
    public int getMaxIdleCount() {
        return maxIdleCount;
    }

    /**
     * @param maxIdleCount
     *            the maxIdleCount to set
     */
    public void setMaxIdleCount(int maxIdleCount) {
        this.maxIdleCount = maxIdleCount;
    }

    /**
     * @return the maxMultipleJobs
     */
    public int getMaxMultipleJobs() {
        return maxMultipleJobs;
    }

    /**
     * @param maxMultipleJobs
     *            the maxMultipleJobs to set
     */
    public void setMaxMultipleJobs(int maxMultipleJobs) {
        this.maxMultipleJobs = maxMultipleJobs;
    }

    /**
     * @return the maxNoClaimTime
     */
    public int getMaxNoClaimTime() {
        return maxNoClaimTime;
    }

    /**
     * @param maxNoClaimTime
     *            the maxNoClaimTime to set
     */
    public void setMaxNoClaimTime(int maxNoClaimTime) {
        this.maxNoClaimTime = maxNoClaimTime;
    }

    /**
     * @return the maxQueueTime
     */
    public int getMaxQueueTime() {
        return maxQueueTime;
    }

    /**
     * @param maxQueueTime
     *            the maxQueueTime to set
     */
    public void setMaxQueueTime(int maxQueueTime) {
        this.maxQueueTime = maxQueueTime;
    }

    /**
     * @return the maxRunTime
     */
    public int getMaxRunTime() {
        return maxRunTime;
    }

    /**
     * @param maxRunTime
     *            the maxRunTime to set
     */
    public void setMaxRunTime(int maxRunTime) {
        this.maxRunTime = maxRunTime;
    }

    /**
     * @return the maxTotalCount
     */
    public int getMaxTotalCount() {
        return maxTotalCount;
    }

    /**
     * @param maxTotalCount
     *            the maxTotalCount to set
     */
    public void setMaxTotalCount(int maxTotalCount) {
        this.maxTotalCount = maxTotalCount;
    }

    /**
     * @return the multiplier
     */
    public int getMultiplier() {
        return multiplier;
    }

    /**
     * @param multiplier
     *            the multiplier to set
     */
    public void setMultiplier(int multiplier) {
        this.multiplier = multiplier;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the project
     */
    public String getProject() {
        return project;
    }

    /**
     * @param project
     *            the project to set
     */
    public void setProject(String project) {
        this.project = project;
    }

    /**
     * @return the queue
     */
    public String getQueue() {
        return queue;
    }

    /**
     * @param queue
     *            the queue to set
     */
    public void setQueue(String queue) {
        this.queue = queue;
    }

    /**
     * @return the runningCount
     */
    public int getRunningCount() {
        return runningCount;
    }

    /**
     * @param runningCount
     *            the runningCount to set
     */
    public void setRunningCount(int runningCount) {
        this.runningCount = runningCount;
    }

    /**
     * @return the totalCount
     */
    public int getTotalCount() {
        return totalCount;
    }

    /**
     * @param totalCount
     *            the totalCount to set
     */
    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    /**
     * @return the scoreComment
     */
    public String getScoreComment() {
        return scoreComment;
    }

    /**
     * @param scoreComment
     *            the scoreComment to set
     */
    public void setScoreComment(String scoreComment) {
        this.scoreComment = scoreComment;
    }

    /**
     * @return the jobsSubmitting
     */
    public int getJobsSubmitting() {
        return jobsSubmitting;
    }

    /**
     * @param jobsSubmitting
     *            the jobsSubmitting to set
     */
    public void setJobsSubmitting(int jobsSubmitting) {
        this.jobsSubmitting = jobsSubmitting;
    }

    /**
     * @return the jobsPending
     */
    public int getJobsPending() {
        return jobsPending;
    }

    /**
     * @param jobsPending
     *            the jobsPending to set
     */
    public void setJobsPending(int jobsPending) {
        this.jobsPending = jobsPending;
    }

    /**
     * @return the jobsRunning
     */
    public int getJobsRunning() {
        return jobsRunning;
    }

    /**
     * @param jobsRunning
     *            the jobsRunning to set
     */
    public void setJobsRunning(int jobsRunning) {
        this.jobsRunning = jobsRunning;
    }

    /**
     * @return the jobsStaging
     */
    public int getJobsStaging() {
        return jobsStaging;
    }

    /**
     * @param jobsStaging
     *            the jobsStaging to set
     */
    public void setJobsStaging(int jobsStaging) {
        this.jobsStaging = jobsStaging;
    }

    /**
     * @return the jobsFailed
     */
    public int getJobsFailed() {
        return jobsFailed;
    }

    /**
     * @param jobsFailed
     *            the jobsFailed to set
     */
    public void setJobsFailed(int jobsFailed) {
        this.jobsFailed = jobsFailed;
    }

    /**
     * @return the jobsOther
     */
    public int getJobsOther() {
        return jobsOther;
    }

    /**
     * @param jobsOther
     *            the jobsOther to set
     */
    public void setJobsOther(int jobsOther) {
        this.jobsOther = jobsOther;
    }

    /**
     * @return the requirementsJobSuccessRates
     */
    public String getRequirementsJobSuccessRates() {
        return requirementsJobSuccessRates;
    }

    /**
     * @param requirementsJobSuccessRates
     *            the requirementsJobSuccessRates to set
     */
    public void setRequirementsJobSuccessRates(String requirementsJobSuccessRates) {
        this.requirementsJobSuccessRates = requirementsJobSuccessRates;
    }

    /**
     * @return the classAd
     */
    public ClassAd getClassAd() {
        return classAd;
    }

    /**
     * @param classAd
     *            the classAd to set
     */
    public void setClassAd(ClassAd classAd) {
        this.classAd = classAd;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Site [classAd=" + classAd + ", currentMatches=" + currentMatches + ", gatekeeper=" + gatekeeper
                + ", gridType=" + gridType + ", heldCount=" + heldCount + ", idleCount=" + idleCount + ", jobmanager="
                + jobmanager + ", jobsFailed=" + jobsFailed + ", jobsOther=" + jobsOther + ", jobsPending="
                + jobsPending + ", jobsRunning=" + jobsRunning + ", jobsStaging=" + jobsStaging + ", jobsSubmitting="
                + jobsSubmitting + ", maxIdleCount=" + maxIdleCount + ", maxMatches=" + maxMatches
                + ", maxMultipleJobs=" + maxMultipleJobs + ", maxNoClaimTime=" + maxNoClaimTime + ", maxQueueTime="
                + maxQueueTime + ", maxRunTime=" + maxRunTime + ", maxTotalCount=" + maxTotalCount + ", multiplier="
                + multiplier + ", name=" + name + ", project=" + project + ", queue=" + queue
                + ", requirementsJobSuccessRates=" + requirementsJobSuccessRates + ", runningCount=" + runningCount
                + ", scoreComment=" + scoreComment + ", siteVerified=" + siteVerified + ", successRate=" + successRate
                + ", totalCount=" + totalCount + ", uniqueId=" + uniqueId + "]";
    }

}
