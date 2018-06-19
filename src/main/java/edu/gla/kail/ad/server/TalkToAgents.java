package edu.gla.kail.ad.server;

import edu.gla.kail.ad.core.Client.InteractionRequest;
import edu.gla.kail.ad.core.Client.InteractionResponse;
import edu.gla.kail.ad.core.DialogAgentManager;
import com.google.protobuf.util.JsonFormat;

public class TalkToAgents {
    /**
     * Currently this method returns a mocked result used for testing server's functionality.
     *
     * @param stringInteractionRequest
     * @return ByteString - InteractionResponse converted to String from ByteString.
     * @throws Exception
     */
    public static String getInteractionResponse(String stringInteractionRequest)
            throws Exception {
        // TODO(Adam): Extract all the information from the JSON and then check whether it's valid.
        // TODO(Adam): Implement calling agents.

        InteractionRequest.Builder interactionRequestBuilder = InteractionRequest.newBuilder();
        JsonFormat.parser().merge(stringInteractionRequest, interactionRequestBuilder);
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
