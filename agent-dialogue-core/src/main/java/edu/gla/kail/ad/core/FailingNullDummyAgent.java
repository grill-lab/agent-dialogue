package edu.gla.kail.ad.core;

import edu.gla.kail.ad.Client.InputInteraction;
import edu.gla.kail.ad.core.Log.ResponseLog;

/**
 * This is a dummy agent created for testing purposes.
 * Returns null.
 * TODO(Adam): Delete after testing is done!
 */
public class FailingNullDummyAgent implements AgentInterface {
    @Override
    public ResponseLog getResponseFromAgent(InputInteraction inputInteraction) {
        return null;
    }
}
