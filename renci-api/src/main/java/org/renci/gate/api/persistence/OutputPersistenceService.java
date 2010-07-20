package org.renci.gate.api.persistence;


/**
 * 
 * @author jdr0887
 */
public interface OutputPersistenceService {

    public void save(Output jobResult) throws PersistenceException;

    public Output findById(Long id) throws PersistenceException;

}
