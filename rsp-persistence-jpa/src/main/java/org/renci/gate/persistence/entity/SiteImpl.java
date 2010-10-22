package org.renci.gate.persistence.entity;

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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.renci.gate.api.persistence.Account;
import org.renci.gate.api.persistence.GridType;
import org.renci.gate.api.persistence.InfrastructureType;
import org.renci.gate.api.persistence.Site;

/**
 * 
 * @author jdr0887
 */
@Entity
@Table(name = "site")
public class SiteImpl implements Site {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Id()
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "site_seq")
    @SequenceGenerator(name = "site_seq", sequenceName = "site_seq", allocationSize = 1, initialValue = 1)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "infrastructure")
    @Enumerated(EnumType.STRING)
    private InfrastructureType infrastructure;

    @Column(name = "grid_type")
    @Enumerated(EnumType.STRING)
    private GridType gridType;

    @Column(name = "gatekeeper_host")
    private String gatekeeperHost;

    @Column(name = "job_manager")
    private String jobManager;

    @Column(name = "project")
    private String project;

    @Column(name = "queue")
    private String queue;

    @Column(name = "max_multiple_jobs")
    private Integer maxMultipleJobs;

    @Column(name = "max_idle_count")
    private Integer maxIdleCount;

    @Column(name = "max_total_count")
    private Integer maxTotalCount;

    @Column(name = "max_queue_time")
    private Integer maxQueueTime;

    @Column(name = "max_run_time")
    private Integer maxRunTime;

    @Column(name = "max_no_claim_time")
    private Integer maxNoClaimTime;

    @Column(name = "multiplier")
    private Integer multiplier;

    @Column(name = "enabled")
    private Boolean enabled;

    @ManyToMany(targetEntity = org.renci.gate.persistence.entity.AccountImpl.class, cascade = { CascadeType.PERSIST,
            CascadeType.MERGE }, fetch = FetchType.EAGER)
    @JoinTable(name = "account_site", joinColumns = @JoinColumn(name = "site_fid"), inverseJoinColumns = @JoinColumn(name = "account_fid"))
    private Set<Account> accounts;

    public SiteImpl() {
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
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the infrastructure
     */
    public InfrastructureType getInfrastructure() {
        return infrastructure;
    }

    /**
     * @param infrastructure
     *            the infrastructure to set
     */
    public void setInfrastructure(InfrastructureType infrastructure) {
        this.infrastructure = infrastructure;
    }

    /**
     * @return the gatekeeper
     */
    public String getGatekeeperHost() {
        return gatekeeperHost;
    }

    /**
     * @param gatekeeper
     *            the gatekeeper to set
     */
    public void setGatekeeperHost(String gatekeeperHost) {
        this.gatekeeperHost = gatekeeperHost;
    }

    /**
     * @return the jobManager
     */
    public String getJobManager() {
        return jobManager;
    }

    /**
     * @param jobManager
     *            the jobManager to set
     */
    public void setJobManager(String jobManager) {
        this.jobManager = jobManager;
    }

    /**
     * @return the project
     */
    public String getProject() {
        return project;
    }

    /**
     * @param project
     *            the project to set
     */
    public void setProject(String project) {
        this.project = project;
    }

    /**
     * @return the multiplier
     */
    public Integer getMultiplier() {
        return multiplier;
    }

    /**
     * @param multiplier
     *            the multiplier to set
     */
    public void setMultiplier(Integer multiplier) {
        this.multiplier = multiplier;
    }

    /**
     * @return the enabled
     */
    public Boolean getEnabled() {
        return enabled;
    }

    /**
     * @param enabled
     *            the enabled to set
     */
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * @return the account
     */
    public Set<Account> getAccounts() {
        return accounts;
    }

    /**
     * @param account
     *            the account to set
     */
    public void setAccounts(Set<Account> accounts) {
        this.accounts = accounts;
    }

    /**
     * @return the queue
     */
    public String getQueue() {
        return queue;
    }

    /**
     * @param queue
     *            the queue to set
     */
    public void setQueue(String queue) {
        this.queue = queue;
    }

    /**
     * @return the maxMultipleJobs
     */
    public Integer getMaxMultipleJobs() {
        return maxMultipleJobs;
    }

    /**
     * @param maxMultipleJobs
     *            the maxMultipleJobs to set
     */
    public void setMaxMultipleJobs(Integer maxMultipleJobs) {
        this.maxMultipleJobs = maxMultipleJobs;
    }

    /**
     * @return the maxIdleCount
     */
    public Integer getMaxIdleCount() {
        return maxIdleCount;
    }

    /**
     * @param maxIdleCount
     *            the maxIdleCount to set
     */
    public void setMaxIdleCount(Integer maxIdleCount) {
        this.maxIdleCount = maxIdleCount;
    }

    /**
     * @return the maxTotalCount
     */
    public Integer getMaxTotalCount() {
        return maxTotalCount;
    }

    /**
     * @param maxTotalCount
     *            the maxTotalCount to set
     */
    public void setMaxTotalCount(Integer maxTotalCount) {
        this.maxTotalCount = maxTotalCount;
    }

    /**
     * @return the maxQueueTime
     */
    public Integer getMaxQueueTime() {
        return maxQueueTime;
    }

    /**
     * @param maxQueueTime
     *            the maxQueueTime to set
     */
    public void setMaxQueueTime(Integer maxQueueTime) {
        this.maxQueueTime = maxQueueTime;
    }

    /**
     * @return the maxRunTime
     */
    public Integer getMaxRunTime() {
        return maxRunTime;
    }

    /**
     * @param maxRunTime
     *            the maxRunTime to set
     */
    public void setMaxRunTime(Integer maxRunTime) {
        this.maxRunTime = maxRunTime;
    }

    /**
     * @return the maxNoClaimTime
     */
    public Integer getMaxNoClaimTime() {
        return maxNoClaimTime;
    }

    /**
     * @param maxNoClaimTime
     *            the maxNoClaimTime to set
     */
    public void setMaxNoClaimTime(Integer maxNoClaimTime) {
        this.maxNoClaimTime = maxNoClaimTime;
    }

    /**
     * @return the gridType
     */
    public GridType getGridType() {
        return gridType;
    }

    /**
     * @param gridType
     *            the gridType to set
     */
    public void setGridType(GridType gridType) {
        this.gridType = gridType;
    }

}
