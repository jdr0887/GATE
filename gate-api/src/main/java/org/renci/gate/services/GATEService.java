package org.renci.gate.services;

import java.util.List;

import org.drools.RuleBase;
import org.renci.gate.GATESite;

/**
 * 
 * @author jdr0887
 */
public interface GATEService {

    public List<GATESite> getSiteList();

    public List<RuleBase> getFilterSiteRules();

}
