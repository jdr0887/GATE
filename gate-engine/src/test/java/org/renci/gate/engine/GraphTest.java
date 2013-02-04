package org.renci.gate.engine;

import java.awt.Color;
import java.io.IOException;
import java.util.Random;

import org.junit.Test;
import org.rrd4j.ConsolFun;
import org.rrd4j.DsType;
import org.rrd4j.core.RrdDb;
import org.rrd4j.core.RrdDbPool;
import org.rrd4j.core.RrdDef;
import org.rrd4j.core.Sample;
import org.rrd4j.core.Util;
import org.rrd4j.graph.RrdGraph;
import org.rrd4j.graph.RrdGraphDef;

public class GraphTest {
    static final long SEED = 1909752002L;

    static final Random RANDOM = new Random(SEED);

    @Test
    public void testRRD4J() {
        RrdDef rrdDef = new RrdDef("/tmp/test.rrd");
        long endTime = Util.getTime();
        System.out.println(endTime);
        long startTime = endTime - (24 * 60 * 60L);
        System.out.println(startTime);
        rrdDef.setStartTime(startTime);
        rrdDef.setStep(300);
        rrdDef.addDatasource("runningJobs", DsType.GAUGE, 600, 0, 50000);
        rrdDef.addDatasource("idleJobs", DsType.GAUGE, 600, 0, 50000);
        rrdDef.addDatasource("heldJobs", DsType.GAUGE, 600, 0, 50000);
        rrdDef.addDatasource("allJobs", DsType.GAUGE, 600, 0, 50000);
        rrdDef.addDatasource("idleGlideIns", DsType.GAUGE, 600, 0, 50000);
        rrdDef.addDatasource("runningGlideIns", DsType.GAUGE, 600, 0, 50000);
        rrdDef.addDatasource("registeredGlideIns", DsType.GAUGE, 600, 0, 50000);
        rrdDef.addArchive(ConsolFun.AVERAGE, 0.5, 1, 2016);
        rrdDef.addArchive(ConsolFun.AVERAGE, 0.5, 12, 2160);
        RrdDbPool pool = RrdDbPool.getInstance();
        try {
            RrdDb rrdDb = pool.requestRrdDb(rrdDef);

            Sample sample = rrdDb.createSample();

            sample.setTime(endTime - 1800);
            sample.setValue("runningJobs", 20);
            sample.setValue("idleJobs", 7);
            sample.setValue("heldJobs", 3);
            sample.setValue("allJobs", 30);
            sample.setValue("idleGlideIns", 5);
            sample.setValue("runningGlideIns", 4);
            sample.setValue("registeredGlideIns", 3);
            System.out.println(sample.dump());
            sample.update();

            sample.setTime(endTime - 1500);
            sample.setValue("runningJobs", 20);
            sample.setValue("idleJobs", 7);
            sample.setValue("heldJobs", 3);
            sample.setValue("allJobs", 30);
            sample.setValue("idleGlideIns", 5);
            sample.setValue("runningGlideIns", 4);
            sample.setValue("registeredGlideIns", 3);
            System.out.println(sample.dump());
            sample.update();

            sample.setTime(endTime - 1200);
            sample.setValue("runningJobs", 20);
            sample.setValue("idleJobs", 7);
            sample.setValue("heldJobs", 3);
            sample.setValue("allJobs", 30);
            sample.setValue("idleGlideIns", 5);
            sample.setValue("runningGlideIns", 4);
            sample.setValue("registeredGlideIns", 3);
            System.out.println(sample.dump());
            sample.update();

            sample.setTime(endTime - 900);
            sample.setValue("runningJobs", 10);
            sample.setValue("idleJobs", 4);
            sample.setValue("heldJobs", 1);
            sample.setValue("allJobs", 15);
            sample.setValue("idleGlideIns", 3);
            sample.setValue("runningGlideIns", 2);
            sample.setValue("registeredGlideIns", 1);
            System.out.println(sample.dump());
            sample.update();

            sample.setTime(endTime - 620);
            sample.setValue("runningJobs", 10);
            sample.setValue("idleJobs", 4);
            sample.setValue("heldJobs", 1);
            sample.setValue("allJobs", 15);
            sample.setValue("idleGlideIns", 3);
            sample.setValue("runningGlideIns", 2);
            sample.setValue("registeredGlideIns", 1);
            System.out.println(sample.dump());
            sample.update();

            sample.setTime(endTime - 600);
            sample.setValue("runningJobs", 10);
            sample.setValue("idleJobs", 4);
            sample.setValue("heldJobs", 1);
            sample.setValue("allJobs", 15);
            sample.setValue("idleGlideIns", 3);
            sample.setValue("runningGlideIns", 2);
            sample.setValue("registeredGlideIns", 1);
            System.out.println(sample.dump());
            sample.update();

            sample.setTime(endTime - 300);
            sample.setValue("runningJobs", 10);
            sample.setValue("idleJobs", 4);
            sample.setValue("heldJobs", 1);
            sample.setValue("allJobs", 15);
            sample.setValue("idleGlideIns", 3);
            sample.setValue("runningGlideIns", 2);
            sample.setValue("registeredGlideIns", 1);
            System.out.println(sample.dump());
            sample.update();

            sample.setTime(endTime);
            sample.setValue("runningJobs", 10);
            sample.setValue("idleJobs", 4);
            sample.setValue("heldJobs", 1);
            sample.setValue("allJobs", 15);
            sample.setValue("idleGlideIns", 3);
            sample.setValue("runningGlideIns", 2);
            sample.setValue("registeredGlideIns", 1);
            System.out.println(sample.dump());
            sample.update();

            pool.release(rrdDb);

            rrdDb.close();

        } catch (IOException e1) {
            e1.printStackTrace();
        }

        RrdGraphDef graphDef = new RrdGraphDef();
        graphDef.setStartTime(startTime);
        // graphDef.setEndTime(endTime);
        graphDef.setWidth(1100);
        graphDef.setHeight(500);
        graphDef.setPoolUsed(false);
        graphDef.setImageFormat("png");
        graphDef.setTitle("Portal Jobs and Glideins");
        graphDef.setVerticalLabel("Count");
        graphDef.datasource("runningJobs", "/tmp/test.rrd", "runningJobs", ConsolFun.AVERAGE);
        graphDef.datasource("idleJobs", "/tmp/test.rrd", "idleJobs", ConsolFun.AVERAGE);
        graphDef.datasource("heldJobs", "/tmp/test.rrd", "heldJobs", ConsolFun.AVERAGE);
        graphDef.datasource("allJobs", "/tmp/test.rrd", "allJobs", ConsolFun.AVERAGE);
        graphDef.datasource("idleGlideIns", "/tmp/test.rrd", "idleGlideIns", ConsolFun.AVERAGE);
        graphDef.datasource("registeredGlideIns", "/tmp/test.rrd", "registeredGlideIns", ConsolFun.AVERAGE);
        graphDef.area("runningJobs", Color.GREEN, "Running Jobs");
        graphDef.stack("idleJobs", Color.CYAN, "Idle Jobs");
        graphDef.stack("heldJobs", Color.PINK, "Held Jobs");
        graphDef.line("allJobs", Color.BLACK, "Jobs Total");
        graphDef.line("idleGlideIns", Color.ORANGE, "Pending GlideIns");
        graphDef.line("registeredGlideIns", Color.BLUE, "Pending GlideIns");
        graphDef.setFilename("/tmp/test.png");

        try {
            RrdGraph graph = new RrdGraph(graphDef);
            graph.getRrdGraphInfo().dump();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
