package edu.gla.kail.ad.service;


import edu.gla.kail.ad.Client.InteractionRequest;
import edu.gla.kail.ad.Client.InteractionResponse;
import edu.gla.kail.ad.core.DialogAgentManager;
import io.grpc.stub.StreamObserver;

import java.io.IOException;

import static com.google.common.base.Preconditions.checkNotNull;

public class AgentDialogueServer {

    private static class AgentDialogueService extends AgentDialogueGrpc.AgentDialogueImplBase {
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
            checkNotNull(dialogAgentManager, "The initialization of the dialogAgentManager failed!");
            responseObserver.onNext(dialogAgentManager.getResponseFromAgentAsInteractionResponse
                    (interactionRequest));
        }
    }
}