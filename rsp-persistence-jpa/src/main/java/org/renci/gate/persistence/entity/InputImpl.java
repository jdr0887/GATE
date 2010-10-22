package org.renci.gate.persistence.entity;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.renci.gate.api.persistence.Input;
import org.renci.gate.api.persistence.Job;

/**
 * 
 * @author jdr0887
 */
@Entity
@Table(name = "input")
public class InputImpl implements Input {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Id()
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "input_seq")
    @SequenceGenerator(name = "input_seq", sequenceName = "input_seq", allocationSize = 1, initialValue = 1)
    @Column(name = "id")
    private Long id;

    @ManyToMany(targetEntity = org.renci.gate.persistence.entity.JobImpl.class, cascade = { CascadeType.PERSIST,
            CascadeType.MERGE }, fetch = FetchType.EAGER)
    @JoinTable(name = "job_input", joinColumns = @JoinColumn(name = "input_fid"), inverseJoinColumns = @JoinColumn(name = "job_fid"))
    private Set<Job> jobs;

    @Column(name = "data_type")
    private String dataType;

    @Column(name = "method")
    private String method;

    @Column(name = "filename")
    private String filename;

    @Column(name = "name")
    private String name;

    public InputImpl() {
        super();
    }

    /**
     * @return the dataType
     */
    public String getDataType() {
        return dataType;
    }

    /**
     * @param dataType
     *            the dataType to set
     */
    public void setDataType(String dataType) {
        this.dataType = dataType;
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
     * @return the method
     */
    public String getMethod() {
        return method;
    }

    /**
     * @param method
     *            the method to set
     */
    public void setMethod(String method) {
        this.method = method;
    }

    /**
     * @return the filename
     */
    public String getFilename() {
        return filename;
    }

    /**
     * @param filename
     *            the filename to set
     */
    public void setFilename(String filename) {
        this.filename = filename;
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

}
