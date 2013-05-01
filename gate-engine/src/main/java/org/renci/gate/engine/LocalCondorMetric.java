package org.renci.gate.engine;

public class LocalCondorMetric {

    private String siteName;

    private Integer idle;

    private Integer running;

    private Double siteRequiredJobOccurance;

    public LocalCondorMetric(String siteName, Integer idle, Integer running, Double siteRequiredJobOccurance) {
        super();
        this.siteName = siteName;
        this.idle = idle;
        this.running = running;
        this.siteRequiredJobOccurance = siteRequiredJobOccurance;
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

    public Double getSiteRequiredJobOccurance() {
        return siteRequiredJobOccurance;
    }

    public void setSiteRequiredJobOccurance(Double siteRequiredJobOccurance) {
        this.siteRequiredJobOccurance = siteRequiredJobOccurance;
    }

    @Override
    public String toString() {
        return String.format("LocalCondorMetric [siteName=%s, idle=%s, running=%s, siteRequiredJobOccurance=%s]",
                siteName, idle, running, siteRequiredJobOccurance);
    }

}
