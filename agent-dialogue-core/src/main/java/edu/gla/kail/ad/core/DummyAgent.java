package edu.gla.kail.ad.core;

import com.google.protobuf.Timestamp;
import edu.gla.kail.ad.Client.InputInteraction;
import edu.gla.kail.ad.Client.InteractionType;
import edu.gla.kail.ad.Client.OutputInteraction;
import edu.gla.kail.ad.core.Log.ResponseLog;
import edu.gla.kail.ad.core.Log.ResponseLog.ServiceProvider;
import edu.gla.kail.ad.core.Log.Slot;
import edu.gla.kail.ad.core.Log.SystemAct;

import java.time.Instant;


/**
 * This is a dummy agent created for testing purposes.
 */
class DummyAgent implements AgentInterface {
    Timestamp timestamp = Timestamp.newBuilder()
            .setSeconds(Instant.now()
                    .getEpochSecond())
            .setNanos(Instant.now()
                    .getNano())
            .build();

    @Override
    public ResponseLog getResponseFromAgent(InputInteraction inputInteraction) {
        ResponseLog responseLog = ResponseLog.newBuilder()
                .setResponseId("ResponseId set by DummyBuilder")
                .setTime(timestamp)
                .setClientId("ClientId set by DummyBuilder")
                .setServiceProvider(ServiceProvider.DUMMYAGENT)
                .setRawResponse("RawResponse set by DummyBuilder")
                .addAction(SystemAct.newBuilder()
                        .setAction("Action set by DummyBuilder")
                        .setInteraction(OutputInteraction.newBuilder()
                                .setText("Text set by DummyBuilder")
                                .setType(InteractionType.TEXT).build())
                        .addSlot(Slot.newBuilder()
                                .setName("SlotName set by DummyBuilder")
                                .setValue("SlotValue set by DummyBuilder").build())
                        .build())
                .build();
        System.out.println("------------------------------------------------------------------\n" +
                "\nDummy agent response: \n\n:" + responseLog.toString());
        return responseLog;
    }
}