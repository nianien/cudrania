package com.kavas.misc.hibernate;

import org.apache.commons.lang3.Validate;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.context.internal.ManagedSessionContext;
import org.hibernate.context.spi.CurrentSessionContext;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.service.jta.platform.spi.JtaPlatform;
import org.springframework.orm.hibernate4.SpringSessionContext;
import org.springframework.transaction.jta.JtaAfterCompletionSynchronization;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.transaction.Synchronization;
import java.util.Arrays;
import java.util.List;

/**
 * Session context that determines whether there exist a transaction in
 * progress in the current thread, and if not, it opens the {@link org.hibernate.Session}.
 * <p>
 * It delegates to {@link org.springframework.orm.hibernate4.SpringSessionContext} to check whether there exist
 * a Session or not. If it doesn't, the Session is created bound to the current
 * thread via {@link org.hibernate.context.internal.ManagedSessionContext}.
 * </p>
 * <p>
 * Sessions created by this context are registered in the available transaction
 * synchronization strategy in order to cleanup and properly close Sessions
 * when transactions finish. If there's no synchronization strategy available,
 * the session will never be closed.
 * </p>
 *
 * @author skyfalling
 */
public class TransactionAwareSessionContext implements CurrentSessionContext {

    /**
     * ID for serialization.
     */
    private static final long serialVersionUID = -4213662197614198364L;

    /**
     * Hibernate session factory; it's never null.
     */
    private final SessionFactoryImplementor sessionFactory;

    /**
     * Default session context to use before creating a new session;
     * it's never null.
     */
    private final SpringSessionContext defaultSessionContext;

    /**
     * Context to store configured sessions; it's never null.
     */
    private final ManagedSessionContext localSessionContext;

    /**
     * Creates a new session context and sets the related session factory.
     *
     * @param theSessionFactory Context session factory. Cannot be null.
     */
    public TransactionAwareSessionContext(
            final SessionFactoryImplementor theSessionFactory) {
        Validate.notNull(theSessionFactory, "The session factory cannot be null.");

        defaultSessionContext = new SpringSessionContext(theSessionFactory);
        localSessionContext = new ManagedSessionContext(theSessionFactory);
        sessionFactory = theSessionFactory;
    }

    /**
     * Binds the configured session to Spring's transaction manager strategy
     * if there's no session.
     *
     * @return Returns the configured session, or the one managed by Spring.
     *         Never returns null.
     */
    public Session currentSession() {
        try {
            return defaultSessionContext.currentSession();
        } catch (HibernateException cause) {

            // There's no session bound to the current thread. Let's open one if
            // needed.
            if (ManagedSessionContext.hasBind(sessionFactory)) {
                return localSessionContext.currentSession();
            }

            Session session = sessionFactory.openSession();

            if (registerSynchronization(session)) {
                // Normalizes Session flush mode, defaulting it to AUTO. Required for
                // synchronization.
                FlushMode flushMode = session.getFlushMode();

                if (FlushMode.isManualFlushMode(flushMode)
                        && !TransactionSynchronizationManager
                        .isCurrentTransactionReadOnly()) {
                    session.setFlushMode(FlushMode.AUTO);
                }
            }
            ManagedSessionContext.bind(session);

            return session;
        }
    }

    /**
     * Registers transaction synchronization with session in order to clean
     * up and close the session when transaction finishes.
     *
     * @param session Session to register into transaction synchronization.
     *                Cannot be null.
     * @return Returns <code>true</code> if the session was register into any
     *         available synchronization strategy, <code>false</code> otherwise.
     */
    private boolean registerSynchronization(final Session session) {
        // Tries Spring's transaction manager synchronization.
        if (TransactionSynchronizationManager.isSynchronizationActive()) {

            // If it's allowed, registers synchronization to cleanup session.
            TransactionSynchronizationManager.registerSynchronization(
                    createTransactionSynchronization(session));
            return true;
        } else {
            // Tries JTA transaction manager synchronization.
            JtaPlatform jtaPlatform = sessionFactory.getServiceRegistry()
                    .getService(JtaPlatform.class);

            // If it's allowed, registers synchronization to cleanup session.
            if (jtaPlatform.canRegisterSynchronization()) {
                List<TransactionSynchronization> synchronizations;

                synchronizations = Arrays.asList(
                        createTransactionSynchronization(session));

                Synchronization jtaSync;
                jtaSync = new JtaAfterCompletionSynchronization(synchronizations);
                jtaPlatform.registerSynchronization(jtaSync);

                return true;
            }
        }
        return false;
    }

    /**
     * Creates a transaction synchronization object for the specified session.
     *
     * @param session Session to synchronize using the created object. Cannot be
     *                null.
     * @return A valid transaction synchronization. Never returns null.
     */
    private TransactionSynchronization createTransactionSynchronization(
            final Session session) {
        return new TransactionSynchronizationAdapter() {
            @Override
            public void afterCompletion(final int status) {
                session.close();
                ManagedSessionContext.unbind(sessionFactory);
            }
        };
    }
}