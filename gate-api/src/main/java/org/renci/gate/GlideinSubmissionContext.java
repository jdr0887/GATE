package org.renci.gate;

import java.util.List;

import org.renci.gate.impl.LargeGlideinSubmissionStrategy;
import org.renci.gate.impl.MediumGlideinSubmissionStrategy;
import org.renci.gate.impl.SmallGlideinSubmissionStrategy;

public class GlideinSubmissionContext {

    private GlideinSubmissionBean glideinSubmissionBean;

    private GlideinSubmissionStrategy glideinSubmissionStrategy;

    public GlideinSubmissionContext(GlideinSubmissionBean glideinSubmissionBean) {

        this.glideinSubmissionBean = glideinSubmissionBean;

        if (glideinSubmissionBean.getIdleCondorJobs() < 50) {
            this.glideinSubmissionStrategy = new SmallGlideinSubmissionStrategy();
        } else if (glideinSubmissionBean.getIdleCondorJobs() >= 50 && glideinSubmissionBean.getIdleCondorJobs() < 600) {
            this.glideinSubmissionStrategy = new MediumGlideinSubmissionStrategy();
        } else if (glideinSubmissionBean.getIdleCondorJobs() >= 200) {
            this.glideinSubmissionStrategy = new LargeGlideinSubmissionStrategy();
        }

    }

    public int calculateNumberToSubmit() {
        return this.glideinSubmissionStrategy.calculateNumberToSubmit(this.glideinSubmissionBean);
    }

    public List<SiteQueueScore> calculateSiteQueueScores() {
        return this.glideinSubmissionStrategy.calculateSiteQueueScores(this.glideinSubmissionBean);
    }

}
