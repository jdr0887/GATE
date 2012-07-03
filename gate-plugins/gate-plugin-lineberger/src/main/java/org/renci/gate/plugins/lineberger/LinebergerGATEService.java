package org.renci.gate.plugins.lineberger;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.renci.gate.GATEService;
import org.renci.gate.GlideinMetrics;
import org.renci.gate.SiteInfo;
import org.renci.jlrm.LRMException;
import org.renci.jlrm.sge.SGEJobStatusType;
import org.renci.jlrm.sge.ssh.SGESSHFactory;
import org.renci.jlrm.sge.ssh.SGESSHJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author jdr0887
 */
public class LinebergerGATEService implements GATEService {

    private final Logger logger = LoggerFactory.getLogger(LinebergerGATEService.class);

    private final List<SGESSHJob> jobCache = new ArrayList<SGESSHJob>();

    private SiteInfo siteInfo;

    private String sgeHome;

    public LinebergerGATEService() {
        super();
    }

    public String getSGEHome() {
        return sgeHome;
    }

    public void setSGEHome(String sgeHome) {
        this.sgeHome = sgeHome;
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
        SGESSHFactory lsfSSHFactory = SGESSHFactory.getInstance(sgeHome, System.getProperty("user.name"),
                siteInfo.getSubmitHost());

        int running = 0;
        int pending = 0;
        try {

            Map<String, SGEJobStatusType> jobStatusMap = lsfSSHFactory.lookupStatus(jobCache
                    .toArray(new SGESSHJob[jobCache.size()]));

            Iterator<SGESSHJob> jobCacheIter = jobCache.iterator();
            while (jobCacheIter.hasNext()) {
                SGESSHJob job = jobCacheIter.next();
                SGEJobStatusType status = jobStatusMap.get(job.getId());
                switch (status) {
                    case WAITING:
                        ++pending;
                        break;
                    case RUNNING:
                        ++running;
                        break;
                    case DELETION:
                    case DONE:
                        jobCacheIter.remove();
                        break;
                    case HOLD:
                    case SUSPENDED:
                    case TRANSFERING:
                    case ERROR:
                    case RESTARTED: 
                    default:
                        break;
                }
            }
        } catch (LRMException e) {
            e.printStackTrace();
            logger.error("Error:", e);
        }
        metrics.setTotal(jobCache.size());
        metrics.setPending(pending);
        metrics.setRunning(running);
        return metrics;
    }

    @Override
    public void postGlidein() {
        File submitDir = new File("/tmp", System.getProperty("user.name"));
        submitDir.mkdirs();
        SGESSHJob job = null;
        try {
            SGESSHFactory lsfSSHFactory = SGESSHFactory.getInstance(sgeHome, System.getProperty("user.name"),
                    siteInfo.getSubmitHost());
            job = lsfSSHFactory.submitGlidein(submitDir, siteInfo.getMaxNoClaimTime(), siteInfo.getMaxRunTime(), 40,
                    siteInfo.getCondorCollectorHost(), siteInfo.getQueue());
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
                SGESSHFactory lsfSSHFactory = SGESSHFactory.getInstance(sgeHome, System.getProperty("user.name"),
                        siteInfo.getSubmitHost());
                SGESSHJob job = jobCache.get(0);
                lsfSSHFactory.killGlidein(job);
                jobCache.remove(0);
            } catch (LRMException e) {
                e.printStackTrace();
            }
        }
    }

}
