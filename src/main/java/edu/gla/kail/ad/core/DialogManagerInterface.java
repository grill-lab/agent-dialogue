package edu.gla.kail.ad.core;

import edu.gla.kail.ad.core.Log.RequestLog;
import edu.gla.kail.ad.core.Log.ResponseLog;

import java.util.List;

/**
 * Interface implemented by different Agent Managers.
 *
 * @interface
 */
public interface DialogManagerInterface<E> {
    List<ResponseLog> getResponsesFromAgents(RequestLog request);
}