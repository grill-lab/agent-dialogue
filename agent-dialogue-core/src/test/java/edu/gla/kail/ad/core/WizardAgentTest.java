package edu.gla.kail.ad.core;

import com.google.protobuf.Timestamp;
import edu.gla.kail.ad.Client;
import edu.gla.kail.ad.Client.InputInteraction;
import edu.gla.kail.ad.Client.InteractionRequest;
import edu.gla.kail.ad.Client.InteractionType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.time.Instant;

import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class WizardAgentTest {

    @Test
    public void testGetResponseFromAgent() throws Exception {
        InteractionRequest request = InteractionRequest.newBuilder()
                .setTime(Timestamp.newBuilder()
                        .setSeconds(Instant.now().getEpochSecond())
                        .setNanos(Instant.now().getNano())
                        .build())
                .setUserID("1")
                .setClientId(Client.ClientId.EXTERNAL_APPLICATION)
                .setInteraction(InputInteraction.newBuilder()
                    .setType(InteractionType.TEXT)
                    .setText("Hi")
                    .setDeviceType("iPhone whatever")
                    .setLanguageCode("en-US"))
                .build();

       WizardAgent wizardAgent = new WizardAgent("magicsessionid");
        assertTrue("The response from Wizard is valid", wizardAgent
                .getResponseFromAgent(request).isInitialized());
    }
}
