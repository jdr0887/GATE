package org.renci.gate;

import java.util.Set;

import org.renci.jlrm.JobStatusInfo;
import org.renci.jlrm.Site;

public abstract class AbstractGATEService implements GATEService {

    private Site site;

    private String collectorHost;

    private String activeQueues;

    private String hostAllow;

    private Set<JobStatusInfo> jobStatusInfo;

    public AbstractGATEService() {
        super();
    }

    public Set<JobStatusInfo> getJobStatusInfo() {
        return jobStatusInfo;
    }

    public void setJobStatusInfo(Set<JobStatusInfo> jobStatusInfo) {
        this.jobStatusInfo = jobStatusInfo;
    }

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }

    public String getCollectorHost() {
        return collectorHost;
    }

    public void setCollectorHost(String collectorHost) {
        this.collectorHost = collectorHost;
    }

    public String getActiveQueues() {
        return activeQueues;
    }

    public void setActiveQueues(String activeQueues) {
        this.activeQueues = activeQueues;
    }

    public String getHostAllow() {
        return hostAllow;
    }

    public void setHostAllow(String hostAllow) {
        this.hostAllow = hostAllow;
    }

}
