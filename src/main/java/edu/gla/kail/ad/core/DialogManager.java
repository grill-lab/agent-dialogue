package edu.gla.kail.ad.core;

import com.google.protobuf.Timestamp;
import edu.gla.kail.ad.core.Client.InputInteraction;
import edu.gla.kail.ad.core.Client.InteractionRequest;
import edu.gla.kail.ad.core.Log.RequestLog;
import edu.gla.kail.ad.core.Log.ResponseLog;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * This class manages the conversation with different particular Dialog Managers e.g. Dialogflow,
 * Alexa.
 * Assign unique Session ID for each session (List of Turns, as specified in log.proto file).
 * Assign unique Request ID for each request sent to all the specified Dialog Managers.
 * This class is responsible for storing the logs.
 * <p>
 * Instruction of using:
 * 1) Set up the Dialog Managers using setUpDialogManagers function
 * 2) Call the getResponsesFromAgents for required inputs
 * 3) Use ranking function. TODO(Adam): further part needs to be implemented
 **/

public class DialogManager {
    // List of instances of used Dialog Managers.
    private List<DialogManagerInterface> _listOfDialogManagers;
    private String _sessionId;

    /**
     * Create a unique session ID generated with startSession() function.
     */
    public DialogManager() {
        startSession();
    }

    /**
     * Create a unique sessionId. TODO(Adam) the choice of Session ID generator function.
     */
    private void startSession() {
        _sessionId = getRandomNumberAsString();
    }

    /**
     * Function called at the end of the session, which will do sth - not known now.
     */
    public void endSession() {
        //TODO(Adam): Creation of configuration file - to be done later on.
        //TODO(Adam): Updating/Saving to the log file?
    }

    private String getRandomNumberAsString() {
        return UUID.randomUUID().toString();
    }

    //TODO(Adam): generateRequestId needs to be implemented
    private String generateRequestId() {
        return getRandomNumberAsString();
    }

    /**
     * Set up (e.g. authenticate agents) a particular Dialog Managers (such as
     * DialogflowDialogManager) and their Agents.
     *
     * @param configurationTuples The list stores the entities of ConfigurationTuple, which holds
     *                            data required by each particular Dialog Manager
     * @throws Exception It is thrown when the type of the Dialog Manager passed in the
     *                   configurationTuples list is not supported (yet).
     */
    private void setUpDialogManagers(List<ConfigurationTuple>
                                             configurationTuples) throws Exception {
        _listOfDialogManagers = new ArrayList();
        for (ConfigurationTuple configurationTuple :
                configurationTuples) {
            switch (configurationTuple.get_dialogManagerType()) {
                case DIALOGFLOW:
                    _listOfDialogManagers.add(new DialogflowDialogManager(_sessionId,
                            configurationTuple.get_particularDialogManagerSpecificData()));
                    break;
                default:
                    throw new IllegalArgumentException("The type of the Dialog Manager Provided " +
                            "\"" +
                            configurationTuple
                                    .get_dialogManagerType() + "\" is not currently supported " +
                            "(yet)!");
            }
        }
    }

    /**
     * Get Request from Client and convert it to the RequestLog.
     * Return the list of responses for a given request.
     * TODO(Adam): Maybe store the logs after each conversation; need to decide later on.
     *
     * @param interactionRequest
     * @return The list of responses of each agent of each particular Dialog Manager specified
     * during the setUpDialogManagers(...) function call.
     * @throws Exception It is thrown when the setUpDialogManagers wasn't called before calling
     *                   this function.
     */
    public List<ResponseLog> getResponsesFromAgents(InteractionRequest interactionRequest) throws
            Exception {
        if (checkNotNull(_listOfDialogManagers, "Dialog Managers are not set up! Use the function" +
                " setUpDialogManagers() first.").isEmpty()) {
            throw new Exception("The list of Dialog Managers is empty!");
        }
        // Convert InteractionRequest to RequestLog.
        long millis = System.currentTimeMillis();
        Timestamp timestamp = Timestamp.newBuilder().setSeconds(millis / 1000)
                .setNanos((int) ((millis % 1000) * 1000000)).build();
        RequestLog requestLog = RequestLog.newBuilder()
                .setRequestId(generateRequestId())
                .setTime(timestamp)
                .setClientId(interactionRequest.getClientId())
                .setInteraction(interactionRequest.getInteraction()).build();

        // Store the responses from the Agents in a list.
        List<ResponseLog> listOfResponseLogs = new ArrayList();
        InputInteraction inputInteraction = requestLog.getInteraction();
        for (DialogManagerInterface dialogManagerInterfaceInstance : _listOfDialogManagers) {
            listOfResponseLogs.addAll(dialogManagerInterfaceInstance.getResponsesFromAgents
                    (inputInteraction));
        }
        return listOfResponseLogs;
    }

    // TODO(Adam): Raking function;

    //TODO(Adam): store the conversation in the log as a single Turn
}
