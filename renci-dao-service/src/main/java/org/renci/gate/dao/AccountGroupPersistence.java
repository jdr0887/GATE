package org.renci.gate.dao;

import org.renci.gate.dao.domain.AccountGroup;

/**
 * 
 * @author jdr0887
 */
public interface AccountGroupPersistence {

    /**
     * 
     * @param account
     */
    public void save(AccountGroup accountGroup) throws PersistenceException;

    /**
     * 
     * @param account
     */
    public void delete(AccountGroup accountGroup) throws PersistenceException;

    /**
     * 
     * @param id
     * @return
     */
    public AccountGroup findById(Long id) throws PersistenceException;

}
