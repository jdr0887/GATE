package org.renci.gate.api.persistence;

import java.util.Date;
import java.util.List;

/**
 * 
 * @author jdr0887
 */
public interface JobPersistenceService {

    /**
     * 
     * @param jobHistory
     */
    public void save(Job job) throws PersistenceException;

    /**
     * 
     * @param jobHistory
     */
    public void delete(Job job) throws PersistenceException;

    /**
     * 
     * @param id
     */
    public void delete(Long id) throws PersistenceException;

    /**
     * 
     * @param id
     * @return
     */
    public Job findById(Long id) throws PersistenceException;

    /**
     * 
     * @param id
     * @throws PersistenceException
     */
    public StatusType findStatus(Long id) throws PersistenceException;

    /**
     * 
     * @param userId
     * @return
     */
    public List<Job> findByUserName(String username) throws PersistenceException;

    /**
     * 
     * @param username
     * @param originationType
     * @return
     */
    public List<Job> findByUserNameAndOriginationList(String username, List<OriginationType> originationTypeList) throws PersistenceException;

    /**
     * 
     * @param userId
     * @return
     */
    public List<Job> findQueuedJobs() throws PersistenceException;

    /**
     * 
     * @param startDate
     * @param endDate
     * @return
     */
    public List<Job> findByDateRange(Date startDate, Date endDate) throws PersistenceException;

    /**
     * 
     * @param userId
     * @return
     */
    public List<Job> findByUserNameAndDateRange(String username, Date startDate, Date endDate) throws PersistenceException;

}
