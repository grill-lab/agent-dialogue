package edu.gla.kail.ad.core;

import com.google.protobuf.Timestamp;
import edu.gla.kail.ad.core.Client.Interaction;
import edu.gla.kail.ad.core.Client.InteractionRequest;
import edu.gla.kail.ad.core.Log.LogEntry;
import edu.gla.kail.ad.core.Log.RequestLog;
import edu.gla.kail.ad.core.Log.ResponseLog;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * This class manages the conversation with different Dialog Managers e.g. Dialogflow, Alexa.
 * Instruction of using:
 * 1) Set up the Dialog Managers using setUpDialogManagers function
 * 2) Call the getResponsesFromAgents for required inputs
 * 3) Dont't know yet, Rank?
 * <p>
 * This class is responsible for storing the logs.
 **/
//TODO(Adam) - add description of all the variables, functions, classes etc.

public class DialogManager {
    final private LogEntry.Builder _logEntryBuilder;
    private List<DialogManagerInterface> _listOfDialogManagers; // List of instances of used
    // Dialog Managers.
    private String _sessionId;
    private String _logsDirectory; //TODO(Adam): Implementation of writing to the file and
    // performing checks on the file.

    /**
     * Constructor of this class creates LogEntry.Builder which is used for login of the entire
     * conversation and assigns unique session ID, which is generated with startSession() function.
     */
    public DialogManager() {
        startSession();
        _logEntryBuilder = LogEntry.newBuilder();
    }

    /**
     * Create a unique sessionId. TODO(Adam) the choice of Session ID generator function.
     */
    public void startSession() {
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
     * Set up Dialog Managers, such as Dialogflow.
     *
     * @param configurationTuples
     * @throws Exception
     */
    public void setUpDialogManagers(List<ConfigurationTuple>
                                            configurationTuples) throws Exception {
        _listOfDialogManagers = new ArrayList();
        for (ConfigurationTuple configurationTuple :
                configurationTuples) {
            switch (configurationTuple.get_dialogManagerType()) {
                case DIALOGFLOW:
                    _listOfDialogManagers.add(new DialogflowDialogManager(_sessionId,
                            configurationTuple.get_parametersRequiredByTheAgent()));
                    break;
                default:
                    throw new Exception("The type of the Agent Provided \"" + configurationTuple
                            .get_dialogManagerType() + "\" is not currently supported!");
            }
        }
    }

    /**
     * Return the list of responses for a given request all the responses in
     * the log file.
     * Get Request from Client: and convert it to the RequestLog.
     * Store the logs after each conversation.
     *
     * @param interactionRequest
     * @return the list of ReponseLog instances with saved responses within them
     * @throws Exception
     */
    public List<ResponseLog> getResponsesFromAgents(InteractionRequest interactionRequest) throws
            Exception {
        /**
         * Convert InteractionRequest to RequestLog.
         */
        long millis = System.currentTimeMillis();
        Timestamp timestamp = Timestamp.newBuilder().setSeconds(millis / 1000)
                .setNanos((int) ((millis % 1000) * 1000000)).build();
        RequestLog requestLog = RequestLog.newBuilder()
                .setRequestId(generateRequestId()) //Assigning the request id function needs to
                // be implemented
                .setTime(timestamp)
                .setClientId(interactionRequest.getClientId())
                .setInteraction(interactionRequest.getInteraction()).build();
        //TODO(Adam) store request log


        if (checkNotNull(_listOfDialogManagers, "Dialog Managers are not set up! Use the function" +
                " setUpDialogManagers() first.").isEmpty()) {
            throw new Exception("The list of Dialog Managers is empty!");
        }

        List<ResponseLog> listOfResponseLogs = new ArrayList();
        Interaction interaction = requestLog.getInteraction();
        for (DialogManagerInterface dialogManagerInterfaceInstance : _listOfDialogManagers) {
            listOfResponseLogs.addAll(dialogManagerInterfaceInstance.getResponsesFromAgents
                    (interaction));
        }


        //TODO(Adam) store the conversation in the log files????.
        return listOfResponseLogs;
    }

    // TODO(Adam): Raking function;

}
