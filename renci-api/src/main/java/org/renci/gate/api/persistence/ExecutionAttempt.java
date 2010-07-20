package org.renci.gate.api.persistence;

import java.io.Serializable;

public interface ExecutionAttempt extends Serializable {

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
     * @return the job
     */
    public abstract Job getJob();

    /**
     * @param job
     *            the job to set
     */
    public abstract void setJob(Job job);

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
     * @return the glideIn
     */
    public abstract GlideIn getGlideIn();

    /**
     * @param glideIn
     *            the glideIn to set
     */
    public abstract void setGlideIn(GlideIn glideIn);

    /**
     * @return the computeNode
     */
    public abstract String getComputeNode();

    /**
     * @param computeNode
     *            the computeNode to set
     */
    public abstract void setComputeNode(String computeNode);

}