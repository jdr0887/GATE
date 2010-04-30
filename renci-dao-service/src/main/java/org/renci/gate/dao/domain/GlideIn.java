package org.renci.gate.dao.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * 
 * @author jdr0887
 */
@Entity
@Table(name = "glide_in")
@SequenceGenerator(name = "glide_in_seq", sequenceName = "glide_in_seq")
public class GlideIn implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Id()
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "glide_in_seq")
    @Column(name = "id")
    private Long id;

    @Column(name = "child_glide_in_id")
    private String childGlideInId;

    @Column(name = "date_submitted")
    private Date dateSubmitted;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(referencedColumnName = "id", name = "parent_fid", insertable = true, nullable = true, updatable = true)
    private Set<GlideIn> glideIns;

    public GlideIn() {
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
     * @return the childGlideInId
     */
    public String getChildGlideInId() {
        return childGlideInId;
    }

    /**
     * @param childGlideInId
     *            the childGlideInId to set
     */
    public void setChildGlideInId(String childGlideInId) {
        this.childGlideInId = childGlideInId;
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
     * @return the glideIns
     */
    public Set<GlideIn> getGlideIns() {
        return glideIns;
    }

    /**
     * @param glideIns
     *            the glideIns to set
     */
    public void setGlideIns(Set<GlideIn> glideIns) {
        this.glideIns = glideIns;
    }

}
