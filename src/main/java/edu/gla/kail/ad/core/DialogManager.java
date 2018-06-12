package edu.gla.kail.ad.core;

import edu.gla.kail.ad.core.Log.LogEntry;

import java.util.List;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * This class manages the conversation with different platforms - such as Dialogflow.
 * 1) Add the list of Dialogflow Agents by project Id and Key File directory
 * 2) initialiseDialogflowDialogManager
 * 3) Do Conversations
 * <p>
 * _mapOfSessionClientsAndSessionNames <project id, direction of the json file with authorization
 * keys for particular agent/project>
 * This class is responsible for storing the logs.
 **/
//TODO(Adam) - add description of all the variables, functions, classes etc.

public class DialogManager {
    final private LogEntry.Builder _logEntryBuilder;
    private List<DialogManagerInterface> listOfServiceInstances;
    private String _sessionId;

    /**
     * Constructor of this class creates LogEntry.Builder which is used for login of the entire
     * conversation.
     */
    public DialogManager() {
        startSession();
        _logEntryBuilder = LogEntry.newBuilder();
    }

    /**
     * This function creates a unique sessionId. The choice of the sessionId creator is to be
     * done later TODO(Adam)
     */
    public void startSession() {
        _sessionId = UUID.randomUUID().toString();
    }

    public void endSession() {
        //TODO(Adam): Creation of configuration file - to be done later on.
    }


    public void setUpDialogManagers(List<ServicesConfigurationTriplet>
                                            servicesConfigurationTripletList) throws Exception {

        for (ServicesConfigurationTriplet servicesConfigurationTriplet :
                servicesConfigurationTripletList) {
            //TODO (adam) check if the provided service exists before setting it up.
            //TODO configure the whatever
        }
        _dialogflowDialogManagerInstance = new DialogflowDialogManager(_sessionId,
                listOfProjectIdAndAuthorizationFile);
    }

    /**
     * Return the list of responses for a passed text textInput from all the agents
     * stored in the _mapOfSessionClientsAndSessionNames and calles storing all the responses in
     * the log file.
     * This function is also responsible for calling the logging function.
     * This function is responsible for storing the logs after each conversation.
     *
     * @param textInput
     * @return
     */
    //TODO passing input as requestLog probably
    public List<Log.ResponseLog.Builder> getResponsesFromDialogflowAgentsForTextInput(String textInput, String requestId, String langugeCode) throws Exception {
        //TODO(Adam) store/create request log

        _dialogflowDialogManagerInstance = checkNotNull(_dialogflowDialogManagerInstance,
                "DialogflowDialogManager not set up! Use the function " +
                        "setUpDialogflowDialogManager(DialogflowDialogManagerSetup) first.");
        _dialogflowDialogManagerInstance.getResponsesFromAgentsFromText(textInput, langugeCode);


        //TODO(Adam) store the conversation in the log files.

        //TODO(Adam) return them in the form of log? think about the type of the function - void,
        // list - of logs, what?
        throw new Exception("Doesn't return anything yet!");
        return null;
    }
    //raking function: take repeated ResponseLog candidate_response = 3;

}
