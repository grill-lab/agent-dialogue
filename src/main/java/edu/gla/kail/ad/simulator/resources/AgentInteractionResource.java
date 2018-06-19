package edu.gla.kail.ad.simulator.resources;

import edu.gla.kail.ad.simulator.TalkToAgents;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;


@Path("/agent_interaction")
public class AgentInteractionResource {

    /**
     * Function returning the string in the format of JSON of the InteractionResponse object holding
     * the response chosen by the agent-dialogue-core. The converstion is done by
     * com.google.protobuf.util.JsonFormat.
     *
     * @param jsonStringInteractionRequest - JSON formatted String consisting of
     *         InteractionRequest object.
     * @return jsonStringInteractionResponse - JSON formatted String consisting of
     *         InteractionResponse object sent back by the DialogAgentManager.
     * @throws IOException
     */
    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public String getResponsesFromAgents(String jsonStringInteractionRequest) throws IOException {
        return TalkToAgents.getInteractionResponse(jsonStringInteractionRequest);
    }
}