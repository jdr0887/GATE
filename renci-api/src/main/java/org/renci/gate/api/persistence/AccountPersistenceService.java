package org.renci.gate.api.persistence;

import java.util.List;

/**
 * 
 * @author jdr0887
 */
public interface AccountPersistenceService {

    /**
     * 
     * @param account
     */
    public void save(Account account) throws PersistenceException;

    /**
     * 
     * @param account
     */
    public void delete(Account account) throws PersistenceException;

    /**
     * 
     * @param id
     * @return
     */
    public Account findById(Long id) throws PersistenceException;

    /**
     * 
     * @param id
     * @return
     */
    public List<Account> findActive() throws PersistenceException;

    /**
     * 
     * @param userId
     * @return
     */
    public Account findByUsername(String username) throws PersistenceException;

    /**
     * 
     * @param key
     * @return
     * @throws PersistenceException
     */
    public Account findByWSAccessKey(String key) throws PersistenceException;

    /**
     * 
     * @param account
     * @return
     */
    public List<Account> findByExample(Account account) throws PersistenceException;

    /**
     * 
     * @param account
     * @return
     */
    public Account findByEmail(String email) throws PersistenceException;

    /**
     * 
     * @param account
     * @return
     */
    public Account findByVerificationHash(String hash) throws PersistenceException;

    /**
     * 
     * @param hash
     * @return
     */
    public Account findByPasswordResetHash(String hash) throws PersistenceException;

    /**
     * 
     * @param account
     * @return
     */
    public List<Account> findCurrentRequests() throws PersistenceException;

    /**
     * 
     * @return
     */
    public List<Account> findApprovedRequests() throws PersistenceException;

    /**
     * 
     * @return
     */
    public List<Account> findPasswordChangedAccounts() throws PersistenceException;

}
