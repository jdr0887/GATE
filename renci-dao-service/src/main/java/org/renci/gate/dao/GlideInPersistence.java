package org.renci.gate.dao;

import org.renci.gate.dao.domain.GlideIn;

/**
 * 
 * @author jdr0887
 */
public interface GlideInPersistence {

    public void save(GlideIn glideIn) throws PersistenceException;

    public GlideIn findById(Long id) throws PersistenceException;

}
