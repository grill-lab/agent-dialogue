package edu.gla.kail.ad.core;

import edu.gla.kail.ad.Client.InputInteraction;
import edu.gla.kail.ad.core.Log.ResponseLog;

import java.util.concurrent.TimeUnit;

/**
 * This is a dummy agent created for testing purposes.
 * Returns null InputInteraction after 10 seconds of waiting.
 */
public class FailingDummyAgent implements AgentInterface  {
    @Override
    public ResponseLog getResponseFromAgent(InputInteraction inputInteraction) throws Exception {
        ResponseLog responseLog = null;
        TimeUnit.SECONDS.sleep(10);
        return responseLog;
    }
}