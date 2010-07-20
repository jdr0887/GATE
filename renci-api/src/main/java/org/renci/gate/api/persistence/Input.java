package org.renci.gate.api.persistence;

import java.io.Serializable;
import java.util.Set;

public interface Input extends Serializable {

    /**
     * @return the dataType
     */
    public abstract String getDataType();

    /**
     * @param dataType
     *            the dataType to set
     */
    public abstract void setDataType(String dataType);

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
     * @return the method
     */
    public abstract String getMethod();

    /**
     * @param method
     *            the method to set
     */
    public abstract void setMethod(String method);

    /**
     * @return the filename
     */
    public abstract String getFilename();

    /**
     * @param filename
     *            the filename to set
     */
    public abstract void setFilename(String filename);

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
     * @return the name
     */
    public abstract String getName();

    /**
     * @param name
     *            the name to set
     */
    public abstract void setName(String name);

}