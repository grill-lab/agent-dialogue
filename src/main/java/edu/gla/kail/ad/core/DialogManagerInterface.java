package edu.gla.kail.ad.core;

import edu.gla.kail.ad.core.Client.InteractionRequest;
import edu.gla.kail.ad.core.Log.ResponseLog;

import java.util.List;


/**
 * @interface
 */
public interface DialogManagerInterface {
    // TODO(adam) do we want this kind of function?: void setUpAgents();

    List<ResponseLog> getResponsesFromAgents(InteractionRequest request);
}