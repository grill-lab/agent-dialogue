package edu.gla.kail.ad.core;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@RunWith(JUnit4.class)
public class SupportedAgentTypesTest {
    @Test
    public void testNotEmpty() {
        assertNotEquals("The SupportedAgentTypes enum doesn't have any types specified!", 0,
                SupportedAgentTypes.values().length);
    }

    @Test
    public void testDialogflowIsIncluded() {
        assertEquals("SupportedAgentTypes enum doesn't contain the DIALOGFLOW as a supported " +
                "agent type!", "DIALOGFLOW", SupportedAgentTypes.DIALOGFLOW.toString());
    }
}
