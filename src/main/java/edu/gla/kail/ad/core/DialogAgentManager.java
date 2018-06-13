package edu.gla.kail.ad.core;

import com.google.cloud.Tuple;
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
 * This class manages the conversation with Agents of different type e.g. Dialogflow or Alexa.
 * Assign unique Session ID for each session (List of Turns, as specified in log.proto file).
 * Assign unique Request ID for each request sent to all the specified Agents.
 * This class is responsible for storing the logs.
 * <p>
 * Instruction of using:
 * 1) Set up the Dialog Agents using setUpAgents function
 * 2) Call the getResponseFromAgent for required inputs
 * 3) Use ranking function. TODO(Adam): further part needs to be implemented
 **/

public class DialogAgentManager {
    // List of instances of used Dialog Agents.
    private List<AgentInterface> _listOfAgents;
    private String _sessionId;

    /**
     * Create a unique session ID generated with startSession() function.
     */
    public DialogAgentManager() {
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
     * Set up (e.g. authenticate) all Agents and store them to the list of agents.
     *
     * @param configurationTuples The list stores the entities of ConfigurationTuple, which holds
     *                            data required by each Agent.
     * @throws Exception It is thrown when the type of the DialogManager passed in the
     *                   configurationTuples list is not supported (yet).
     */
    public void setUpAgents(List<ConfigurationTuple> configurationTuples) throws Exception {
        _listOfAgents = new ArrayList();
        for (ConfigurationTuple configurationTuple : configurationTuples) {
            switch (configurationTuple.get_agentType()) {
                case DIALOGFLOW:
                    List<Tuple> agentSpecificData = checkNotNull(configurationTuple
                            .get_agentSpecificData(), "The Dialogflow specific data is null!");
                    if (agentSpecificData.size() != 1) {
                        throw new IllegalArgumentException("The Dialogflow Agent specific data " +
                                "passed is not valid for Dialogflow! It has to be project ID and " +
                                "Service Account key file directory.");
                    }
                    _listOfAgents.add(new DialogflowAgent(_sessionId, agentSpecificData.get(0)));
                    break;
                case DUMMYAGENT: _listOfAgents.add(new DummyAgent());
                    break;
                default:
                    throw new IllegalArgumentException("The type of the Agent provided " +
                            "\"" +
                            configurationTuple
                                    .get_agentType() + "\" is not currently supported " +
                            "(yet)!");
            }
        }
    }

    /**
     * Get Request from Client and convert it to the RequestLog.
     * Return the list of responses for a given request.
     * TODO(Adam): Maybe store the logs after each conversation; need to decide later on.
     *
     * @param interactionRequest The a data structure (implemented in log.proto) holding the
     *                           interaction from a client.
     * @return The list of responses of all Agents set up on the setUpAgents(...) function call.
     * @throws Exception It is thrown when the setUpAgents wasn't called before calling
     *                   this function.
     */
    public List<ResponseLog> getResponsesFromAgents(InteractionRequest interactionRequest) throws
            Exception {
        if (checkNotNull(_listOfAgents, "Agents are not set up! Use the function" +
                " setUpAgents() first.").isEmpty()) {
            throw new Exception("The list of Agents is empty!");
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
        for (AgentInterface agentInterfaceInstance : _listOfAgents) {
            listOfResponseLogs.add(agentInterfaceInstance.getResponseFromAgent(inputInteraction));
        }
        return listOfResponseLogs;
    }

    // TODO(Adam): Raking function;

    // TODO(Adam): store the conversation in the log as a single Turn
}
