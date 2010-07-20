package org.renci.gate.api.persistence;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

public interface GlideIn extends Serializable {

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
     * @return the childGlideInId
     */
    public abstract String getChildGlideInId();

    /**
     * @param childGlideInId
     *            the childGlideInId to set
     */
    public abstract void setChildGlideInId(String childGlideInId);

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
     * @return the glideIns
     */
    public abstract Set<GlideIn> getGlideIns();

    /**
     * @param glideIns
     *            the glideIns to set
     */
    public abstract void setGlideIns(Set<GlideIn> glideIns);

}