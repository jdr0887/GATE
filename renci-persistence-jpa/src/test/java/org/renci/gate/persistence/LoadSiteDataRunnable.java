package org.renci.gate.persistence;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.renci.gate.api.persistence.InfrastructureType;
import org.renci.gate.persistence.entity.SiteImpl;

public class LoadSiteDataRunnable implements Runnable {

    public LoadSiteDataRunnable() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Runnable#run()
     */
    public void run() {
        System.out.println("Entering run()");
        EntityManagerFactory emf = null;
        EntityManager em = null;
        try {
            emf = Persistence.createEntityManagerFactory("rsp", null);
            em = emf.createEntityManager();

            em.getTransaction().begin();
            
            SiteImpl site = new SiteImpl();
            site.setEnabled(true);
            site.setName("KITTYHAWK0");
            site.setInfrastructure(InfrastructureType.RENCI);
            site.setGatekeeperHost("kh0.renci.org");
            site.setJobManager("pbs");
            site.setProject("");
            site.setQueue("");
            site.setMultiplier(1);
            site.setMaxMultipleJobs(4);
            site.setMaxIdleCount(10);
            site.setMaxTotalCount(200);
            site.setMaxQueueTime(1440);
            site.setMaxRunTime(2880);
            site.setMaxNoClaimTime(60);
            System.out.println("persisting site");
            em.persist(site);

            site = new SiteImpl();
            site.setEnabled(true);
            site.setName("BLUERIDGE");
            site.setInfrastructure(InfrastructureType.RENCI);
            site.setGatekeeperHost("brgw0.renci.org");
            site.setJobManager("pbs");
            site.setProject("");
            site.setQueue("");
            site.setMultiplier(1);
            site.setMaxMultipleJobs(4);
            site.setMaxIdleCount(10);
            site.setMaxTotalCount(200);
            site.setMaxQueueTime(1440);
            site.setMaxRunTime(2880);
            site.setMaxNoClaimTime(60);
            System.out.println("persisting site");
            em.persist(site);
            
            em.getTransaction().commit();

        } catch (Exception e) {
            e.printStackTrace();
            em.getTransaction().commit();
        } finally {
            em.close();
            emf.close();
        }

    }

    public static void main(String[] args) {
        LoadSiteDataRunnable runnable = new LoadSiteDataRunnable();
        runnable.run();
    }

}
