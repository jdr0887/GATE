package org.renci.gate.dao.domain;

import java.io.Serializable;

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
@Table(name = "execution_attempt")
@SequenceGenerator(name = "execution_attempt_seq", sequenceName = "execution_attempt_seq")
public class ExecutionAttempt implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Id()
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "execution_attempt_seq")
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "job_fid", nullable = true)
    private Job job;

    @ManyToOne
    @JoinColumn(name = "site_fid", nullable = true)
    private Site site;

    @Column(name = "compute_node")
    private String computeNode;

    @ManyToOne
    @JoinColumn(name = "glide_in_fid", nullable = true)
    private GlideIn glideIn;

    public ExecutionAttempt() {
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
     * @return the glideIn
     */
    public GlideIn getGlideIn() {
        return glideIn;
    }

    /**
     * @param glideIn
     *            the glideIn to set
     */
    public void setGlideIn(GlideIn glideIn) {
        this.glideIn = glideIn;
    }

    /**
     * @return the computeNode
     */
    public String getComputeNode() {
        return computeNode;
    }

    /**
     * @param computeNode
     *            the computeNode to set
     */
    public void setComputeNode(String computeNode) {
        this.computeNode = computeNode;
    }

}
