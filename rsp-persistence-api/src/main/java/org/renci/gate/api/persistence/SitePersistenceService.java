package org.renci.gate.api.persistence;

import java.util.List;

/**
 * 
 * @author jdr0887
 */
public interface SitePersistenceService {

    public void save(Site site) throws PersistenceException;

    public List<? extends Site> findAll() throws PersistenceException;

    public List<? extends Site> findEnabled() throws PersistenceException;

    public Site findById(Long id) throws PersistenceException;

    public Site findByName(String name) throws PersistenceException;

}
