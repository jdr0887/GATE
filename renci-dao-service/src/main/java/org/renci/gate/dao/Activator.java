package org.renci.gate.dao;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.renci.gate.dao.impl.AccountGroupPersister;
import org.renci.gate.dao.impl.AccountPersister;
import org.renci.gate.dao.impl.ExecutionAttemptPersister;
import org.renci.gate.dao.impl.GlideInPersister;
import org.renci.gate.dao.impl.InputPersister;
import org.renci.gate.dao.impl.JobPersister;
import org.renci.gate.dao.impl.OutputPersister;
import org.renci.gate.dao.impl.PhoneHomePersister;
import org.renci.gate.dao.impl.SitePersister;
import org.renci.gate.dao.impl.SiteSnapshotPersister;

public class Activator implements BundleActivator {

    public void start(BundleContext context) throws Exception {
        context.registerService(AccountPersistence.class.getName(), new AccountPersister(), null);
        context.registerService(AccountGroupPersistence.class.getName(), new AccountGroupPersister(), null);
        context.registerService(ExecutionAttemptPersistence.class.getName(), new ExecutionAttemptPersister(), null);
        context.registerService(GlideInPersistence.class.getName(), new GlideInPersister(), null);
        context.registerService(InputPersistence.class.getName(), new InputPersister(), null);
        context.registerService(JobPersistence.class.getName(), new JobPersister(), null);
        context.registerService(OutputPersistence.class.getName(), new OutputPersister(), null);
        context.registerService(PhoneHomePersistence.class.getName(), new PhoneHomePersister(), null);
        context.registerService(SitePersistence.class.getName(), new SitePersister(), null);
        context.registerService(SiteSnapshotPersistence.class.getName(), new SiteSnapshotPersister(), null);
    }

    public void stop(BundleContext context) throws Exception {

    }

}
