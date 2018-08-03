package edu.gla.kail.ad.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import edu.gla.kail.ad.CoreConfigOuterClass.Agent;
import edu.gla.kail.ad.core.DialogAgentManager;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Class used to hold the instances of DialogAgentManager for each session ID
 * over certain period of time after the last activity, which are unique for every session.
 */
final class DialogAgentManagerSingleton {
    private static DialogAgentManagerSingleton _instance;
    private static int _MAX_NUMBER_OF_SIMULTANEOUS_CONVERSATIONS = PropertiesSingleton
            .getCoreConfig().getMaxNumberOfSimultaneousConversations();
    private static int _SESSION_TIMEOUT_IN_MINUTES = PropertiesSingleton.getCoreConfig()
            .getSessionTimeoutMinutes();

    // The cache mapping userID and the DialogAgentManager instances assigned to each user.
    private static LoadingCache<String, DialogAgentManager> _initializedManagers = CacheBuilder
            .newBuilder()
            .maximumSize(_MAX_NUMBER_OF_SIMULTANEOUS_CONVERSATIONS)
            .expireAfterAccess(_SESSION_TIMEOUT_IN_MINUTES, TimeUnit.MINUTES)
            .removalListener(new RemovalListener<String, DialogAgentManager>() {
                public void onRemoval(RemovalNotification<String, DialogAgentManager> removal) {
                    removal.getValue().endSession();
                }
            })
            .build(
                    new CacheLoader<String, DialogAgentManager>() {
                        public DialogAgentManager load(String key) throws IOException {
                            DialogAgentManager dialogAgentManager = new DialogAgentManager();
                            dialogAgentManager.setUpAgents((List<Agent>) PropertiesSingleton
                                    .getCoreConfig()
                                    .getAgentsList());
                            // TODO(Adam): Add a functionality to setting up agents from the
                            // database once we have it.
                            return dialogAgentManager;
                        }
                    });


    /**
     * @param userId - The identification String userID, which is sent by each user
     *         with every request.
     * @return DialogAgentManager - The instance of DialogAgentManager used for particular session.
     * @throws ExecutionException - Thrown when setting up the agents is unsuccessful or max
     *         number of ongoing conversations has been reached.
     */
    static synchronized DialogAgentManager getDialogAgentManager(String userId) throws Exception {
        if (_instance == null) {
            _instance = new DialogAgentManagerSingleton();
        }
        if (_initializedManagers.size() == _MAX_NUMBER_OF_SIMULTANEOUS_CONVERSATIONS) {
            throw new Exception("The maximum number of conversations have been reached - wait " +
                    "some time or quit coversations on other user accounts.");
        }
        return _initializedManagers.get(userId);
    }

    /**
     * Delete the instance of DialogAgentManager for the session corresponding to passed userID.
     *
     * @param userId - The identification String userID, which is sent by each user
     *         with every request.
     */
    static synchronized boolean deleteDialogAgentManager(String userId) {
        _initializedManagers.invalidate(userId);
        return true;
    }
}
