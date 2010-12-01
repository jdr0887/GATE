package org.renci.gate.engine;

public enum JobStateType {

    UNEXPANDED("0"),

    IDLE("1"),

    RUNNING("2"),

    REMOVED("3"),

    COMPLETED("4"),

    HELD("5"),

    SUBMISSION_ERROR("6");

    private String jobStatus = null;

    private JobStateType(String jobStatus) {
        this.setJobStatus(jobStatus);
    }

    public void setJobStatus(String jobStatus) {
        this.jobStatus = jobStatus;
    }

    public String getJobStatus() {
        return jobStatus;
    }

}
