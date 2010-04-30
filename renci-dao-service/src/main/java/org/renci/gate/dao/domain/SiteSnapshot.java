package org.renci.gate.dao.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * 
 * @author jdr0887
 */
@Entity
@Table(name = "site_snapshot")
@SequenceGenerator(name = "site_snapshot_seq", sequenceName = "site_snapshot_seq")
public class SiteSnapshot implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Id()
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "site_snapshot_seq")
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "site_fid", nullable = true)
    private Site site;

    @Column(name = "snapshot_date")
    private Date snapshotDate;

    @Column(name = "average_queue_time")
    private String averageQueueTime;

    @Column(name = "submitted_glide_in_count")
    private Integer submittedGlideInCount;

    @Column(name = "running_glide_in_count")
    private Integer runningGlideInCount;

    public SiteSnapshot() {
        super();
    }

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id
     *            the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the site
     */
    public Site getSite() {
        return site;
    }

    /**
     * @param site
     *            the site to set
     */
    public void setSite(Site site) {
        this.site = site;
    }

    /**
     * @return the snapshotDate
     */
    public Date getSnapshotDate() {
        return snapshotDate;
    }

    /**
     * @param snapshotDate
     *            the snapshotDate to set
     */
    public void setSnapshotDate(Date snapshotDate) {
        this.snapshotDate = snapshotDate;
    }

    /**
     * @return the averageQueueTime
     */
    public String getAverageQueueTime() {
        return averageQueueTime;
    }

    /**
     * @param averageQueueTime
     *            the averageQueueTime to set
     */
    public void setAverageQueueTime(String averageQueueTime) {
        this.averageQueueTime = averageQueueTime;
    }

    /**
     * @return the submittedGlideInCount
     */
    public Integer getSubmittedGlideInCount() {
        return submittedGlideInCount;
    }

    /**
     * @param submittedGlideInCount
     *            the submittedGlideInCount to set
     */
    public void setSubmittedGlideInCount(Integer submittedGlideInCount) {
        this.submittedGlideInCount = submittedGlideInCount;
    }

    /**
     * @return the runningGlideInCount
     */
    public Integer getRunningGlideInCount() {
        return runningGlideInCount;
    }

    /**
     * @param runningGlideInCount
     *            the runningGlideInCount to set
     */
    public void setRunningGlideInCount(Integer runningGlideInCount) {
        this.runningGlideInCount = runningGlideInCount;
    }

}
