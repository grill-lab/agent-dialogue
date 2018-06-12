package edu.gla.kail.ad.core;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.cloud.Tuple;
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
    private List<Object> listOfServicesInstances;
    private DialogflowDialogManager _dialogflowDialogManagerInstance; //TODO to be deleted and added in the list?
    private String _sessionId;
    final private LogEntry.Builder _logEntryBuilder;

    /**
     * Constructor of this class creates LogEntry.Builder which is used for login of the entire conversation.
     */
    public DialogManager() {
        startSession();
        _logEntryBuilder = LogEntry.newBuilder();
    }

    /**
     * This function creates a unique sessionId. The choice of the sessionId creator is to be done later TODO(Adam)
     */
    public void startSession() {
        _sessionId = UUID.randomUUID().toString();
    }
    public void endSession() {
        //TODO(Adam) - creation of configuration file?
    }


    public void setUpDialogManagers(List<Triplet<String, String>> listOfProjectIdAndAuthorizationFile) throws Exception {
        _dialogflowDialogManagerInstance = new DialogflowDialogManager(_sessionId, listOfProjectIdAndAuthorizationFile);
    }

    /**
     * Return the list of responses for a passed text textInput from all the agents
     *       stored in the _mapOfSessionClientsAndSessionNames and calles storing all the responses in the log file.
     *       This function is also responsible for calling the logging function.
     *       This function is responsible for storing the logs after each conversation.
     * @param textInput
     * @return
     */
    //TODO passing input as requestLog probably
    public List<Log.ResponseLog.Builder> getResponsesFromDialogflowAgentsForTextInput(String textInput, String requestId, String langugeCode) throws Exception {
        //TODO(Adam) store/create request log

        _dialogflowDialogManagerInstance = checkNotNull(_dialogflowDialogManagerInstance,
                "DialogflowDialogManager not set up! Use the function setUpDialogflowDialogManager(DialogflowDialogManagerSetup) first.");
        _dialogflowDialogManagerInstance.getResponsesFromAgentsFromText(textInput, langugeCode);


        //TODO(Adam) store the conversation in the log files.

        //TODO(Adam) return them in the form of log? think about the type of the function - void, list - of logs, what?
        throw new Exception("Doesn't return anything yet!");
        return null;
    }
    //raking function: take repeated ResponseLog candidate_response = 3;

}
