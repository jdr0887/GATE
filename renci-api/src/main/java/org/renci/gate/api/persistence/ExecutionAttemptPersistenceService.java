package org.renci.gate.api.persistence;


/**
 * 
 * @author jdr0887
 */
public interface ExecutionAttemptPersistenceService {

    public void save(ExecutionAttempt ea) throws PersistenceException;

    public ExecutionAttempt findById(Long id) throws PersistenceException;

    public ExecutionAttempt findByJobId(Long id) throws PersistenceException;

}
