package org.renci.gate;

import org.renci.jlrm.Site;

public abstract class AbstractGATEService implements GATEService {

    private Site site;

    private String collectorHost;

    private String activeQueues;

    private String username;

    public AbstractGATEService() {
        super();
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}
