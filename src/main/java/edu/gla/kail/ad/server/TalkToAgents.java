package edu.gla.kail.ad.server;

import com.google.protobuf.ByteString;
import edu.gla.kail.ad.core.Client.InteractionRequest;
import edu.gla.kail.ad.core.Client.InteractionResponse;
import edu.gla.kail.ad.core.DialogAgentManager;

import java.nio.charset.Charset;

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
        ByteString byteStringInteractionRequest = ByteString.copyFrom(stringInteractionRequest, Charset.defaultCharset());
        // TODO(Adam): Extract all the information from the JSON and then check whether it's valid.
        InteractionRequest interactionRequest = InteractionRequest.parseFrom
                (byteStringInteractionRequest);

        // TODO(Adam): Implement calling agents.
        String clientId = interactionRequest.getClientId();
        DialogAgentManager dialogAgentManager = DialogAgentManagerSingleton.getDialogAgentManager
                (clientId);
        // Using the getResponseFromAgentAsInteractionResponse only for testing the server's
        // functionality.
        InteractionResponse interactionResponse = dialogAgentManager
                .getResponseFromAgentAsInteractionResponse(interactionRequest);
        return interactionRequest.toByteString().toString();
    }

}
