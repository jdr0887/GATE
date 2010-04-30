package org.renci.gate.dao;

import org.renci.gate.dao.domain.Input;

/**
 * 
 * @author jdr0887
 */
public interface InputPersistence {

    public void save(Input jobData) throws PersistenceException;

    public Input findById(Long id) throws PersistenceException;

}
