package edu.gla.kail.ad.service;

import edu.gla.kail.ad.service.AgentDialogueClientService;

import com.google.protobuf.Timestamp;
import edu.gla.kail.ad.Client.InteractionRequest;
import edu.gla.kail.ad.Client.InteractionResponse;
import edu.gla.kail.ad.core.Log.LogEntry;
import edu.gla.kail.ad.core.Log.RequestLog;
import edu.gla.kail.ad.core.Log.Turn;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Functionality includes:
 * - reading the log data from LogEntry files,
 * - translating taken data to client requests,
 * - sending the requests to agent-dialogue-core through getInteractionResponse method of
 * AgentDialogueClientService class,
 * - providing back ???? TODO(Adam): what should this function create?
 * //TODO(Adam):
 */
public class LogRePlayer {
    /**
     * Replay the conversation with agents.
     * Store the conversation to the log file????
     *
     * @param path - Instance of Path class, holding the directory to the log file.
     * @param clientId - The string identifying the client.
     * @throws Exception - Throw when the provided path is not valid.
     */
    public void rePlayConversation(Path path, String clientId) throws Exception {
        List<RequestLog> requests = getRequestsFromLogFile(path);
        List<InteractionResponse> listOfInteractionResponses = new ArrayList();
        for (RequestLog request : requests) {
            InteractionRequest interactionRequest = InteractionRequest.newBuilder()
                    .setTime(Timestamp.newBuilder()
                            .setSeconds(Instant.now()
                                    .getEpochSecond())
                            .setNanos(Instant.now()
                                    .getNano())
                            .build())
                    .setClientId(clientId)
                    .setInteraction(request.getInteraction()).build();
            listOfInteractionResponses.add(getInteractionResponse(interactionRequest));
        }

        // TODO(Adam): Finish this method!

    }


    /**
     * Extract requests used in the given file.
     *
     * @param path - Instance of Path class, holding the directory to the log file.
     * @return List<RequestLog> - Extracted requests used in the session stored in LogEntry.
     * @throws Exception - Throw when the provided path is not valid.
     */
    public List<RequestLog> getRequestsFromLogFile(Path path) throws Exception {
        InputStream inputStream;
        try {
            inputStream = Files.newInputStream(path);
        } catch (IOException iOException) {
            throw new Exception("Provided file path is not valid! Error message: " + iOException
                    .getMessage());
        }
        return getRequestFromInputStream(inputStream);
    }

    public List<RequestLog> getRequestFromInputStream(InputStream inputStream) throws Exception {
        LogEntry logentry;
        try {
            logentry = LogEntry.parseFrom(inputStream);
        } catch (IOException iOException) {
            throw new Exception("Provided file path is not valid! Error message: " + iOException
                    .getMessage());
        }
        return getRequestsFromLogEntry(logentry);
    }

    /**
     * Extract requests sent by the client.
     *
     * @param logEntry - The instance of LogEntry (read from the file).
     * @return List<RequestLog> - Extracted requests used in the session stored in LogEntry.
     */
    private List<RequestLog> getRequestsFromLogEntry(LogEntry logEntry) {
        List<RequestLog> listOfRequestLogs = new ArrayList();
        for (Turn turn : logEntry.getTurnList()) {
            listOfRequestLogs.add(turn.getRequestLog());
        }
        return listOfRequestLogs;
    }
}
