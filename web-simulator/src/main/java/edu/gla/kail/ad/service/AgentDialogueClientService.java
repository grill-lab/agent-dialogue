package edu.gla.kail.ad.service;

import com.google.protobuf.Timestamp;
import edu.gla.kail.ad.Client.InputInteraction;
import edu.gla.kail.ad.Client.InteractionRequest;
import edu.gla.kail.ad.Client.InteractionResponse;
import edu.gla.kail.ad.Client.InteractionType;
import edu.gla.kail.ad.core.Log.LogEntry;
import edu.gla.kail.ad.core.Log.Turn;
import edu.gla.kail.ad.service.AgentDialogueGrpc.AgentDialogueBlockingStub;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class AgentDialogueClientService {
    private final ManagedChannel _channel;
    private final AgentDialogueBlockingStub _blockingStub; // RPC will wait for the server to
    // respond; return response or raise an exception

    public AgentDialogueClientService(String host, int port) {
        this(ManagedChannelBuilder.forAddress(host, port).usePlaintext()); // usePlainText
        // skips negation: true
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
                .setClientId("ClientID set by client")
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
                .build();
        AgentDialogueClientService client = new AgentDialogueClientService("localhost", 8080);
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
            throw new Exception("Error occured: " + e.getStatus());
        }
    }

    /**
     * Replay the conversation with agents.
     * Return the OutputStream of the conversation.
     *
     * @param inputStream - InputStream created from the log file.
     * @param clientId - The string identifying the client.
     * @return OutputStream - The outputStream of the Logfile created by replaying conversation.
     *         TODO(Jeff): Is it what we want as a return value?
     * @throws Exception - Throw when the provided InputStream is invalid or
     *         getInteractionResponse throws error.
     */
    public OutputStream replayConversation(InputStream inputStream, String clientId) throws
            Exception {
        LogEntry logEntry;
        List<InteractionResponse> listOfInteractionResponses = new ArrayList();
        try {
            logEntry = LogEntry.parseFrom(inputStream);
        } catch (IOException iOException) {
            throw new Exception("Provided InputStream is not valid! Error message: " +
                    iOException.getMessage());
        }
        for (Turn turn : logEntry.getTurnList()) {
            InteractionRequest interactionRequest = InteractionRequest.newBuilder()
                    .setTime(Timestamp.newBuilder()
                            .setSeconds(Instant.now()
                                    .getEpochSecond())
                            .setNanos(Instant.now()
                                    .getNano())
                            .build())
                    .setClientId(clientId)
                    .setInteraction(turn.getRequestLog().getInteraction()).build();
            listOfInteractionResponses.add(getInteractionResponse(interactionRequest));
        }

        throw new Exception("Return not implemented yet!");
    }
}