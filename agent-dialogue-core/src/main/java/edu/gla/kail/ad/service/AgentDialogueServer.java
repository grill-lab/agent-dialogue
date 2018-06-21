package edu.gla.kail.ad.service;


import edu.gla.kail.ad.Client.InteractionRequest;
import edu.gla.kail.ad.Client.InteractionResponse;
import edu.gla.kail.ad.core.DialogAgentManager;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * TODO(Adam): Add the logging part?
 * TODO(Adam): Create a database used for server initialization.
 */
public class AgentDialogueServer {
    private final int _port;
    private final Server _server;

    public AgentDialogueServer(int port) {
        this(ServerBuilder.forPort(port), port);
    }

    /**
     * Create a server listening on specified port.
     *
     * @param serverBuilder
     * @param port
     */
    private AgentDialogueServer(ServerBuilder<?> serverBuilder, int port) {
        _port = port;
        _server = serverBuilder.addService(new AgentDialogueService()).build();
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        // Using the default port 8080.
        AgentDialogueServer server = new AgentDialogueServer(8080);
        server.start();
        server.blockUntilShutdown();
    }

    /**
     * Start the server.
     *
     * @throws IOException
     */
    public void start() throws IOException {
        _server.start();
        Runtime.getRuntime().addShutdownHook(new Thread() {
            /**
             * In case the JVM is being shut down
             */
            @Override
            public void run() {
                System.err.println("Server shut down due to JVM being shut down.");
                shutDown();
            }
        });
    }

    /**
     * Shut down the server.
     */
    public void shutDown() {
        if (_server != null) {
            _server.shutdown();
        }
    }

    /**
     * Wait until main thread is terminated. (gRPC is based on daemon threads)
     *
     * @throws InterruptedException
     */
    private void blockUntilShutdown() throws InterruptedException {
        if (_server != null) {
            _server.awaitTermination();
        }
    }

    /**
     * Serves the requests from clients.
     * TODO(Adam): check if the class and the gRPC server are thread safe.
     */
    static class AgentDialogueService extends AgentDialogueGrpc.AgentDialogueImplBase {
        /**
         * Sends the request to the agents and retrieves the chosen response.
         * TODO(Adam): Right now the function uses getResponseFromAgentAsInteractionResponse
         * function, which is a dummy function for testing purposes.
         *
         * @param interactionRequest - The instance of InteractionRequest passed by the
         *         client to the agents.
         * @param responseObserver - The instance, which is used to pass the instance of
         *         InteractionResponse with the response from the agents.
         */
        @Override
        public void getResponseFromAgents(InteractionRequest interactionRequest,
                                          StreamObserver<InteractionResponse> responseObserver) {
            if (checkNotNull(interactionRequest.getClientId(), "The interactionRequest that have " +
                    "been sent doesn't have ClientID!").isEmpty()) {
                throw new IllegalArgumentException("The interactionRequest that have been has " +
                        "empty ClientID!");
            }
            DialogAgentManager dialogAgentManager;
            try {
                dialogAgentManager = DialogAgentManagerSingleton
                        .getDialogAgentManager(interactionRequest.getClientId());
            } catch (IOException ioException) {
                ioException.printStackTrace();
                dialogAgentManager = null;
            }
            checkNotNull(dialogAgentManager, "The initialization of the dialogAgentManager " +
                    "failed!");
            responseObserver.onNext(dialogAgentManager.getResponseFromAgentAsInteractionResponse
                    (interactionRequest));
            responseObserver.onCompleted();
        }
    }
}