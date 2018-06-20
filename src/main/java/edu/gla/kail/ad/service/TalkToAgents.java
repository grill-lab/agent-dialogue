package edu.gla.kail.ad.service;

import com.google.protobuf.util.JsonFormat;
import edu.gla.kail.ad.Client.InteractionRequest;
import edu.gla.kail.ad.Client.InteractionResponse;
import edu.gla.kail.ad.core.DialogAgentManager;

import java.io.IOException;

import static com.google.common.base.Preconditions.checkNotNull;

public class TalkToAgents {
    /**
     * Currently this method returns a mocked result used for testing server's functionality.
     *
     * @param jsonStringInteractionRequest - JSON formatted String consisting of
     *         InteractionRequest object.
     * @return jsonStringInteractionResponse - JSON formatted String consisting of
     *         InteractionResponse object sent back by the DialogAgentManager.
     * @throws IOException
     */
    public static String getInteractionResponse(String jsonStringInteractionRequest) throws
            IOException {
        // TODO(Adam): Check whether the JSON of the InteractioNRequest is valid and contains
        // appropriate information.
        InteractionRequest.Builder interactionRequestBuilder = InteractionRequest.newBuilder();
        JsonFormat.parser().merge(jsonStringInteractionRequest, interactionRequestBuilder);
        InteractionRequest interactionRequest = interactionRequestBuilder.build();

        if (checkNotNull(interactionRequest.getClientId(), "The ClientID sent in the request is " +
                "null").isEmpty()) {
            throw new IllegalArgumentException("The ClientID sent in the request is empty");
        }
        String clientId = interactionRequest.getClientId();
        DialogAgentManager dialogAgentManager = DialogAgentManagerSingleton.getDialogAgentManager
                (clientId);

        /* TODO(Adam): Implement calling agents - turn on next line only after the testing is done.
        InteractionResponse interactionResponse = dialogAgentManager.getResponsesFromAgents
        (interactionRequest);*/

        // Using the getResponseFromAgentAsInteractionResponse only for testing the server's
        // functionality.
        InteractionResponse interactionResponse = dialogAgentManager
                .getResponseFromAgentAsInteractionResponse(interactionRequest);
        return JsonFormat.printer().print(interactionResponse);
    }

}
