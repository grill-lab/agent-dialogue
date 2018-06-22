package edu.gla.kail.ad.core;

import edu.gla.kail.ad.Client.InputInteraction;
import edu.gla.kail.ad.core.Log.ResponseLog;

/**
 * This is a dummy agent created for testing purposes.
 * Throws an Exception.
 * TODO(Adam): Delete after testing is done!
 */
public class FailingExceptionDummyAgent implements AgentInterface {
    @Override
    public ResponseLog getResponseFromAgent(InputInteraction inputInteraction) throws Exception {
        throw new Exception("This exception is thrown for testing purposes.");
    }
}
