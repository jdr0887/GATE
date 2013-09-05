package org.renci.gate;

import java.util.List;
import java.util.Map;

public class GlideinSubmissionBean {

    private int idleCondorJobs;

    private int runningCondorJobs;

    private Map<String, GATEService> gateServiceMap;

    private Map<String, Integer> requiredSiteMetricsMap;

    private List<GlideinMetric> siteQueueGlideinMetricList;

    public GlideinSubmissionBean() {
        super();
    }

    public int getIdleCondorJobs() {
        return idleCondorJobs;
    }

    public void setIdleCondorJobs(int idleCondorJobs) {
        this.idleCondorJobs = idleCondorJobs;
    }

    public int getRunningCondorJobs() {
        return runningCondorJobs;
    }

    public void setRunningCondorJobs(int runningCondorJobs) {
        this.runningCondorJobs = runningCondorJobs;
    }

    public Map<String, GATEService> getGateServiceMap() {
        return gateServiceMap;
    }

    public void setGateServiceMap(Map<String, GATEService> gateServiceMap) {
        this.gateServiceMap = gateServiceMap;
    }

    public Map<String, Integer> getRequiredSiteMetricsMap() {
        return requiredSiteMetricsMap;
    }

    public void setRequiredSiteMetricsMap(Map<String, Integer> requiredSiteMetricsMap) {
        this.requiredSiteMetricsMap = requiredSiteMetricsMap;
    }

    public List<GlideinMetric> getSiteQueueGlideinMetricList() {
        return siteQueueGlideinMetricList;
    }

    public void setSiteQueueGlideinMetricList(List<GlideinMetric> siteQueueGlideinMetricList) {
        this.siteQueueGlideinMetricList = siteQueueGlideinMetricList;
    }

}
