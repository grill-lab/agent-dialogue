package edu.gla.kail.ad.service;

import com.google.cloud.Tuple;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import edu.gla.kail.ad.core.ConfigurationTuple;
import edu.gla.kail.ad.core.DialogAgentManager;
import edu.gla.kail.ad.core.Log.ResponseLog.ServiceProvider;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Class used to hold the instances of DialogAgentManager for each session ID
 * over certain period of time after the last activity, which are unique for every session.
 */
final class DialogAgentManagerSingleton {
    private static DialogAgentManagerSingleton _instance;
    private static int _MAX_NUMBER_OF_SIMULTANEOUS_CONVERSATIONS = 10;
    private static int _SESSION_TIMEOUT_IN_MINUTES = 5;

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
                            dialogAgentManager.setUpAgents(supportingFunctionToBeDeleted());
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

    /**
     * The supporting method used for the purposes of testing.
     * When the database with the list of available agents is set up, this method should be
     * deleted.
     * TODO(Adam): Delete this method after the database with agents is set up.
     *
     * @return List<ConfigurationTuple> - the tuples used to set up agents
     * @throws NullPointerException - Throw when the file with ProjectIds and Json
     *         Authorisation file is not accessible.
     */
    private static List<ConfigurationTuple> supportingFunctionToBeDeleted() {
        List<ConfigurationTuple> configurationTuples = new ArrayList<>();
        Path projectCoreDir = Paths
                .get(DialogAgentManagerSingleton
                        .class
                        .getProtectionDomain()
                        .getCodeSource()
                        .getLocation()
                        .getPath())
                .getParent()
                .getParent();
        Path path = Paths.get(projectCoreDir +
                "/src/main/resources/TestTextFiles/ProjectIdAndJsonKeyFileLocations.txt");
        List<String> lines;
        try {
            lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        } catch (IOException ioException) {
            ioException.printStackTrace();
            lines = null;
        }
        checkNotNull(lines, "Reading the file with ProjectIds and Json Authorisation file " +
                "locations failed!");
        for (String line : lines) {
            String[] projectIdAndJsonKey = line.split(",");
            switch (projectIdAndJsonKey[0]) {
                case "Dialogflow":
                    List<Tuple<String, String>> dialogflowProjectIdAndJsonKeyFileList = new
                            ArrayList<>();
                    dialogflowProjectIdAndJsonKeyFileList.add(Tuple.of(projectIdAndJsonKey[1],
                            projectCoreDir + projectIdAndJsonKey[2]));
                    configurationTuples.add(new ConfigurationTuple<>(ServiceProvider.DIALOGFLOW,
                            dialogflowProjectIdAndJsonKeyFileList));
                    break;
/*                case "DummyAgent":
                    configurationTuples.add(new ConfigurationTuple<>(ServiceProvider
                            .DUMMYAGENT, null));
                    break;
                case "FailingExceptionDummyAgent":
                    configurationTuples.add(new ConfigurationTuple<>(ServiceProvider
                            .FAILINGEXCEPTIONDUMMYAGENT, null));
                    break;
                case "FailingNullDummyAgent":
                    configurationTuples.add(new ConfigurationTuple<>(ServiceProvider
                            .FAILINGNULLDUMMYAGENT, null));
                    break;
                case "FailingTimeDummyAgent":
                    configurationTuples.add(new ConfigurationTuple<>(ServiceProvider
                            .FAILINGTIMEDUMMYAGENT, null));
                    break;*/
                default:
                    throw new IllegalArgumentException("The name of the agent is not correctly " +
                            "formatted or the agent type: " +
                            projectIdAndJsonKey[0] + " is not supported yet.");
            }
        }
        return configurationTuples;
    }
}
