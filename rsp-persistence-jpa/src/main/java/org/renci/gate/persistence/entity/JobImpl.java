package org.renci.gate.persistence.entity;

import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.apache.openjpa.persistence.jdbc.ElementJoinColumn;
import org.renci.gate.api.persistence.Input;
import org.renci.gate.api.persistence.Job;
import org.renci.gate.api.persistence.OriginationType;
import org.renci.gate.api.persistence.Output;
import org.renci.gate.api.persistence.PhoneHome;
import org.renci.gate.api.persistence.StatusType;

/**
 * 
 * @author jdr0887
 */
@Entity
@Table(name = "job")
public class JobImpl implements Job {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Id()
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "job_seq")
    @SequenceGenerator(name = "job_seq", sequenceName = "job_seq", allocationSize = 1, initialValue = 1)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "requester_address")
    private String requesterAddress;

    @Column(name = "service_name")
    private String serviceName;

    @Column(name = "directory")
    private String directory;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private StatusType status;

    @Column(name = "origination")
    @Enumerated(EnumType.STRING)
    private OriginationType origination;

    @Column(name = "exception_message")
    private String exceptionMessage;

    @Column(name = "purged")
    private Boolean purged;

    @Column(name = "date_submitted")
    private Date dateSubmitted;

    @Column(name = "date_started_locally")
    private Date dateStartedLocally;

    @Column(name = "date_finished_locally")
    private Date dateFinishedLocally;

    @Column(name = "run_name")
    private String runName;

    @OneToOne(targetEntity = org.renci.gate.persistence.entity.PhoneHomeImpl.class, mappedBy = "job", cascade = CascadeType.ALL)
    private PhoneHome phoneHome;

    @ManyToMany(targetEntity = org.renci.gate.persistence.entity.InputImpl.class, cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
    @JoinTable(name = "job_input", joinColumns = @JoinColumn(name = "job_fid"), inverseJoinColumns = @JoinColumn(name = "input_fid"))
    private Set<Input> inputData;

    @ManyToMany(targetEntity = org.renci.gate.persistence.entity.OutputImpl.class, cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
    @JoinTable(name = "job_output", joinColumns = @JoinColumn(name = "job_fid"), inverseJoinColumns = @JoinColumn(name = "output_fid"))
    private Set<Output> outputData;

    @OneToMany(targetEntity = org.renci.gate.persistence.entity.JobImpl.class, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @ElementJoinColumn(referencedColumnName = "id", name = "parent_fid", insertable = true, nullable = true, updatable = true)
    private Set<Job> jobs;

    public JobImpl() {
        super();
    }

    public JobImpl(JobImpl parentJob) {
        super();
        this.userName = parentJob.getUserName();
        this.requesterAddress = parentJob.getRequesterAddress();
        this.serviceName = parentJob.getServiceName();
        this.directory = parentJob.getDirectory();
        this.status = parentJob.getStatus();
        this.origination = parentJob.getOrigination();
        this.exceptionMessage = parentJob.getExceptionMessage();
        this.purged = parentJob.getPurged();
        this.dateSubmitted = parentJob.getDateSubmitted();
        this.runName = parentJob.getRunName();
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
     * @return the jobs
     */
    public Set<Job> getJobs() {
        return jobs;
    }

    /**
     * @param jobs
     *            the jobs to set
     */
    public void setJobs(Set<Job> jobs) {
        this.jobs = jobs;
    }

    /**
     * @return the userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @param userName
     *            the userName to set
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * @return the requesterAddress
     */
    public String getRequesterAddress() {
        return requesterAddress;
    }

    /**
     * @param requesterAddress
     *            the requesterAddress to set
     */
    public void setRequesterAddress(String requesterAddress) {
        this.requesterAddress = requesterAddress;
    }

    /**
     * @return the serviceName
     */
    public String getServiceName() {
        return serviceName;
    }

    /**
     * @param serviceName
     *            the serviceName to set
     */
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    /**
     * @return the directory
     */
    public String getDirectory() {
        return directory;
    }

    /**
     * @param directory
     *            the directory to set
     */
    public void setDirectory(String directory) {
        this.directory = directory;
    }

    /**
     * @return the status
     */
    public StatusType getStatus() {
        return status;
    }

    /**
     * @param status
     *            the status to set
     */
    public void setStatus(StatusType status) {
        this.status = status;
    }

    /**
     * @return the origination
     */
    public OriginationType getOrigination() {
        return origination;
    }

    /**
     * @param origination
     *            the origination to set
     */
    public void setOrigination(OriginationType origination) {
        this.origination = origination;
    }

    /**
     * @return the exceptionMessage
     */
    public String getExceptionMessage() {
        return exceptionMessage;
    }

    /**
     * @param exceptionMessage
     *            the exceptionMessage to set
     */
    public void setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }

    /**
     * @return the purged
     */
    public Boolean getPurged() {
        return purged;
    }

    /**
     * @param purged
     *            the purged to set
     */
    public void setPurged(Boolean purged) {
        this.purged = purged;
    }

    /**
     * @return the dateStartedLocally
     */
    public Date getDateStartedLocally() {
        return dateStartedLocally;
    }

    /**
     * @param dateStartedLocally
     *            the dateStartedLocally to set
     */
    public void setDateStartedLocally(Date dateStartedLocally) {
        this.dateStartedLocally = dateStartedLocally;
    }

    /**
     * @return the dateFinishedLocally
     */
    public Date getDateFinishedLocally() {
        return dateFinishedLocally;
    }

    /**
     * @param dateFinishedLocally
     *            the dateFinishedLocally to set
     */
    public void setDateFinishedLocally(Date dateFinishedLocally) {
        this.dateFinishedLocally = dateFinishedLocally;
    }

    /**
     * @return the dateSubmitted
     */
    public Date getDateSubmitted() {
        return dateSubmitted;
    }

    /**
     * @param dateSubmitted
     *            the dateSubmitted to set
     */
    public void setDateSubmitted(Date dateSubmitted) {
        this.dateSubmitted = dateSubmitted;
    }

    /**
     * @return the inputData
     */
    public Set<Input> getInputData() {
        return inputData;
    }

    /**
     * @param inputData
     *            the inputData to set
     */
    public void setInputData(Set<Input> inputData) {
        this.inputData = inputData;
    }

    /**
     * @return the outputData
     */
    public Set<Output> getOutputData() {
        return outputData;
    }

    /**
     * @param outputData
     *            the outputData to set
     */
    public void setOutputData(Set<Output> outputData) {
        this.outputData = outputData;
    }

    /**
     * @return the runName
     */
    public String getRunName() {
        return runName;
    }

    /**
     * @param runName
     *            the runName to set
     */
    public void setRunName(String runName) {
        this.runName = runName;
    }

    /**
     * @return the phoneHome
     */
    public PhoneHome getPhoneHome() {
        return phoneHome;
    }

    /**
     * @param phoneHome
     *            the phoneHome to set
     */
    public void setPhoneHome(PhoneHome phoneHome) {
        this.phoneHome = phoneHome;
    }

}