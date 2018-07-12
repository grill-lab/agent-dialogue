package main.java;

import com.google.protobuf.Timestamp;
import edu.gla.kail.ad.Client.InputInteraction;
import edu.gla.kail.ad.Client.InteractionRequest;
import edu.gla.kail.ad.Client.InteractionRequestOrBuilder;
import edu.gla.kail.ad.Client.InteractionResponse;
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

@WebServlet("/main.java.JavaScriptLinker")
public class JavaScriptLinker extends HttpServlet {
    private static main.java.AgentDialogueClientService _client = new main.java.AgentDialogueClientService
            ("localhost", 8080);

    public static synchronized main.java.AgentDialogueClientService getClient() {
        System.out.print("working");
        return _client;
    }


    public void doPost(HttpServletRequest request, HttpServletResponse response) throws
            IOException {
        System.out.println("Java Script Linker called successfully!");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html");
        response.getWriter().write("There was a fatal");
        return;

        // TODO delete
/*
        System.out.println("Java Script Linker called successfully!");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html");

        InteractionRequest interactionRequest = getInteractionRequestFromText(request.getHeader
                ("textInput"), request.getHeader("language"));
        InteractionResponse interactionResponse;
        try {
            interactionResponse = _client.getInteractionResponse(interactionRequest);
            response.getWriter().write(handleResponse(interactionResponse));
        } catch (Exception e) {
            response.getWriter().write("There was a fatal error!\n" + e.getMessage() + "\n\n" +
                    e.getStackTrace());
        }*/
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws java.io.IOException {
        doPost(request, response);
    }


    // TODO(Adam): Restructure entire code below!

    /**
     *
     */
    private String handleResponse(InteractionResponse interactionResponse) throws Exception {
        switch (interactionResponse.getMessageStatus()) {
            case ERROR:
                return interactionResponse.getErrorMessage();
            case SUCCESSFUL:
                String concatenatedResponses = "";
                List<OutputInteraction> outputInteractionList = interactionResponse
                        .getInteractionList();
                for (OutputInteraction outputInteraction : outputInteractionList) {
                    concatenatedResponses += handleOutputInteraction(outputInteraction);
                }
                return concatenatedResponses;
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
                .setTime(Timestamp.newBuilder()
                        .setSeconds(Instant.now()
                                .getEpochSecond())
                        .setNanos(Instant.now()
                                .getNano())
                        .build())
                .setInteraction(inputInteraction);
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

