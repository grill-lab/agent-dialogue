package edu.gla.kail.ad.server.resources;

import edu.gla.kail.ad.core.Client.InteractionRequest;
import edu.gla.kail.ad.core.Client.InteractionResponse;
import edu.gla.kail.ad.server.TalkToAgents;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;


@Path("/agent_interaction")
public class AgentInteractionResource {

    /**
     * Function returning the InteractionResponse object holding the response chosen by the
     * agent-dialogue-core.
     *
     * @param stringInteractionRequest - A ByteString object created by calling the
     *         toByteString() method on an InteractionRequest object.
     * @return ByteString - A ByteString object created by calling the toByteString() method on an
     *         InteractionResponse.
     * @throws Exception
     */
    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public String getResponsesFromAgents(String stringInteractionRequest) throws
            Exception {
        return TalkToAgents.getInteractionResponse(stringInteractionRequest);
    }
}