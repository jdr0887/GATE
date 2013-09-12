package org.renci.gate.engine;

public class GATEEngineBeanService {

    private Long period;

    private Integer maxTotalGlideins;

    public GATEEngineBeanService() {
        super();
    }

    public Long getPeriod() {
        return period;
    }

    public void setPeriod(Long period) {
        this.period = period;
    }

    public Integer getMaxTotalGlideins() {
        return maxTotalGlideins;
    }

    public void setMaxTotalGlideins(Integer maxTotalGlideins) {
        this.maxTotalGlideins = maxTotalGlideins;
    }

}
