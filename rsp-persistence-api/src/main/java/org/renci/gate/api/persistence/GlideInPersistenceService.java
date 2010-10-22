package org.renci.gate.api.persistence;


/**
 * 
 * @author jdr0887
 */
public interface GlideInPersistenceService {

    public void save(GlideIn glideIn) throws PersistenceException;

    public GlideIn findById(Long id) throws PersistenceException;

}
