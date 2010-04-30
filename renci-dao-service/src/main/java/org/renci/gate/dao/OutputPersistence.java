package org.renci.gate.dao;

import org.renci.gate.dao.domain.Output;

/**
 * 
 * @author jdr0887
 */
public interface OutputPersistence {

    public void save(Output jobResult) throws PersistenceException;

    public Output findById(Long id) throws PersistenceException;

}
