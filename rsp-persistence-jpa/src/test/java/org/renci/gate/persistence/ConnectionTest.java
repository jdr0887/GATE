package org.renci.gate.persistence;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.Test;


public class ConnectionTest {

    
    @Test
    public void testConnection() {
        try {
            
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("rsp", null);
            EntityManager em = emf.createEntityManager();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
