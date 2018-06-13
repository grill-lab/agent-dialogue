package edu.gla.kail.ad.core;

import edu.gla.kail.ad.core.Client.InputInteraction;
import edu.gla.kail.ad.core.Log.ResponseLog;

/**
 * Agent Interface which provides integrity, needed by DialogAgentManager, across different
 * particular Dialog Agents (such as DialogflowAgent).
 */
public interface AgentInterface {
    /**
     * Return responses for a formated request from all the Agents of the particular Dialog Manager.
     *
     * @param inputInteraction - a data structure (implemented in log.proto) holding the incoming
     *                         interaction that is being sent to an Agent.
     * @return A response from an Agent. ResponseLog is a data structure implemented in log.proto.
     * @throws Exception The type of exception is dependent on the implementation of the function
     *                   by a particular Dialog Agent.
     */
    ResponseLog getResponseFromAgent(InputInteraction inputInteraction) throws Exception;
}