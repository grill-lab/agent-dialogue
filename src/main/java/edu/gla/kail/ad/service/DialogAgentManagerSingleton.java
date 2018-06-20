package edu.gla.kail.ad.service;

import com.google.cloud.Tuple;
import edu.gla.kail.ad.core.ConfigurationTuple;
import edu.gla.kail.ad.core.DialogAgentManager;
import edu.gla.kail.ad.core.DialogAgentManagerTestMain;
import edu.gla.kail.ad.core.SupportedAgentTypes;

import java.io.File;
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
 * The current version creates unique DialogAgentManager for every client, not every session!
 */
final class DialogAgentManagerSingleton {
    // The map of clientId and the DialogAgentManager instances assigned to each session
    // (currently client).
    private static Map<String, DialogAgentManager> _initializedManagers = new HashMap();
    private static DialogAgentManagerSingleton _instance;

    /**
     * @param clientId - The identification String ClientID, which is sent by each client
     *         with every request.
     * @return DialogAgentManager - The instance of DialogAgentManager used for particular session
     *         (currently client).
     * @throws IOException
     */
    static synchronized DialogAgentManager getDialogAgentManager(String clientId) throws
            IOException {
        if (_instance == null) {
            _instance = new DialogAgentManagerSingleton();
        }
        if (!_initializedManagers.containsKey(clientId)) {
            DialogAgentManager dialogAgentManager = new DialogAgentManager();
            dialogAgentManager.setUpAgents(supportingFunctionToBeDeleted()); // TODO(Adam): Add a
            // functionality to setting up agents from the database
            _initializedManagers.put(clientId, dialogAgentManager);
        }
        return _initializedManagers.get(clientId);
    }

    /**
     * The supporting function used for the purposes of testing.
     * When the database with the list of available agents is set up, this function should be
     * deleted.
     * TODO(Adam): Delete this funtion after the database with agents is set up.
     *
     * @return List<ConfigurationTuple> - the tuples used to set up agents
     * @throws IOException
     */
    private static List<ConfigurationTuple> supportingFunctionToBeDeleted() {
        List<ConfigurationTuple> configurationTuples = new ArrayList();
        File currentClassPathFile = new File(DialogAgentManagerTestMain.class.getProtectionDomain()
                .getCodeSource().getLocation().getPath()).getParentFile();
        Path path = Paths.get(currentClassPathFile.getParent() +
                "/src/main/resources/TestTextFiles/ProjectIdAndJsonKeyFileLocations.txt");
        List<String> lines;
        try {
            lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        } catch (IOException ioException) {
            ioException.printStackTrace();
            lines = null;
        }
        checkNotNull(lines, "Reading the file with ProjectIds and Json Authrisation file locations failed!");
        for (String line : lines) {
            String[] projectIdAndJsonKey = line.split(",");
            switch (projectIdAndJsonKey[0]) {
                case "Dialogflow":
                    List<Tuple<String, String>> dialogflowProjectIdAndJsonKeyFileList = new
                            ArrayList();
                    dialogflowProjectIdAndJsonKeyFileList.add(Tuple.of(projectIdAndJsonKey[1],
                            projectIdAndJsonKey[2]));
                    configurationTuples.add(new ConfigurationTuple(SupportedAgentTypes.DIALOGFLOW,
                            dialogflowProjectIdAndJsonKeyFileList));
                    break;
                case "DummyAgent":
                    configurationTuples.add(new ConfigurationTuple(SupportedAgentTypes
                            .DUMMYAGENT, null));
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
