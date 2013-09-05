package org.renci.gate;

import java.util.List;

public interface GlideinSubmissionStrategy {

    public List<SiteQueueScore> calculateSiteQueueScores(GlideinSubmissionBean loadSubmissionStrategyBean);

    public int calculateNumberToSubmit(GlideinSubmissionBean loadSubmissionStrategyBean);

}
