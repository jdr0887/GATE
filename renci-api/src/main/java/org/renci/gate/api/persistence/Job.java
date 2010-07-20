package org.renci.gate.api.persistence;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

public interface Job extends Serializable {

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
     * @return the jobs
     */
    public abstract Set<Job> getJobs();

    /**
     * @param jobs
     *            the jobs to set
     */
    public abstract void setJobs(Set<Job> jobs);

    /**
     * @return the userName
     */
    public abstract String getUserName();

    /**
     * @param userName
     *            the userName to set
     */
    public abstract void setUserName(String userName);

    /**
     * @return the requesterAddress
     */
    public abstract String getRequesterAddress();

    /**
     * @param requesterAddress
     *            the requesterAddress to set
     */
    public abstract void setRequesterAddress(String requesterAddress);

    /**
     * @return the serviceName
     */
    public abstract String getServiceName();

    /**
     * @param serviceName
     *            the serviceName to set
     */
    public abstract void setServiceName(String serviceName);

    /**
     * @return the directory
     */
    public abstract String getDirectory();

    /**
     * @param directory
     *            the directory to set
     */
    public abstract void setDirectory(String directory);

    /**
     * @return the status
     */
    public abstract StatusType getStatus();

    /**
     * @param status
     *            the status to set
     */
    public abstract void setStatus(StatusType status);

    /**
     * @return the origination
     */
    public abstract OriginationType getOrigination();

    /**
     * @param origination
     *            the origination to set
     */
    public abstract void setOrigination(OriginationType origination);

    /**
     * @return the exceptionMessage
     */
    public abstract String getExceptionMessage();

    /**
     * @param exceptionMessage
     *            the exceptionMessage to set
     */
    public abstract void setExceptionMessage(String exceptionMessage);

    /**
     * @return the purged
     */
    public abstract Boolean getPurged();

    /**
     * @param purged
     *            the purged to set
     */
    public abstract void setPurged(Boolean purged);

    /**
     * @return the dateStartedLocally
     */
    public abstract Date getDateStartedLocally();

    /**
     * @param dateStartedLocally
     *            the dateStartedLocally to set
     */
    public abstract void setDateStartedLocally(Date dateStartedLocally);

    /**
     * @return the dateFinishedLocally
     */
    public abstract Date getDateFinishedLocally();

    /**
     * @param dateFinishedLocally
     *            the dateFinishedLocally to set
     */
    public abstract void setDateFinishedLocally(Date dateFinishedLocally);

    /**
     * @return the dateSubmitted
     */
    public abstract Date getDateSubmitted();

    /**
     * @param dateSubmitted
     *            the dateSubmitted to set
     */
    public abstract void setDateSubmitted(Date dateSubmitted);

    /**
     * @return the inputData
     */
    public abstract Set<Input> getInputData();

    /**
     * @param inputData
     *            the inputData to set
     */
    public abstract void setInputData(Set<Input> inputData);

    /**
     * @return the outputData
     */
    public abstract Set<Output> getOutputData();

    /**
     * @param outputData
     *            the outputData to set
     */
    public abstract void setOutputData(Set<Output> outputData);

    /**
     * @return the runName
     */
    public abstract String getRunName();

    /**
     * @param runName
     *            the runName to set
     */
    public abstract void setRunName(String runName);

    /**
     * @return the phoneHome
     */
    public abstract PhoneHome getPhoneHome();

    /**
     * @param phoneHome
     *            the phoneHome to set
     */
    public abstract void setPhoneHome(PhoneHome phoneHome);

}