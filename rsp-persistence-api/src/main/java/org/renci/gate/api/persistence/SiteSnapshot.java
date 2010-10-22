package org.renci.gate.api.persistence;

import java.io.Serializable;
import java.util.Date;

public interface SiteSnapshot extends Serializable {

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
     * @return the site
     */
    public abstract Site getSite();

    /**
     * @param site
     *            the site to set
     */
    public abstract void setSite(Site site);

    /**
     * @return the snapshotDate
     */
    public abstract Date getSnapshotDate();

    /**
     * @param snapshotDate
     *            the snapshotDate to set
     */
    public abstract void setSnapshotDate(Date snapshotDate);

    /**
     * @return the averageQueueTime
     */
    public abstract String getAverageQueueTime();

    /**
     * @param averageQueueTime
     *            the averageQueueTime to set
     */
    public abstract void setAverageQueueTime(String averageQueueTime);

    /**
     * @return the submittedGlideInCount
     */
    public abstract Integer getSubmittedGlideInCount();

    /**
     * @param submittedGlideInCount
     *            the submittedGlideInCount to set
     */
    public abstract void setSubmittedGlideInCount(Integer submittedGlideInCount);

    /**
     * @return the runningGlideInCount
     */
    public abstract Integer getRunningGlideInCount();

    /**
     * @param runningGlideInCount
     *            the runningGlideInCount to set
     */
    public abstract void setRunningGlideInCount(Integer runningGlideInCount);

}