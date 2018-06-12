package edu.gla.kail.ad.core;

import com.google.cloud.Tuple;
import com.google.protobuf.Timestamp;
import edu.gla.kail.ad.core.Client.InputInteraction;
import edu.gla.kail.ad.core.Client.InteractionRequest;
import edu.gla.kail.ad.core.Client.InteractionType;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Class for testing the functionality.
 */
public class DialogManagerTestMain {
    private static List<ConfigurationTuple> _configurationTuples;

    /**
     * Add ConfigurationTuples to the _configurationTuples.
     * Function prone to crashes.
     *
     * @param fileDirectory
     * @throws IOException
     */
    private static void readProjectIdAndKeyFileToHashMap(String fileDirectory) throws Exception {
        _configurationTuples = new ArrayList();
        Path path = Paths.get(fileDirectory);
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        List<Tuple<String, String>> dialogflowProjectIdAndJsonKeyFileList = new ArrayList();
        for (String line : lines) {
            String[] projectIdAndJsonKey = line.split(", ");
            switch (projectIdAndJsonKey[0]) {
                case "Dialogflow":
                    dialogflowProjectIdAndJsonKeyFileList.add(Tuple.of(projectIdAndJsonKey[1],
                            projectIdAndJsonKey[2]));
                    break;
                default:
                    throw new IllegalArgumentException("The given Dialog Manager is not supported" +
                            " yet.");
            }
        }
        _configurationTuples.add(new ConfigurationTuple(SupportedDialogManagers.DIALOGFLOW,
                dialogflowProjectIdAndJsonKeyFileList));
    }

    private static String getRandomNumberAsString() {
        return UUID.randomUUID().toString();
    }

    public static void main(String[] args) throws Exception {
        String languageCode = "en-US";
        String deviceType = "iPhone Google Assistant";
        File currentClassPathFile = new File(DialogManagerTestMain.class.getProtectionDomain()
                .getCodeSource().getLocation().getPath()).getParentFile();
        String testTextFileDirectory = currentClassPathFile.getParent() +
                "/src/main/resources/TestTextFiles/";
        String logFileDirectory = currentClassPathFile.getParent() +
                "/src/main/resources/LogFiles/"; //TODO (Adam) Is this line needed?

        String nameOfTestedFile = "SampleConversation.txt";
        String nameOfFileWithProjectIdAndKeysLocations = "ProjectIdAndJsonKeyFileLocations.txt";

        // Add the Agents we want to test from text file.
        readProjectIdAndKeyFileToHashMap(testTextFileDirectory +
                nameOfFileWithProjectIdAndKeysLocations);


        DialogManager dialogManager = new DialogManager();
        dialogManager.setUpDialogManagers(_configurationTuples);
        long millis = System.currentTimeMillis();
        Timestamp timestamp = Timestamp.newBuilder().setSeconds(millis / 1000)
                .setNanos((int) ((millis % 1000) * 1000000)).build();

        // Call the DialogflowManager on all sentences/lines stored in a text file.
        Path path = Paths.get(testTextFileDirectory + nameOfTestedFile);
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        for (String line : lines) {
            //TODO(Adam) delete print statements - however, this is testing class
            System.out.println("TESTING CLASS OUTPUT: CURRENTLY HANDLING THE REQUEST FOR: " + line);
            InteractionRequest interactionRequest = InteractionRequest.newBuilder()
                    .setTime(timestamp)
                    .setClientId(getRandomNumberAsString())
                    .setInteraction(InputInteraction.newBuilder()
                            .setType(InteractionType.TEXT)
                            .setText(line)
                            .setDeviceType(deviceType)
                            .setLanguageCode(languageCode))
                    .build();
            dialogManager.getResponsesFromAgents(interactionRequest);
            System.out.println("TESTING CLASS OUTPUT: FINISHED HANDLING THE REQUEST FOR: " + line
                    + "\n");
        }
    }
}
