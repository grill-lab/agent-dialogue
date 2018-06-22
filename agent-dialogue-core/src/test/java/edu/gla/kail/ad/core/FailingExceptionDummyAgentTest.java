package edu.gla.kail.ad.core;

import edu.gla.kail.ad.Client.InputInteraction;
import edu.gla.kail.ad.Client.InteractionType;
import edu.gla.kail.ad.core.Log.ResponseLog;
import org.junit.Test;

public class FailingExceptionDummyAgentTest {
    /**
     * Test the FailingExceptionDummyAgent raise the exception.
     */
    @Test(expected = Exception.class)
    public void testThrowingException() {
        InputInteraction inputInteraction = InputInteraction.newBuilder()
                .setType(InteractionType.TEXT)
                .setText("Sample text")
                .setDeviceType("iPhone whatever")
                .setLanguageCode("en-US")
                .build();
        FailingExceptionDummyAgent failingExceptionDummyAgent = new FailingExceptionDummyAgent();
        ResponseLog responseLog = failingExceptionDummyAgent.getResponseFromAgent(inputInteraction);
    }
}
