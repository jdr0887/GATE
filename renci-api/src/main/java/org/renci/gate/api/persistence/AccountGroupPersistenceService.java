package org.renci.gate.api.persistence;


/**
 * 
 * @author jdr0887
 */
public interface AccountGroupPersistenceService {

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
