package org.renci.gate.dao;

import org.renci.gate.dao.domain.SiteSnapshot;

/**
 * 
 * @author jdr0887
 */
public interface SiteSnapshotPersistence {

    public void save(SiteSnapshot siteSnapshot) throws PersistenceException;

    public SiteSnapshot findById(Long id) throws PersistenceException;

}
