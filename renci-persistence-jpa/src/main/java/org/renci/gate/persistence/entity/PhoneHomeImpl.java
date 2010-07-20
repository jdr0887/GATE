package org.renci.gate.persistence.entity;

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

import org.renci.gate.api.persistence.Job;
import org.renci.gate.api.persistence.PhoneHome;

/**
 * 
 * @author jdr0887
 */
@Entity
@Table(name = "phone_home")
public class PhoneHomeImpl implements PhoneHome {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Id()
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "phone_home_seq")
    @SequenceGenerator(name = "phone_home_seq", sequenceName = "phone_home_seq", allocationSize = 1, initialValue = 1)
    @Column(name = "id")
    private Long id;

    @Column(name = "process_id")
    private Integer processId;

    @Column(name = "duration")
    private Float duration;

    @Column(name = "utime")
    private Float uTime;

    @Column(name = "stime")
    private Float sTime;

    @Column(name = "date_started_remotely")
    private Date dateStartedRemotely;

    @ManyToOne(targetEntity = org.renci.gate.persistence.entity.JobImpl.class)
    @JoinColumn(name = "job_fid")
    private Job job;

    public PhoneHomeImpl() {
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
     * @return the processId
     */
    public Integer getProcessId() {
        return processId;
    }

    /**
     * @param processId
     *            the processId to set
     */
    public void setProcessId(Integer processId) {
        this.processId = processId;
    }

    /**
     * @return the duration
     */
    public Float getDuration() {
        return duration;
    }

    /**
     * @param duration
     *            the duration to set
     */
    public void setDuration(Float duration) {
        this.duration = duration;
    }

    /**
     * @return the uTime
     */
    public Float getUTime() {
        return uTime;
    }

    /**
     * @param time
     *            the uTime to set
     */
    public void setUTime(Float time) {
        uTime = time;
    }

    /**
     * @return the sTime
     */
    public Float getSTime() {
        return sTime;
    }

    /**
     * @param time
     *            the sTime to set
     */
    public void setSTime(Float time) {
        sTime = time;
    }

    /**
     * @return the job
     */
    public Job getJob() {
        return job;
    }

    /**
     * @param job
     *            the job to set
     */
    public void setJob(Job job) {
        this.job = job;
    }

    /**
     * @return the dateStartedRemotely
     */
    public Date getDateStartedRemotely() {
        return dateStartedRemotely;
    }

    /**
     * @param dateStartedRemotely
     *            the dateStartedRemotely to set
     */
    public void setDateStartedRemotely(Date dateStartedRemotely) {
        this.dateStartedRemotely = dateStartedRemotely;
    }

}