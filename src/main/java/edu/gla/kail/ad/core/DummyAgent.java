package edu.gla.kail.ad.core;

import com.google.protobuf.Timestamp;
import edu.gla.kail.ad.core.Client.InputInteraction;
import edu.gla.kail.ad.core.Client.InteractionType;
import edu.gla.kail.ad.core.Client.OutputInteraction;
import edu.gla.kail.ad.core.Log.ResponseLog;
import edu.gla.kail.ad.core.Log.ResponseLog.ServiceProvider;
import edu.gla.kail.ad.core.Log.Slot;
import edu.gla.kail.ad.core.Log.SystemAct;


/**
 * This is a dummy Agent created for testing purposes.
 */
public class DummyAgent implements AgentInterface {
    long millis = System.currentTimeMillis();
    Timestamp timestamp = Timestamp.newBuilder().setSeconds(millis / 1000)
            .setNanos((int) ((millis % 1000) * 1000000)).build();

    /**
     * @param inputInteraction - a data structure (implemented in log.proto) holding the incoming
     *                         interaction that is being sent to an Agent.
     * @return A response from an Agent. ResponseLog is a data structure implemented in log.proto.
     */
    @Override
    public ResponseLog getResponseFromAgent(InputInteraction inputInteraction) {
        return ResponseLog.newBuilder()
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
    }
}
