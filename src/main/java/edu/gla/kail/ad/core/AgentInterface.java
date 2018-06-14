package edu.gla.kail.ad.core;

import edu.gla.kail.ad.core.Client.InputInteraction;
import edu.gla.kail.ad.core.Log.ResponseLog;

/**
 * Agent interface is a common interface to different dialogue framework implementations.
 */
interface AgentInterface {
    /**
     * Return a response for a formatted request.
     *
     * @param inputInteraction - A data structure (implemented in log.proto) holding the incoming
     *                         interaction that is being sent to an agent.
     * @return responseLog - The response from the agent, must be non-null. ResponseLog is a data
     * structure implemented in log.proto.
     * @throws Exception
     */
    ResponseLog getResponseFromAgent(InputInteraction inputInteraction) throws Exception;
}