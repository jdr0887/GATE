package org.renci.gate.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

import org.osgi.framework.BundleContext;
import org.renci.cm.RSPCredential;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author jdr0887
 */
public class CredentialTask extends TimerTask {

    private final Logger logger = LoggerFactory.getLogger(CredentialTask.class);

    private BundleContext context;

    public CredentialTask(BundleContext context) {
        super();
        this.context = context;
    }

    /**
     * updates the graphs
     */
    public void run() {
        logger.debug("ENTERING run()");

        List<RSPCredential> credList = new ArrayList<RSPCredential>();

//        try {
//            ServiceReference[] credServiceRefArray = context.getServiceReferences(CredentialService.class
//                    .getName(), null);
//            if (credServiceRefArray != null) {
//                for (ServiceReference serviceRef : credServiceRefArray) {
//                    CredentialService credentialService = (CredentialService) context.getService(serviceRef);
//                    credList.add(credentialService.getCredential());
//                    context.ungetService(serviceRef);
//                }
//            }
//        } catch (InvalidSyntaxException e) {
//            e.printStackTrace();
//        }
//
//        // clean out old credentials, renew current ones
//        if (credList != null && credList.size() > 0) {
//            for (AbstractCredential cred : credList) {
//                if (cred.isDone()) {
//                    try {
//                        cred.remove();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                } else {
//                    try {
//                        cred.renew();
//                    } catch (Exception e) {
//                        // ignore - this is getting logged in credential
//                        // should we remove this credential once error start
//                        // happening?
//                    }
//                }
//            }
//        }
        
    }

}
