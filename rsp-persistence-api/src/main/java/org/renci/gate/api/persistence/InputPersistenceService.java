package org.renci.gate.api.persistence;


/**
 * 
 * @author jdr0887
 */
public interface InputPersistenceService {

    public void save(Input jobData) throws PersistenceException;

    public Input findById(Long id) throws PersistenceException;

}
