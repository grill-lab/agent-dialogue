package edu.gla.kail.ad.core;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * This class manages the conversation with different platforms - such as Dialogflow.
 * 1) Add the list of Dialogflow Agents by project Id and Key File directory
 * 2) initialiseDialogflowDialogManager
 * 3) Do Conversations
 * <p>
 * _agentsByProjectIdAndKeyMap <project id, direction of the json file with authorization keys for particular agent/project>
 **/
//TODO(Adam) - add description of all the variables, functions, classes etc.

public class DialogManager {
    private DialogflowDialogManager _dialogflowDialogManagerInstance;

    // Initializer
    public DialogManager() {
    }

    /* Creating an instance of DialogflowDialogManager and ConversationLogger used for the purpose of one session.
     Takes used languageCode and logFileLocation which is the directory to store log files into.*/
    public void setUpDialogflowDialogManager(DialogflowDialogManagerSetup dialogflowDialogManager) throws Exception {
        _dialogflowDialogManagerInstance = new DialogflowDialogManager(dialogflowDialogManager);
    }

    /*  Return the list of responses for a passed text textInput from all the agents
      stored in the _agentsByProjectIdAndKeyMap and calles storing all the responses in the log file.
      This function is also responsible for calling the logging function. */
    public List<ResponseDataStructure> getResponsesFromDialogflowAgentsForTextInput(String textInput) throws Exception {
        this._dialogflowDialogManagerInstance = checkNotNull(_dialogflowDialogManagerInstance,
                "DialogflowDialogManager not set up! Use the function setUpDialogflowDialogManager(DialogflowDialogManagerSetup) first.");
        List<ResponseDataStructure> listOfResponses = _dialogflowDialogManagerInstance.
                getResponsesFromAgentsFromText(textInput);
        storeResponsesInLogs(listOfResponses);
        return listOfResponses;
    }

    /* Call the function of ConversationLogger to store the log for each response passed in the list listOfDialogFlowRespons.*/
    public void storeResponsesInLogs(List<ResponseDataStructure> listOfDialogFlowRespons) throws Exception {
        for (ResponseDataStructure responseDataStructure : listOfDialogFlowRespons) {
            _conversationLogger.writeConversationResponse(responseDataStructure);
        }
    }

}
