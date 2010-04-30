package org.renci.gate.dao;

import org.renci.gate.dao.domain.PhoneHome;

/**
 * 
 * @author jdr0887
 */
public interface PhoneHomePersistence {

    /**
     * 
     * @param jobHistory
     */
    public void save(PhoneHome job) throws PersistenceException;

    /**
     * 
     * @param jobHistory
     */
    public void delete(PhoneHome job) throws PersistenceException;

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
    public PhoneHome findById(Long id) throws PersistenceException;

}
