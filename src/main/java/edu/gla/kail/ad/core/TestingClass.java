package edu.gla.kail.ad.core;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Class for testing the functionality

public class TestingClass {
    public static String _languageCode = "en-US";
    public static String _logFileDirectory = "/Users/Adam/Documents/Internship/";
    public static String _testedTextFileDirectory = "/Users/Adam/Documents/Internship/GitHub/agent-dialogue/" +
            "Metabot_prototype/src/main/java/Metabot_core/TestTextFile";
    public static String _nameOfTestedFile = "SampleConversation.txt";
    public static String _nameOfFileWithProjectIdAndKeysLocations = "ProjectIdAndJsonKeyFileLocations.txt";
    public static Map<String, String> _listOfDialogflowAgentsByProjectIdAndKeyFile = new HashMap<String, String>() {
    };

    // Adds projectId and the directory of key files, stored in a text file with directory
    // fileDirectory to the main HashMap of all Dialogflow Agents _nameOfFileWithProjectIdAndKeysLocations.
    public static void readProjectIdAndKeyFileToHashMap(String fileDirectory) throws IOException {
        Path path = Paths.get(fileDirectory);
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        for (String line : lines) {
            String[] projectIdAndJsonKey = line.split(", ");
            _listOfDialogflowAgentsByProjectIdAndKeyFile.put(projectIdAndJsonKey[0], projectIdAndJsonKey[1]);
        }
    }


    // Runs the test class.
    public static void main(String[] args) throws IOException {
        DialogManager dialogManager = new DialogManager();
        dialogManager.initialiseDialogflowDialogManagerInstanceAndLogger(_languageCode, _logFileDirectory);

        // Add the Agents we want to test from text file:
        readProjectIdAndKeyFileToHashMap(_testedTextFileDirectory + _nameOfFileWithProjectIdAndKeysLocations);
        for (Map.Entry<String, String> agentInformation : _listOfDialogflowAgentsByProjectIdAndKeyFile.entrySet()) {
            dialogManager.addDialogflowAgentByProjectId(agentInformation.getKey(), agentInformation.getValue());
        }

        // Call the DialogflowManager on all sentences/lines stored in a text file.
        Path path = Paths.get(_testedTextFileDirectory + _nameOfTestedFile);
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        for (String line : lines) {
            //TODO delete print statements - however, this is testing class
            System.out.println("TESTING CLASS OUTPUT: CURRENTLY HANDLING THE REQUEST FOR: " + line);
            dialogManager.getResponsesFromDialogflowAgentsForTextInput(line);
            System.out.println("TESTING CLASS OUTPUT: FINISHED HANDLING THE REQUEST FOR: " + line + "\n");
        }

    }
}
