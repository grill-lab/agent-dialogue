package edu.gla.kail.ad.core;

import edu.gla.kail.ad.Client.InputInteraction;
import edu.gla.kail.ad.core.Log.ResponseLog;

import java.util.concurrent.TimeUnit;

/**
 * This is a dummy agent created for testing purposes.
 * Returns null ResponseLog after 10 seconds of waiting.
 * TODO(Adam): Delete after testing is done!
 */
public class FailingTimeDummyAgent implements AgentInterface {
    @Override
    public ResponseLog getResponseFromAgent(InputInteraction inputInteraction) throws Exception {
        ResponseLog responseLog = null;
        while (true) {
            TimeUnit.SECONDS.sleep(10);
        }
    }
}
