package edu.gla.kail.ad.service;

import com.google.cloud.Tuple;
import edu.gla.kail.ad.core.ConfigurationTuple;
import edu.gla.kail.ad.core.DialogAgentManager;
import edu.gla.kail.ad.core.Log.ResponseLog.ServiceProvider;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Class used to hold the instances of DialogAgentManager, which are unique for every session.
 * The current version creates unique DialogAgentManager for every user, not every session!
 */
final class DialogAgentManagerSingleton {
    // The map of userID and the DialogAgentManager instances assigned to each session
    // (currently user).
    private static Map<String, DialogAgentManager> _initializedManagers = new HashMap<>();
    private static DialogAgentManagerSingleton _instance;

    /**
     * @param userID - The identification String userID, which is sent by each user
     *         with every request.
     * @return DialogAgentManager - The instance of DialogAgentManager used for particular session
     *         (currently user).
     * @throws IOException - Thrown when setting up the agents is unsuccessful.
     */
    static synchronized DialogAgentManager getDialogAgentManager(String userID) throws
            IOException {
        if (_instance == null) {
            _instance = new DialogAgentManagerSingleton();
        }
        if (!_initializedManagers.containsKey(userID)) {
            DialogAgentManager dialogAgentManager = new DialogAgentManager();
            dialogAgentManager.setUpAgents(supportingFunctionToBeDeleted()); // TODO(Adam): Add a
            // functionality to setting up agents from the database
            _initializedManagers.put(userID, dialogAgentManager);
        }
        return _initializedManagers.get(userID);
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
        String currentClassPathFile = System.getProperty("user.dir");
        Path path = Paths.get(currentClassPathFile +
                "/agent-dialogue-core/src/main/resources/TestTextFiles" +
                "/ProjectIdAndJsonKeyFileLocations.txt");
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
                            projectIdAndJsonKey[2]));
                    configurationTuples.add(new ConfigurationTuple<>(ServiceProvider.DIALOGFLOW,
                            dialogflowProjectIdAndJsonKeyFileList));
                    break;
                case "DummyAgent":
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
                    break;
                default:
                    throw new IllegalArgumentException("The name of the agent is not correctly " +
                            "formatted or the agent type: " +
                            projectIdAndJsonKey[0] + " is not supported yet.");
            }
        }
        return configurationTuples;

    }


}
