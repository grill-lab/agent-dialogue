package edu.gla.kail.ad.core;

import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

import edu.gla.kail.ad.core.Log.LogEntry;

/**
 * This class manages the conversation with different platforms - such as Dialogflow.
 * 1) Add the list of Dialogflow Agents by project Id and Key File directory
 * 2) initialiseDialogflowDialogManager
 * 3) Do Conversations
 * <p>
 * _mapOfSessionClientsAndSessionNames <project id, direction of the json file with authorization keys for particular agent/project>
 *     This class is responsible for storing the logs.
 **/
//TODO(Adam) - add description of all the variables, functions, classes etc.

public class DialogManager {
    private DialogflowDialogManager _dialogflowDialogManagerInstance;
    private String _languageCode;
    private String _sessionId;
    final private LogEntry.Builder _logEntryBuilder;

    // Initializer
    public DialogManager(DialogManagerSetup dialogManagerSetup) {
        _languageCode = dialogManagerSetup.get_languageCode();
        _sessionId = dialogManagerSetup.get_sessionId();
        _logEntryBuilder = dialogManagerSetup.get_logEntry();
    }

    // TODO(adam) passing the log function ot the dialogflow initialiser, so that we can use one log class per session
    /* Creating an instance of DialogflowDialogManager and ConversationLogger used for the purpose of one session.
     Takes used languageCode and logFileLocation which is the directory to store log files into.*/
    public void setUpDialogflowDialogManager(Map<String, String> mapOfProjectIdAndAuthorizationFile) throws Exception {
        DialogflowDialogManagerSetup dialogflowDialogManagerSetup = new DialogflowDialogManagerSetup(
                _languageCode, _sessionId, mapOfProjectIdAndAuthorizationFile, _logEntryBuilder);
        _dialogflowDialogManagerInstance = new DialogflowDialogManager(dialogflowDialogManagerSetup);
    }

    /**
     * Return the list of responses for a passed text textInput from all the agents
     *       stored in the _mapOfSessionClientsAndSessionNames and calles storing all the responses in the log file.
     *       This function is also responsible for calling the logging function.
     *       This function is responsible for storing the logs after each conversation.
     * @param textInput
     * @return
     */
    public List<Log.ResponseLog.Builder> getResponsesFromDialogflowAgentsForTextInput(String textInput, String requestId) throws Exception {
        //TODO(Adam) store/create request log

        this._dialogflowDialogManagerInstance = checkNotNull(_dialogflowDialogManagerInstance,
                "DialogflowDialogManager not set up! Use the function setUpDialogflowDialogManager(DialogflowDialogManagerSetup) first.");
        _dialogflowDialogManagerInstance.getResponsesFromAgentsFromText(textInput);


        //TODO(Adam) store the conversation in the log files.

        //TODO(Adam) return them in the form of log? think about the type of the function - void, list - of logs, what?
        throw new Exception("Doesn't return anything yet!");
        return null;
    }
    //raking function: take repeated ResponseLog candidate_response = 3;

}
