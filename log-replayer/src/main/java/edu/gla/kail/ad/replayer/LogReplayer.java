package edu.gla.kail.ad.replayer;

import com.google.protobuf.Timestamp;
import edu.gla.kail.ad.Client.InteractionRequest;
import edu.gla.kail.ad.Client.InteractionResponse;
import edu.gla.kail.ad.core.Log.LogEntry;
import edu.gla.kail.ad.core.Log.Turn;
import edu.gla.kail.ad.service.AgentDialogueGrpc;
import edu.gla.kail.ad.service.AgentDialogueGrpc.AgentDialogueBlockingStub;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


/**
 * Responsible for replaying the conversation from the LogEntry file.
 * °A lot of code is repeated in AgentDialogueClientService in web-simulator client.
 */
public class LogReplayer {
    private final ManagedChannel _channel;
    // RPC will wait for the server to respond; return response or raise an exception.
    private final AgentDialogueBlockingStub _blockingStub;
    // The string identifying the client.
    private String _clientId;
    // Directory to the folder with logs.
    private String _LOGSTORAGEDIRECTORY;


    public LogReplayer(String host, int port) {
        this(ManagedChannelBuilder.forAddress(host, port).usePlaintext());
    }

    public LogReplayer(ManagedChannelBuilder<?> channelBuilder) {
        _channel = channelBuilder.build();
        _blockingStub = AgentDialogueGrpc.newBlockingStub(_channel);
        _clientId = generateClientId();

        // Hardcoded directory path.
        File directory = new File(System.getProperty("user.dir") + "/Logs/Replayer");
        if (!directory.exists()) {
            directory.mkdir();
        }
        _LOGSTORAGEDIRECTORY = directory.toString();
    }

    /**
     * Created for testing purposes.
     *
     * @throws InterruptedException
     */
    public static void main(String[] args) throws InterruptedException {
        LogReplayer client = new LogReplayer("localhost", 8080);
        File directory;
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Enter the path of the log file you want to read (LogEntry) or " +
                    "type \"q\" to exit: ");
            String providedLogEntryDirectory = scanner.nextLine();
            if (providedLogEntryDirectory.equals("q")) {
                System.out.println("Bye bye!");
                System.exit(0);
            }
            directory = new File(providedLogEntryDirectory);
            if (!directory.exists()) {
                System.out.println("The provided path to the log file is invalid:\n" + directory
                        .toString() + "\nTry again!!");
            } else {
                break;
            }
        }
        try {
            InputStream inputStream = new FileInputStream(directory);
            List<InteractionResponse> interactionResponses = client.replayConversation(inputStream);
            System.out.println("The following turns were successfully stored in the Log " +
                    "directory:\n\n" + interactionResponses.toString());
        } catch (Exception exception) {
            System.out.println("Something went wrong. Error message:\n" + exception.getMessage()
                    + "\n" + exception.getStackTrace());
        } finally {
            client.shutdown();
        }
    }

    /**
     * Return random, unique Id.
     * TODO(Jeff): What method should we implement?
     *
     * @return random clientId
     */
    private String generateClientId() {
        return "LogReplayer_" + UUID.randomUUID().toString();
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
     * Get one response from the agents
     *
     * @param interactionRequest - The request sent to the Agent Dialog Manager.
     * @return interactionResponse - The response from an Agent chosen by DialogAgentManager.
     * @throws Exception - Throw when certain time of waiting for the repsonse passes.
     */
    private InteractionResponse getInteractionResponse(InteractionRequest interactionRequest)
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
     * Store all the responses in Log files.
     * Return the the list of responses.
     *
     * @param inputStream - InputStream created from the log file.
     * @return OutputStream - The outputStream of the Logfile created by replaying conversation.
     * @throws Exception - Throw when the provided InputStream is invalid or
     *         getInteractionResponse throws error.
     */
    private List<InteractionResponse> replayConversation(InputStream inputStream) throws
            Exception {
        LogEntry logEntry;
        List<InteractionResponse> listOfInteractionResponses = new ArrayList();
        try {
            logEntry = LogEntry.parseFrom(inputStream);
        } catch (IOException iOException) {
            throw new Exception("Provided InputStream is not valid! Error message: " +
                    iOException.getMessage());
        }
        // TODO(Adam): Implement sending requests at specified rate (e.g. e per second, or one
        // query every 2 sec, or min 2 sec between queries, etc.)
        for (Turn turn : logEntry.getTurnList()) {
            InteractionRequest interactionRequest = InteractionRequest.newBuilder()
                    .setTime(Timestamp.newBuilder()
                            .setSeconds(Instant.now().getEpochSecond())
                            .setNanos(Instant.now().getNano())
                            .build())
                    .setClientId(_clientId)
                    .setInteraction(turn.getRequestLog().getInteraction()).build();
            InteractionResponse interactionResponse = getInteractionResponse(interactionRequest);
            listOfInteractionResponses.add(interactionResponse);
            OutputStream outputStream = new FileOutputStream(_LOGSTORAGEDIRECTORY + "/" +
                    logEntry.getSessionId() + "_" + Instant.now().toString() + ".log");
            interactionRequest.writeTo(outputStream);
            outputStream.close();
        }
        return listOfInteractionResponses;
    }
}
