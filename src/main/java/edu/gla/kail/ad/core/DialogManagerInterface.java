package edu.gla.kail.ad.core;

import edu.gla.kail.ad.core.Client.InputInteraction;
import edu.gla.kail.ad.core.Log.ResponseLog;

import java.util.List;


/**
 * Agent Manager Interface which provides integrity, needed by DialogManager, across different
 * particular Dialog Managers (such as DialogflowDialogManager).
 */
public interface DialogManagerInterface {
    /**
     * Return responses for a formated request from all the Agents of the particular Dialog Manager.
     *
     * @param inputInteraction - a data structure (implemented in log.proto) holding the incoming
     *                         interaction that is being sent to Agents of particular Dialog
     *                         Managers.
     * @return The list of responses from different Agents of a particular Dialog Manager.
     *     ResponseLog is a data structure implemented in log.proto.
     * @throws Exception The type of exception is dependent on the implementation of the function
     *                   by a particular Dialog Manager.
     */
    List<ResponseLog> getResponsesFromAgents(InputInteraction inputInteraction) throws Exception;
}