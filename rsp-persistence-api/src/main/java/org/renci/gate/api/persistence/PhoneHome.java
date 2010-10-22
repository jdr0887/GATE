package org.renci.gate.api.persistence;

import java.io.Serializable;
import java.util.Date;

public interface PhoneHome extends Serializable {

    /**
     * @return the id
     */
    public abstract Long getId();

    /**
     * @param id
     *            the id to set
     */
    public abstract void setId(Long id);

    /**
     * @return the processId
     */
    public abstract Integer getProcessId();

    /**
     * @param processId
     *            the processId to set
     */
    public abstract void setProcessId(Integer processId);

    /**
     * @return the duration
     */
    public abstract Float getDuration();

    /**
     * @param duration
     *            the duration to set
     */
    public abstract void setDuration(Float duration);

    /**
     * @return the uTime
     */
    public abstract Float getUTime();

    /**
     * @param time
     *            the uTime to set
     */
    public abstract void setUTime(Float time);

    /**
     * @return the sTime
     */
    public abstract Float getSTime();

    /**
     * @param time
     *            the sTime to set
     */
    public abstract void setSTime(Float time);

    /**
     * @return the job
     */
    public abstract Job getJob();

    /**
     * @param job
     *            the job to set
     */
    public abstract void setJob(Job job);

    /**
     * @return the dateStartedRemotely
     */
    public abstract Date getDateStartedRemotely();

    /**
     * @param dateStartedRemotely
     *            the dateStartedRemotely to set
     */
    public abstract void setDateStartedRemotely(Date dateStartedRemotely);

}