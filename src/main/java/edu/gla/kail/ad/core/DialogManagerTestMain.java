package edu.gla.kail.ad.core;

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
    public static Map<String, String> _agentsByProjectIdAndKeyMap = new HashMap<String, String>();

    /*     Return random sessionId used to initialise the DialogflowDialogManager. */
    public static String getRandomSessionIdAsString() {
        return UUID.randomUUID().toString();
    }

    /*   Add projectId and the directory of key files, stored in a text file with directory
       fileDirectory to the main HashMap of all Dialogflow Agents _nameOfFileWithProjectIdAndKeysLocations. */
    public static void readProjectIdAndKeyFileToHashMap(String fileDirectory) throws IOException {
        Path path = Paths.get(fileDirectory);
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        for (String line : lines) {
            String[] projectIdAndJsonKey = line.split(", ");
            _agentsByProjectIdAndKeyMap.put(projectIdAndJsonKey[0], projectIdAndJsonKey[1]);
        }
    }

    public static void main(String[] args) throws Exception {
        String languageCode = "en-US";
        String logFileDirectory = "/Users/Adam/Documents/Internship/";
        String testedTextFileDirectory = "/Users/Adam/Documents/Internship/GitHub/agent-dialogue/" +
                "Metabot_prototype/src/main/java/Metabot_core/TestTextFile";
        String nameOfTestedFile = "SampleConversation.txt";
        String nameOfFileWithProjectIdAndKeysLocations = "ProjectIdAndJsonKeyFileLocations.txt";

        DialogManager dialogManager = new DialogManager();
        dialogManager.initialiseDialogflowDialogManagerInstanceAndLogger(languageCode, logFileDirectory, getRandomSessionIdAsString());

        /* Add the Agents we want to test from text file: */
        readProjectIdAndKeyFileToHashMap(testedTextFileDirectory + nameOfFileWithProjectIdAndKeysLocations);

        /* Call the DialogflowManager on all sentences/lines stored in a text file. */
        Path path = Paths.get(testedTextFileDirectory + nameOfTestedFile);
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        for (String line : lines) {
            //TODO(Adam) delete print statements - however, this is testing class
            System.out.println("TESTING CLASS OUTPUT: CURRENTLY HANDLING THE REQUEST FOR: " + line);
            dialogManager.getResponsesFromDialogflowAgentsForTextInput(line, _agentsByProjectIdAndKeyMap);
            System.out.println("TESTING CLASS OUTPUT: FINISHED HANDLING THE REQUEST FOR: " + line + "\n");
        }

    }
}
