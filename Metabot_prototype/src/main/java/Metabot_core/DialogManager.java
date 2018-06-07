package Metabot_core;

import java.io.IOException;
import java.util.*;

/*  1) Add the list of Dialogflow Agents by project Id and Key File directory
 *  2) initialiseDialogflowDialogManager
 *  3) Do Conversations
 *  etc. */


public class DialogManager {
    private ConversationLogger _conversationLogger;

    private static Map<String, String> _listOfDialogflowAgentsByProjectIdAndKeyFile = new HashMap<String, String>() {
    };
    private DialogflowDialogManager _dialogflowDialogManagerInstance;

    public DialogManager() {
    }

    private String getRandomSessionIdAsString() {
        return UUID.randomUUID().toString();
    }


    public void initialiseDialogflowDialogManagerInstanceAndLogger(String languageCode, String logFileLocation) {
        _dialogflowDialogManagerInstance = new DialogflowDialogManager(languageCode, getRandomSessionIdAsString());
        _conversationLogger = new ConversationLogger(logFileLocation);
    }

    public void addDialogflowAgentByProjectId(String projecId, String jsonKeyFileLocation) {
        _listOfDialogflowAgentsByProjectIdAndKeyFile.put(projecId, jsonKeyFileLocation);
    }

    public List<ResponseDataStructure> getResponsesFromDialogflowAgentsForTextInput(String textInput) throws IOException {
        // This function is also responsible for calling the logging function
        List<ResponseDataStructure> listOfResponses = _dialogflowDialogManagerInstance.
                getResponsesFromAgentsFromText(textInput, _listOfDialogflowAgentsByProjectIdAndKeyFile);
        storeResponsesInLogs(listOfResponses);
        return listOfResponses;
    }

    public void storeResponsesInLogs(List<ResponseDataStructure> listOfDialogFlowRespons) {
        for (ResponseDataStructure responseDataStructure : listOfDialogFlowRespons) {
            _conversationLogger.storeDialogflowResponse(responseDataStructure);
        }
    }

}
