package org.renci.gate.dao;

import org.renci.gate.dao.domain.ExecutionAttempt;

/**
 * 
 * @author jdr0887
 */
public interface ExecutionAttemptPersistence {

    public void save(ExecutionAttempt ea) throws PersistenceException;

    public ExecutionAttempt findById(Long id) throws PersistenceException;

    public ExecutionAttempt findByJobId(Long id) throws PersistenceException;

}
