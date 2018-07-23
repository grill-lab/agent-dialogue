package edu.gla.kail.ad.service;

import com.google.protobuf.Timestamp;
import edu.gla.kail.ad.Client;
import edu.gla.kail.ad.Client.InputInteraction;
import edu.gla.kail.ad.Client.InteractionRequest;
import edu.gla.kail.ad.Client.InteractionResponse;
import edu.gla.kail.ad.Client.InteractionType;
import edu.gla.kail.ad.service.AgentDialogueGrpc.AgentDialogueBlockingStub;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

public class AgentDialogueClientService {
    private final ManagedChannel _channel;
    private final AgentDialogueBlockingStub _blockingStub; // gRPC will wait for the server to
    // respond; return response or raise an exception.

    public AgentDialogueClientService(String host, int port) {
        this(ManagedChannelBuilder.forAddress(host, port).usePlaintext(true));
    }

    public AgentDialogueClientService(ManagedChannelBuilder<?> channelBuilder) {
        _channel = channelBuilder.build();
        _blockingStub = AgentDialogueGrpc.newBlockingStub(_channel);
    }

    /**
     * Created for testing purposes.
     */
    public static void main(String[] args) throws Exception {
        InteractionRequest interactionRequest = InteractionRequest.newBuilder()
                .setClientId(Client.ClientId.WEB_SIMULATOR)
                .setTime(Timestamp.newBuilder()
                        .setSeconds(Instant.now()
                                .getEpochSecond())
                        .setNanos(Instant.now()
                                .getNano())
                        .build())
                .setInteraction(InputInteraction.newBuilder()
                        .setText("Text set by client")
                        .setType(InteractionType.TEXT)
                        .setLanguageCode("en-US")
                        .setDeviceType("DeviceType set by client")
                        .addAction("Action set by client")
                        .build())
                .setUserID("UserId set by Client")
                .build();
        AgentDialogueClientService client = new AgentDialogueClientService("localhost", 8070);
        try {
            InteractionResponse interactionResponse = client.getInteractionResponse
                    (interactionRequest);
            System.out.println(interactionResponse.toString());
        } finally {
            client.shutdown();
        }
    }

    /**
     * Shut the channel down after specified number of seconds (5 in this case).
     *
     * @throws InterruptedException
     */
    public void shutdown() throws InterruptedException {
        _channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    /**
     * Get Interaction response.
     *
     * @param interactionRequest - The request sent to the Agent Dialog Manager.
     * @return interactionResponse - The response from an Agent chosen by DialogAgentManager.
     * @throws Exception
     */
    public InteractionResponse getInteractionResponse(InteractionRequest interactionRequest)
            throws Exception {
        InteractionResponse interactionResponse;
        try {
            interactionResponse = _blockingStub.getResponseFromAgents(interactionRequest);
            return interactionResponse;
        } catch (StatusRuntimeException e) {
            e.printStackTrace();
            throw new Exception("Error occured: " + e.getStatus() + e.getMessage());
        }
    }
}