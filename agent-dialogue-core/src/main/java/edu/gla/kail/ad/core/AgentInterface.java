package edu.gla.kail.ad.core;

import edu.gla.kail.ad.Client.InteractionRequest;
import edu.gla.kail.ad.CoreConfiguration.ServiceProvider;
import edu.gla.kail.ad.core.Log.ResponseLog;

/**
 * Agent interface is a common interface to different dialogue framework implementations.
 */
public interface AgentInterface {
    ServiceProvider serviceProvider = null;
    String _agentId = null;


    ServiceProvider getServiceProvider(); // Return the service provider type of the instance.

    String getAgentId(); // Return the agentID of the instance (e.g. projectID).

    /**
     * Return a response for a request.
     *
     * @param interactionRequest - A data structure (implemented in log.proto) holding the
     *         incoming interaction that is being sent to an agent.
     * @return ResponseLog - The response from the agent, must be non-null. ResponseLog is a data
     *         structure implemented in log.proto.
     * @throws Exception
     */
    ResponseLog getResponseFromAgent(InteractionRequest interactionRequest) throws Exception;
}