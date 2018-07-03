package edu.gla.kail.ad.replayer;

import com.google.protobuf.Timestamp;
import edu.gla.kail.ad.Client;
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
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


/**
 * Responsible for replaying the conversation from the LogEntry file.
 * °A lot of code is repeated in AgentDialogueClientService in web-simulator client.
 */
public class LogReplayer {
    private final String _AVAILABLE_COMMANDS = "Available commands:" +
            "\nq - Exit the application and stop all processes." +
            "\nn - Add a path to the log file to be processed and process it." +
            "\nw - Wait for the application to finish processing all the requests and then quit.\n";
    private final ManagedChannel _channel;
    // RPC will wait for the server to respond; return response or raise an exception.
    private final AgentDialogueBlockingStub _blockingStub;
    // The string identifying the client.
    private String _userId;
    // Directory to the folder with logs.
    private String _LOG_STORAGE_DIRECTORY;
    // Queue holding conversations to be assigned to different threads.
    private LinkedList<LogEntry> logEntryQueue = new LinkedList<>();


    public LogReplayer(String host, int port) {
        this(ManagedChannelBuilder.forAddress(host, port).usePlaintext());
    }

    public LogReplayer(ManagedChannelBuilder<?> channelBuilder) {
        _channel = channelBuilder.build();
        _blockingStub = AgentDialogueGrpc.newBlockingStub(_channel);
        _userId = generateUserId();

        // Hardcoded directory path.
        File directory = new File(System.getProperty("user.dir") + "/Logs/Replayer");
        if (!directory.exists()) {
            directory.mkdir();
        }
        _LOG_STORAGE_DIRECTORY = directory.toString();
    }

    /**
     * Created for testing purposes.
     *
     * @throws InterruptedException
     */
    public static void main(String[] args) throws Exception {
        LogReplayer client = new LogReplayer("localhost", 8080);
        File directory;
        Scanner scanner = new Scanner(System.in);
        System.out.println("Hi, I'm the log replayer. How can I help you?\n" + client
                ._AVAILABLE_COMMANDS);
        while (true) {
            String command = scanner.nextLine();
            switch (command) {
                case "q":
                    System.out.println("Bye bye!");
                    System.exit(0);
                case "n":
                    System.out.println("Type the path to the logEntry file: ");
                    String providedLogEntryDirectory = scanner.nextLine();
                    directory = new File(providedLogEntryDirectory);
                    if (!directory.exists()) {
                        System.out.println("The provided path to the log file is invalid:\n" +
                                directory.toString() + "\nTry again!!");
                    } else {
                        client.startReplayerThread(directory, client);
                        break;
                    }
                case "w":
                    // TODO(Adam): Implement!
                default:
                    System.out.println("Unrecognised command, try again!\n" + client
                            ._AVAILABLE_COMMANDS);
            }

        }

    }

    /**
     * Create new threads for running requests.ū
     *
     * @param directoryFile - the File with the LogEntry we want to replay.
     * @param client - the Instance of LogReplayer which we are currently using.
     * @throws Exception - When something goes wrong.
     */
    private void startReplayerThread(File directoryFile, LogReplayer client) throws Exception {
        // TODO(Adam): Implement this!
        try {
            InputStream inputStream = new FileInputStream(directoryFile);
            List<InteractionResponse> interactionResponses = client.replayConversation(inputStream);
            System.out.println("The following turns were successfully stored in the Log " +
                    "directory:\n\n" + interactionResponses.toString());
        } catch (Exception exception) {
            throw new Exception("Something went wrong. Error message:\n" + exception.getMessage()
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
    private String generateUserId() {
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
        List<InteractionResponse> listOfInteractionResponses = new ArrayList<>();
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
                    .setUserID(_userId)
                    .setClientId(Client.ClientId.LOG_REPLAYER)
                    .setInteraction(turn.getRequestLog().getInteraction()).build();
            InteractionResponse interactionResponse = getInteractionResponse(interactionRequest);
            listOfInteractionResponses.add(interactionResponse);
            OutputStream outputStream = new FileOutputStream(_LOG_STORAGE_DIRECTORY + "/" +
                    logEntry.getSessionId() + "_" + Instant.now().toString() + ".log");
            interactionRequest.writeTo(outputStream);
            outputStream.close();
        }
        return listOfInteractionResponses;
    }
}
