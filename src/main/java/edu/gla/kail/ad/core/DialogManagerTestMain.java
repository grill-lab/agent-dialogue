package edu.gla.kail.ad.core;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/* Class for testing the functionality. */

public class DialogManagerTestMain {

    // map of agents?
    private static Map<String, String> _mapOfSessionClientsAndSessionNames = new HashMap();

    /*     Return random sessionId used to initialise the DialogflowDialogManager. */
    private static String getRandomNumber() {
        return UUID.randomUUID().toString();
    }

    /*   Add projectId and the directory of key files, stored in a text file with directory
       fileDirectory to the main HashMap of all Dialogflow Agents _nameOfFileWithProjectIdAndKeysLocations. */
    public static void readProjectIdAndKeyFileToHashMap(String fileDirectory) throws IOException {
        Path path = Paths.get(fileDirectory);
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        for (String line : lines) {
            String[] projectIdAndJsonKey = line.split(", ");
            _mapOfSessionClientsAndSessionNames.put(projectIdAndJsonKey[0], projectIdAndJsonKey[1]);
        }
    }

    public static void main(String[] args) throws Exception {
        String languageCode = "en-US";
        File currentClassPathFile = new File(DialogManagerTestMain.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParentFile();
        String testTextFileDirectory = currentClassPathFile.getParent() + "/src/main/resources/TestTextFiles/";
        String logFileDirectory = currentClassPathFile.getParent() + "/src/main/resources/LogFiles/"; //TODO (Adam) Is this line needed?

        String nameOfTestedFile = "SampleConversation.txt";
        String nameOfFileWithProjectIdAndKeysLocations = "ProjectIdAndJsonKeyFileLocations.txt";

        /* Add the Agents we want to test from text file: */
        readProjectIdAndKeyFileToHashMap(testTextFileDirectory + nameOfFileWithProjectIdAndKeysLocations);


        DialogManager dialogManager = new DialogManager();
        dialogManager.setUpDialogflowDialogManager(_mapOfSessionClientsAndSessionNames);

        /* Call the DialogflowManager on all sentences/lines stored in a text file. */
        Path path = Paths.get(testTextFileDirectory + nameOfTestedFile);
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        for (String line : lines) {
            //TODO(Adam) delete print statements - however, this is testing class
            System.out.println("TESTING CLASS OUTPUT: CURRENTLY HANDLING THE REQUEST FOR: " + line);
            dialogManager.getResponsesFromDialogflowAgentsForTextInput(line, getRandomNumber(), languageCode);
            System.out.println("TESTING CLASS OUTPUT: FINISHED HANDLING THE REQUEST FOR: " + line + "\n");
        }

    }
}
