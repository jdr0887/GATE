 package org.renci.gate.api.persistence;

import java.io.Serializable;
import java.util.Set;

public interface Site extends Serializable {

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
     * @return the name
     */
    public abstract String getName();

    /**
     * @param name
     *            the name to set
     */
    public abstract void setName(String name);

    /**
     * @return the infrastructure
     */
    public abstract InfrastructureType getInfrastructure();

    /**
     * @param infrastructure
     *            the infrastructure to set
     */
    public abstract void setInfrastructure(InfrastructureType infrastructure);

    /**
     * @return the gatekeeper
     */
    public abstract String getGatekeeperHost();

    /**
     * @param gatekeeper
     *            the gatekeeper to set
     */
    public abstract void setGatekeeperHost(String gatekeeperHost);

    /**
     * @return the jobManager
     */
    public abstract String getJobManager();

    /**
     * @param jobManager
     *            the jobManager to set
     */
    public abstract void setJobManager(String jobManager);

    /**
     * @return the project
     */
    public abstract String getProject();

    /**
     * @param project
     *            the project to set
     */
    public abstract void setProject(String project);

    /**
     * @return the multiplier
     */
    public abstract Integer getMultiplier();

    /**
     * @param multiplier
     *            the multiplier to set
     */
    public abstract void setMultiplier(Integer multiplier);

    /**
     * @return the enabled
     */
    public abstract Boolean getEnabled();

    /**
     * @param enabled
     *            the enabled to set
     */
    public abstract void setEnabled(Boolean enabled);

    /**
     * @return the account
     */
    public abstract Set<Account> getAccounts();

    /**
     * @param account
     *            the account to set
     */
    public abstract void setAccounts(Set<Account> accounts);

    /**
     * @return the queue
     */
    public abstract String getQueue();

    /**
     * @param queue
     *            the queue to set
     */
    public abstract void setQueue(String queue);

    /**
     * @return the maxMultipleJobs
     */
    public abstract Integer getMaxMultipleJobs();

    /**
     * @param maxMultipleJobs
     *            the maxMultipleJobs to set
     */
    public abstract void setMaxMultipleJobs(Integer maxMultipleJobs);

    /**
     * @return the maxIdleCount
     */
    public abstract Integer getMaxIdleCount();

    /**
     * @param maxIdleCount
     *            the maxIdleCount to set
     */
    public abstract void setMaxIdleCount(Integer maxIdleCount);

    /**
     * @return the maxTotalCount
     */
    public abstract Integer getMaxTotalCount();

    /**
     * @param maxTotalCount
     *            the maxTotalCount to set
     */
    public abstract void setMaxTotalCount(Integer maxTotalCount);

    /**
     * @return the maxQueueTime
     */
    public abstract Integer getMaxQueueTime();

    /**
     * @param maxQueueTime
     *            the maxQueueTime to set
     */
    public abstract void setMaxQueueTime(Integer maxQueueTime);

    /**
     * @return the maxRunTime
     */
    public abstract Integer getMaxRunTime();

    /**
     * @param maxRunTime
     *            the maxRunTime to set
     */
    public abstract void setMaxRunTime(Integer maxRunTime);

    /**
     * @return the maxNoClaimTime
     */
    public abstract Integer getMaxNoClaimTime();

    /**
     * @param maxNoClaimTime
     *            the maxNoClaimTime to set
     */
    public abstract void setMaxNoClaimTime(Integer maxNoClaimTime);

    /**
     * @return the gridType
     */
    public abstract GridType getGridType();

    /**
     * @param gridType
     *            the gridType to set
     */
    public abstract void setGridType(GridType gridType);

}