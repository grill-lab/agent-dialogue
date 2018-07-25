package edu.gla.kail.ad.service;

import com.google.gson.JsonObject;
import com.google.protobuf.Timestamp;
import edu.gla.kail.ad.Client.InputInteraction;
import edu.gla.kail.ad.Client.InteractionRequest;
import edu.gla.kail.ad.Client.InteractionRequestOrBuilder;
import edu.gla.kail.ad.Client.InteractionResponse;
import edu.gla.kail.ad.Client.InteractionResponse.ClientMessageStatus;
import edu.gla.kail.ad.Client.InteractionType;
import edu.gla.kail.ad.Client.OutputInteraction;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.List;

import static edu.gla.kail.ad.Client.ClientId.WEB_SIMULATOR;

/**
 * Connect to AgentDialogueClientService and therefore enable interaction with Agent Dialogue Core.
 * Accessible from JavaScript, through RESTful calls.
 * Responsible for storing logs.
 * TODO(Adam): Store logs.
 */
@WebServlet("/ad-client-service-servlet")
public class AdClientServiceServlet extends HttpServlet {
    private static LogManagerSingleton _logManagerSingleton = LogManagerSingleton
            .getLogManagerSingleton();
    private static AgentDialogueClientService _client = new AgentDialogueClientService
            ("localhost", 8070);

    /**
     * Handle POST request.
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws
            IOException {
        switch (request.getHeader("operation")) {
            case "sendRequestToAgents":
                sendRequestToAgents(request, response);
                break;
            case "updateRating":
                updateRating(request, response);
                break;
            default:
                JsonObject json = new JsonObject();
                json.addProperty("message", "The Operation passed in the header is not supported.");
                response.getWriter().write(json.toString());
        }
    }

    /**
     * Handle GET request.
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        doPost(request, response);
    }

    /**
     * Add proto buffer for passed rating to the log.
     *
     * @param request
     * @param response
     */
    private void updateRating(HttpServletRequest request, HttpServletResponse response) throws
            IOException {
        _logManagerSingleton.addRating(request.getParameter("ratingScore"), request.getParameter
                ("responseId"), request.getParameter("experimentId"), request.getParameter
                ("requestId"));
    }

    /**
     * Send request to agent and write (return) JSON back with response and it's details.
     * Store request and response in log files.
     */
    private void sendRequestToAgents(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        JsonObject json = new JsonObject();

        InteractionRequest interactionRequest = getInteractionRequestFromText(request
                .getParameter("textInput"), request.getParameter("language"));
        _logManagerSingleton.addInteraction(interactionRequest, null);
        json.addProperty("interactionRequest", interactionRequest.toString());
        InteractionResponse interactionResponse;
        try {
            interactionResponse = _client.getInteractionResponse(interactionRequest);
            _logManagerSingleton.addInteraction(null, interactionResponse);
            json.addProperty("message", handleResponse(interactionResponse));
            json.addProperty("interactionResponse", interactionResponse.toString());
            json.addProperty("responseId", interactionResponse.getResponseId());
            response.getWriter().write(json.toString());
        } catch (Exception exception) {
            interactionResponse = InteractionResponse.newBuilder()
                    .setMessageStatus(ClientMessageStatus.ERROR)
                    .setErrorMessage(exception.getMessage() + "\n\n" + exception.getStackTrace())
                    .setTime(getTimeStamp())
                    .build();
            _logManagerSingleton.addInteraction(null, interactionResponse);
            json.addProperty("message", "There was a fatal error!");
            json.addProperty("interactionResponse", interactionResponse.toString());
            response.getWriter().write(json.toString());
        }
    }

    /**
     * Creates text presented to the used depending on the MessageStatus.
     *
     * @param interactionResponse - The response obtained from agents.
     * @return String - Either the interactions passed by the agent or error message.
     * @throws Exception - Thrown when the MessageStatus is not recognised.
     */
    private String handleResponse(InteractionResponse interactionResponse) throws Exception {
        switch (interactionResponse.getMessageStatus()) {
            case ERROR:
                return interactionResponse.getErrorMessage();
            case SUCCESSFUL:
                StringBuilder concatenatedResponses = new StringBuilder();
                List<OutputInteraction> outputInteractionList = interactionResponse
                        .getInteractionList();
                for (OutputInteraction outputInteraction : outputInteractionList) {
                    concatenatedResponses.append(handleOutputInteraction(outputInteraction));
                }
                return concatenatedResponses.toString();
            default:
                return ("There was an error, contact the developer: " + interactionResponse
                        .toString());

        }
    }

    /**
     * Handle single outputInteraction (passed from outputInteractionList obtained from
     * InteractionRequest).
     */
    private String handleOutputInteraction(OutputInteraction outputInteraction) throws Exception {
        switch (outputInteraction.getType()) {
            case TEXT:
                return outputInteraction.getText();
            // TODO(Adam): Implement handling audio and action output.
            case ACTION:
                throw new Exception("Handling actions not available yet!");
            case AUDIO:
                throw new Exception("Handling audio not available yet!");
            default:
                throw new Exception("There was an error! Not recognised OutputInteraction Type!");
        }

    }

    /**
     * Helper method: create InteractionRequests from text Input.
     */
    private InteractionRequest getInteractionRequestFromText(String textInput, String
            languageCode) {
        return ((InteractionRequest.Builder) getInteractionRequestBuilder(InputInteraction
                .newBuilder()
                .setLanguageCode(languageCode)
                .setDeviceType(deviceType())
                .setType(InteractionType.TEXT)
                .setText(textInput)
                .build()))
                .build();
    }

    /**
     * Helper method: create InteractionRequestBuilders.
     */
    private InteractionRequestOrBuilder getInteractionRequestBuilder(InputInteraction
                                                                             inputInteraction) {
        return InteractionRequest.newBuilder()
                .setClientId(WEB_SIMULATOR)
                .setUserID(returnUserName())
                .setTime(getTimeStamp())
                .setInteraction(inputInteraction);
    }

    private Timestamp getTimeStamp() {
        return Timestamp.newBuilder()
                .setSeconds(Instant.now()
                        .getEpochSecond())
                .setNanos(Instant.now()
                        .getNano())
                .build();
    }

    /**
     * Return the current user name.
     * TODO(Adam): Implement getting username from database or having a username object!
     */
    private String returnUserName() {
        return "sampleUserName-to-be-implemented";
    }

    /**
     * Return the device type.
     * TODO(Adam): Implement getting device type.
     */
    private String deviceType() {
        return "sampleDeviceType-to-be-implemented";
    }
}
