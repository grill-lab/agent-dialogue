package edu.gla.kail.ad.core;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/*  This class Manages the conversation with different platforms - such as Dialogflow.
 *  1) Add the list of Dialogflow Agents by project Id and Key File directory
 *  2) initialiseDialogflowDialogManager
 *  3) Do Conversations
 *  etc. */


public class DialogManager {
    private ConversationLogger _conversationLogger;
    private static Map<String, String> _listOfDialogflowAgentsByProjectIdAndKeyFile = new HashMap<String, String>() {};
    private DialogflowDialogManager _dialogflowDialogManagerInstance;

    // Initialiser
    public DialogManager() {
    }

    // Returns random sessionId used to initialise the DialogflowDialogManager.
    private String getRandomSessionIdAsString() {
        return UUID.randomUUID().toString();
    }

    // Creating an instance of DialogflowDialogManager and ConversationLogger used for the purpose of one session.
    // Takes used languageCode and logFileLocation which is the directory to store log files into.
    public void initialiseDialogflowDialogManagerInstanceAndLogger(String languageCode, String logFileLocation) {
        _dialogflowDialogManagerInstance = new DialogflowDialogManager(languageCode, getRandomSessionIdAsString());
        _conversationLogger = new ConversationLogger(logFileLocation);
    }

    // Adding Agents to the list of agents used with the request.
    public void addDialogflowAgentByProjectId(String projecId, String jsonKeyFileLocation) {
        _listOfDialogflowAgentsByProjectIdAndKeyFile.put(projecId, jsonKeyFileLocation);
    }

    // Returns the list of responses for a passed text textInput from all the agents
    // stored in the _listOfDialogflowAgentsByProjectIdAndKeyFile and calles storing all the responses in the log file.
    public List<ResponseDataStructure> getResponsesFromDialogflowAgentsForTextInput(String textInput) throws IOException {
        // This function is also responsible for calling the logging function
        List<ResponseDataStructure> listOfResponses = _dialogflowDialogManagerInstance.
                getResponsesFromAgentsFromText(textInput, _listOfDialogflowAgentsByProjectIdAndKeyFile);
        storeResponsesInLogs(listOfResponses);
        return listOfResponses;
    }

    // Calls the function of ConversationLogger to store the log for each response passed in the list listOfDialogFlowRespons.
    public void storeResponsesInLogs(List<ResponseDataStructure> listOfDialogFlowRespons) {
        for (ResponseDataStructure responseDataStructure : listOfDialogFlowRespons) {
            _conversationLogger.storeDialogflowResponse(responseDataStructure);
        }
    }

}
