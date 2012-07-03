package org.renci.gate;

import java.util.concurrent.TimeUnit;

public class QueueInfo {

    private String name;

    private long pendingTime;

    private TimeUnit pendingTimeUnit;

    private long runTime;

    private TimeUnit runTimeUnit;

    private int maxJobLimit;

    private int maxMultipleJobsToSubmit;

    public QueueInfo() {
        super();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getPendingTime() {
        return pendingTime;
    }

    public void setPendingTime(long pendingTime) {
        this.pendingTime = pendingTime;
    }

    public TimeUnit getPendingTimeUnit() {
        return pendingTimeUnit;
    }

    public void setPendingTimeUnit(TimeUnit pendingTimeUnit) {
        this.pendingTimeUnit = pendingTimeUnit;
    }

    public long getRunTime() {
        return runTime;
    }

    public void setRunTime(long runTime) {
        this.runTime = runTime;
    }

    public TimeUnit getRunTimeUnit() {
        return runTimeUnit;
    }

    public void setRunTimeUnit(TimeUnit runTimeUnit) {
        this.runTimeUnit = runTimeUnit;
    }

    public int getMaxJobLimit() {
        return maxJobLimit;
    }

    public void setMaxJobLimit(int maxJobLimit) {
        this.maxJobLimit = maxJobLimit;
    }

    public int getMaxMultipleJobsToSubmit() {
        return maxMultipleJobsToSubmit;
    }

    public void setMaxMultipleJobsToSubmit(int maxMultipleJobsToSubmit) {
        this.maxMultipleJobsToSubmit = maxMultipleJobsToSubmit;
    }

    @Override
    public String toString() {
        return "QueueInfo [name=" + name + ", pendingTime=" + pendingTime + ", pendingTimeUnit=" + pendingTimeUnit
                + ", runTime=" + runTime + ", runTimeUnit=" + runTimeUnit + ", maxJobLimit=" + maxJobLimit
                + ", maxMultipleJobsToSubmit=" + maxMultipleJobsToSubmit + "]";
    }

}
