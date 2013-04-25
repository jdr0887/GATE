package org.renci.gate.engine;

public class LocalCondorMetric {

    private String siteName;

    private Integer idle;

    private Integer running;

    private Double siteRequiredJobOccurancePercentile;

    public LocalCondorMetric(String siteName, Integer idle, Integer running, Double siteRequiredJobOccurancePercentile) {
        super();
        this.siteName = siteName;
        this.idle = idle;
        this.running = running;
        this.siteRequiredJobOccurancePercentile = siteRequiredJobOccurancePercentile;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public Integer getIdle() {
        return idle;
    }

    public void setIdle(Integer idle) {
        this.idle = idle;
    }

    public Integer getRunning() {
        return running;
    }

    public void setRunning(Integer running) {
        this.running = running;
    }

    public Double getSiteRequiredJobOccurancePercentile() {
        return siteRequiredJobOccurancePercentile;
    }

    public void setSiteRequiredJobOccurancePercentile(Double siteRequiredJobOccurancePercentile) {
        this.siteRequiredJobOccurancePercentile = siteRequiredJobOccurancePercentile;
    }

    @Override
    public String toString() {
        return String.format(
                "LocalCondorMetric [siteName=%s, idle=%s, running=%s, siteRequiredJobOccurancePercentile=%s]",
                siteName, idle, running, siteRequiredJobOccurancePercentile);
    }

}
