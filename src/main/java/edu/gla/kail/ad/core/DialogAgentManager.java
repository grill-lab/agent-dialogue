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
 * The manager is configured to have conversations with specified agents (of certain agent type,
 * * e.g. Dialogflow or Alexa).
 * Conversation management includes handling session state, including starting sessions (assigning
 * session IDs), as well as request identifiers. It also handles serialization of conversation log
 * data.
 * <p>
 * Instruction of usage:
 * 1) Set up the Dialog agents using setUpAgents function
 * 2) Call the getResponseFromAgent for required inputs
 * 3) Use ranking function. TODO(Adam): further part needs to be implemented
 * <p>
 * Example usage :
 * DialogAgentManager dialogAgentManager = new DialogAgentManager();
 * dialogAgentManager.setUpAgents(_configurationTuples);
 * dialogAgentManager.getResponsesFromAgents(interactionRequest);
 **/

public class DialogAgentManager {
    // List of instances of used Dialog agents.
    private List<AgentInterface> _agents;
    // Session ID is a unique identifier of a session which is assigned by the function
    // startSession() called by DialogAgentManager constructor.
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

    public void endSession() {
        // TODO(Adam): Creation of configuration file - to be done later on.
        // TODO(Adam): Updating/Saving to the log file?
    }

    private String getRandomNumberAsString() {
        return UUID.randomUUID().toString();
    }

    // TODO(Adam): generateRequestId needs to be implemented
    private String generateRequestId() {
        return getRandomNumberAsString();
    }

    /**
     * Set up (e.g. authenticate) all agents and store them to the list of agents.
     *
     * @param configurationTuples - The list stores the entities of ConfigurationTuple, which
     *                            holds data required by each agent.
     * @throws Exception                - Raised by _agents.add(new DialogflowAgent(_sessionId,
     * agentSpecificData.get(0)));
     * @throws IllegalArgumentException
     */
    public void setUpAgents(List<ConfigurationTuple> configurationTuples) throws
            IllegalArgumentException, Exception {
        _agents = new ArrayList();
        for (ConfigurationTuple configurationTuple : configurationTuples) {
            switch (configurationTuple.get_agentType()) {
                case DIALOGFLOW:
                    List<Tuple> agentSpecificData = checkNotNull(configurationTuple
                            .get_agentSpecificData(), "The Dialogflow specific data is null!");
                    if (agentSpecificData.size() != 1) {
                        throw new IllegalArgumentException("The Dialogflow agent specific data " +
                                "passed is not valid for Dialogflow! It has to be project ID and " +
                                "Service Account key file directory.");
                    }
                    _agents.add(new DialogflowAgent(_sessionId, agentSpecificData.get(0)));
                    break;
                case DUMMYAGENT:
                    _agents.add(new DummyAgent());
                    break;
                default:
                    throw new IllegalArgumentException("The type of the agent provided " +
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
     * @param interactionRequest - The a data structure (implemented in log.proto) holding the
     *                           interaction from a client.
     * @return List<ResponseLog> - The list of responses of all agents set up on the setUpAgents
     * (...) function call.
     * @throws Exception
     */
    public List<ResponseLog> getResponsesFromAgents(InteractionRequest interactionRequest) throws
            Exception {
        if (checkNotNull(_agents, "agents are not set up! Use the function" +
                " setUpAgents() first.").isEmpty()) {
            throw new Exception("The list of agents is empty!");
        }

        // Set current time on Timestamp.
        Timestamp timestamp = Timestamp.newBuilder().setNanos(0).build();
        // Convert InteractionRequest to RequestLog.
        RequestLog requestLog = RequestLog.newBuilder()
                .setRequestId(generateRequestId())
                .setTime(timestamp)
                .setClientId(interactionRequest.getClientId())
                .setInteraction(interactionRequest.getInteraction()).build();

        // Store the responses from the agents in a list.
        List<ResponseLog> listOfResponseLogs = new ArrayList();
        InputInteraction inputInteraction = requestLog.getInteraction();
        for (AgentInterface agent : _agents) {
            listOfResponseLogs.add(agent.getResponseFromAgent(inputInteraction));
        }
        return listOfResponseLogs;
    }

    // TODO(Adam): Raking function;

    // TODO(Adam): store the conversation in the log as a single Turn
}
