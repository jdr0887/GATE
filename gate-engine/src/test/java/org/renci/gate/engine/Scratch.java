package org.renci.gate.engine;

import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;
import org.renci.gate.SiteScoreInfo;

public class Scratch {

    @Test
    public void testMapSort() {
        
        Map<String, SiteScoreInfo> siteScoreMap = new HashMap<String, SiteScoreInfo>();
        siteScoreMap.put("qwer", new SiteScoreInfo(5, "qwer"));
        siteScoreMap.put("asdf", new SiteScoreInfo(1, "asdf"));
        siteScoreMap.put("zxcv", new SiteScoreInfo(3, "zxcv"));
        siteScoreMap.put("qwerasdfzxcv", new SiteScoreInfo(7, "qwerasdfzxcv"));

        List<Map.Entry<String, SiteScoreInfo>> list = new LinkedList<Map.Entry<String, SiteScoreInfo>>(
                siteScoreMap.entrySet());

        Collections.sort(list, new Comparator<Map.Entry<String, SiteScoreInfo>>() {
            @Override
            public int compare(Entry<String, SiteScoreInfo> o1, Entry<String, SiteScoreInfo> o2) {
                return o2.getValue().getScore().compareTo(o1.getValue().getScore());
            }
        });

        Map.Entry<String, SiteScoreInfo> winner = list.get(0);
        assertTrue(winner.getKey().equals("qwerasdfzxcv"));

    }
}
