package org.renci.gate.plugins.kure;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.renci.gate.GATEService;
import org.renci.gate.GlideinMetric;
import org.renci.gate.QueueInfo;
import org.renci.gate.SiteInfo;
import org.renci.jlrm.LRMException;
import org.renci.jlrm.lsf.LSFJobStatusInfo;
import org.renci.jlrm.lsf.ssh.LSFSSHFactory;
import org.renci.jlrm.lsf.ssh.LSFSSHJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author jdr0887
 */
public class KureGATEService implements GATEService {

    private final Logger logger = LoggerFactory.getLogger(KureGATEService.class);

    private final List<LSFSSHJob> jobCache = new ArrayList<LSFSSHJob>();

    private SiteInfo siteInfo;

    private String lsfHome;

    public KureGATEService() {
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
    public Map<String, GlideinMetric> lookupMetrics() {
        Map<String, GlideinMetric> metricsMap = new HashMap<String, GlideinMetric>();
        LSFSSHFactory lsfSSHFactory = LSFSSHFactory.getInstance(lsfHome, System.getProperty("user.name"),
                siteInfo.getSubmitHost());

        try {

            Set<LSFJobStatusInfo> jobStatusSet = lsfSSHFactory.lookupStatus(jobCache.toArray(new LSFSSHJob[jobCache
                    .size()]));

            // get unique list of queues
            Set<String> queueSet = new HashSet<String>();
            if (jobStatusSet != null) {
                for (LSFJobStatusInfo info : jobStatusSet) {
                    queueSet.add(info.getQueue());
                }
            }

            Iterator<LSFSSHJob> jobCacheIter = jobCache.iterator();
            while (jobCacheIter.hasNext()) {
                LSFSSHJob job = jobCacheIter.next();
                for (String queue : queueSet) {
                    int running = 0;
                    int pending = 0;
                    for (LSFJobStatusInfo info : jobStatusSet) {
                        GlideinMetric metrics = new GlideinMetric();
                        if (info.getQueue().equals(queue) && job.getId().equals(info.getJobId())) {
                            switch (info.getType()) {
                                case PENDING:
                                    ++pending;
                                    break;
                                case RUNNING:
                                    ++running;
                                    break;
                                case EXIT:
                                case UNKNOWN:
                                case ZOMBIE:
                                case DONE:
                                    jobCacheIter.remove();
                                    break;
                                case SUSPENDED_BY_SYSTEM:
                                case SUSPENDED_BY_USER:
                                case SUSPENDED_FROM_PENDING:
                                default:
                                    break;
                            }
                        }
                        metrics.setPending(pending);
                        metrics.setRunning(running);
                        metricsMap.put(info.getQueue(), metrics);
                    }
                }
            }

        } catch (LRMException e) {
            e.printStackTrace();
            logger.error("Error:", e);
        }
        return metricsMap;
    }

    @Override
    public void postGlidein(SiteInfo site, QueueInfo queue) {
        File submitDir = new File("/tmp", System.getProperty("user.name"));
        submitDir.mkdirs();
        LSFSSHJob job = null;
        try {
            LSFSSHFactory lsfSSHFactory = LSFSSHFactory.getInstance(lsfHome, System.getProperty("user.name"),
                    siteInfo.getSubmitHost());
            job = lsfSSHFactory.submitGlidein(submitDir, siteInfo.getMaxNoClaimTime(), siteInfo.getMaxRunTime(), 40,
                    siteInfo.getCondorCollectorHost(), queue);
            if (job != null && StringUtils.isNotEmpty(job.getId())) {
                logger.info("job.getId(): {}", job.getId());
                jobCache.add(job);
            }
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
                jobCache.remove(0);
            } catch (LRMException e) {
                e.printStackTrace();
            }
        }
    }

}
