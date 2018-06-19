package edu.gla.kail.ad.server;

import com.google.protobuf.util.JsonFormat;
import edu.gla.kail.ad.core.Client.InteractionRequest;
import edu.gla.kail.ad.core.Client.InteractionResponse;
import edu.gla.kail.ad.core.DialogAgentManager;

import java.io.IOException;

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
        // TODO(Adam): Extract all the information from the JSON and then check whether it's valid.
        // TODO(Adam): Implement calling agents.

        InteractionRequest.Builder interactionRequestBuilder = InteractionRequest.newBuilder();
        JsonFormat.parser().merge(jsonStringInteractionRequest, interactionRequestBuilder);
        InteractionRequest interactionRequest = interactionRequestBuilder.build();

        String clientId = interactionRequest.getClientId();
        DialogAgentManager dialogAgentManager = DialogAgentManagerSingleton.getDialogAgentManager
                (clientId);
        // Using the getResponseFromAgentAsInteractionResponse only for testing the server's
        // functionality.
        InteractionResponse interactionResponse = dialogAgentManager
                .getResponseFromAgentAsInteractionResponse(interactionRequest);
        return JsonFormat.printer().print(interactionResponse);
    }

}
