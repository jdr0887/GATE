package org.renci.gate.dao;

import java.util.List;

import org.renci.gate.dao.domain.Site;

/**
 * 
 * @author jdr0887
 */
public interface SitePersistence {

    public void save(Site site) throws PersistenceException;

    public List<Site> findAll() throws PersistenceException;

    public List<Site> findEnabled() throws PersistenceException;

    public Site findById(Long id) throws PersistenceException;

    public Site findByName(String name) throws PersistenceException;

}
