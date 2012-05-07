package org.renci.gate.plugins.killdevil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.renci.gate.GATEService;
import org.renci.gate.GlideinMetrics;
import org.renci.gate.SiteInfo;
import org.renci.jlrm.LRMException;
import org.renci.jlrm.lsf.LSFJobStatusType;
import org.renci.jlrm.lsf.ssh.LSFSSHFactory;
import org.renci.jlrm.lsf.ssh.LSFSSHJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author jdr0887
 */
public class KillDevilGATEService implements GATEService {

    private final Logger logger = LoggerFactory.getLogger(KillDevilGATEService.class);

    private final List<LSFSSHJob> jobCache = new ArrayList<LSFSSHJob>();

    private SiteInfo siteInfo;

    private String lsfHome;

    public KillDevilGATEService() {
        super();
    }

    public String getLsfHome() {
        return lsfHome;
    }

    public void setLsfHome(String lsfHome) {
        this.lsfHome = lsfHome;
    }

    public void setSiteInfo(SiteInfo siteInfo) {
        this.siteInfo = siteInfo;
    }

    public SiteInfo getSiteInfo() {
        return siteInfo;
    }

    @Override
    public GlideinMetrics lookupMetrics() {
        GlideinMetrics metrics = new GlideinMetrics();
        LSFSSHFactory lsfSSHFactory = LSFSSHFactory.getInstance(lsfHome, System.getProperty("user.name"),
                siteInfo.getSubmitHost());

        metrics.setTotal(jobCache.size());
        int running = 0;
        int pending = 0;
        for (LSFSSHJob job : jobCache) {
            try {
                LSFJobStatusType status = lsfSSHFactory.lookupStatus(job);
                switch (status) {
                    case PENDING:
                        ++pending;
                        break;
                    case RUNNING:
                        ++running;
                        break;
                    case SUSPENDED_BY_SYSTEM:
                    case SUSPENDED_BY_USER:
                    case SUSPENDED_FROM_PENDING:
                    case UNKNOWN:
                    case ZOMBIE:
                    case DONE:
                    case EXIT:
                    default:
                        break;
                }
            } catch (LRMException e) {
                e.printStackTrace();
            }
        }
        metrics.setPending(pending);
        metrics.setRunning(running);
        return metrics;
    }

    @Override
    public void postGlidein() {
        File submitDir = new File("/tmp");
        LSFSSHJob job = null;
        try {
            LSFSSHFactory lsfSSHFactory = LSFSSHFactory.getInstance(lsfHome, System.getProperty("user.name"),
                    siteInfo.getSubmitHost());
            job = lsfSSHFactory.submitGlidein(submitDir, siteInfo.getMaxNoClaimTime(), siteInfo.getMaxRunTime(), 40,
                    siteInfo.getCondorCollectorHost(), siteInfo.getQueue());
            logger.info("job.getId(): {}", job.getId());
            jobCache.add(job);
        } catch (LRMException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteGlidein() {
        if (jobCache.size() > 0) {
            try {
                LSFSSHFactory lsfSSHFactory = LSFSSHFactory.getInstance(lsfHome, System.getProperty("user.name"),
                        siteInfo.getSubmitHost());
                LSFSSHJob job = jobCache.get(0);
                lsfSSHFactory.killGlidein(job);
            } catch (LRMException e) {
                e.printStackTrace();
            }
        }
    }

}
