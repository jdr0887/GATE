package org.renci.gate.api.persistence;


/**
 * 
 * @author jdr0887
 */
public interface SiteSnapshotPersistenceService {

    public void save(SiteSnapshot siteSnapshot) throws PersistenceException;

    public SiteSnapshot findById(Long id) throws PersistenceException;

}
