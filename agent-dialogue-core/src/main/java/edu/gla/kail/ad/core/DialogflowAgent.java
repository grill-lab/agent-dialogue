package edu.gla.kail.ad.core;

import com.google.cloud.Tuple;
import com.google.cloud.dialogflow.v2beta1.Context;
import com.google.cloud.dialogflow.v2beta1.DetectIntentResponse;
import com.google.cloud.dialogflow.v2beta1.QueryInput;
import com.google.cloud.dialogflow.v2beta1.QueryResult;
import com.google.cloud.dialogflow.v2beta1.SessionName;
import com.google.cloud.dialogflow.v2beta1.SessionsClient;
import com.google.cloud.dialogflow.v2beta1.TextInput;
import com.google.protobuf.Timestamp;
import com.google.protobuf.Value;
import edu.gla.kail.ad.Client.InputInteraction;
import edu.gla.kail.ad.Client.InteractionType;
import edu.gla.kail.ad.Client.OutputInteraction;
import edu.gla.kail.ad.core.Log.ResponseLog;
import edu.gla.kail.ad.core.Log.ResponseLog.MessageStatus;
import edu.gla.kail.ad.core.Log.ResponseLog.ServiceProvider;
import edu.gla.kail.ad.core.Log.Slot;
import edu.gla.kail.ad.core.Log.SystemAct;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.gla.kail.ad.core.DialogflowAgentAuthorizationSingleton
        .getProjectIdAndSessionsClient;


/**
 * A class used to talk to Dialogflow agents.
 * The responses from agent is added to the log.
 * The request sent to the agent are validated. (There are no invalid characters which can crash
 * dialogflow)
 * // TODO(Adam) Use shutdown() methof for closing _sessionClient stream.
 */
public class DialogflowAgent implements AgentInterface {
    // The SessionsClient and SessionName are needed for the Dialogflow interaction.
    private SessionsClient _sessionsClient;
    private SessionName _session;
    // A unique ID passed set in the constructor, passed by DialogAgentManager.
    private String _sessionId;

    /**
     * Initialize a ready-to-work DialogflowAgent.
     *
     * @param sessionId - A unique ID passed to the method by DialogAgentManager.
     * @param tupleOfProjectIdAndAuthorizationFile - A tuple specific for DialogflowAgent.
     *         It holds the project ID of a particular agent and the directory location of the file
     *         with Service Account key for this particular agent.
     * @throws IOException - T setUpAgent method may throw exception if the data passed in
     *         the tupleOfProjectIdAndAuthorizationFile is invalid.
     */
    DialogflowAgent(String sessionId,
                    Tuple<String, String>
                            tupleOfProjectIdAndAuthorizationFile) throws IOException {
        _sessionId = sessionId;
        setUpAgent(tupleOfProjectIdAndAuthorizationFile);
    }

    @Override
    public ServiceProvider getServiceProvider() {
        return ServiceProvider.DIALOGFLOW;
    }

    /**
     * Create the SessionClients and SessionNames for the agent which project ID and Service Account
     * key file directory.
     *
     * @param tupleOfProjectIdAndAuthenticationFile - A tuple specific for DialogflowAgent.
     *         It holds the project ID of a particular agent and the directory location of the file
     *         with Service Account key for this particular agent.
     * @throws IOException - When a projectID or the Service Account key is either null or
     *         empty, appropriate exception is thrown.
     */
    private void setUpAgent(Tuple<String, String>
                                    tupleOfProjectIdAndAuthenticationFile) throws IOException {
        Tuple<String, SessionsClient> projectIdAndSessionsClient = getProjectIdAndSessionsClient
                (tupleOfProjectIdAndAuthenticationFile);
        _sessionsClient = projectIdAndSessionsClient.y();
        _session = SessionName.of(projectIdAndSessionsClient.x(), _sessionId);
    }

    /**
     * Validate the inputInteraction for Dialogflow usage.
     * Checks: inputInteraction, type, respective type field, language code.
     *
     * @param inputInteraction - A data structure (implemented in log.proto) holding the
     *         incoming interaction that is being sent to an agent.
     * @throws IllegalArgumentException
     */
    private void validateInputInteraction(InputInteraction inputInteraction) throws
            IllegalArgumentException {
        checkNotNull(inputInteraction, "The passed inputInteraction is null!");
        if (checkNotNull(inputInteraction.getLanguageCode(), "The inputInteraction LanguageCode " +
                "is set to null!").isEmpty()) {
            throw new IllegalArgumentException("The inputInteraction LanguageCode is empty!");
        }
        String ERROR_MESSAGE = "The inputInteraction of type %s has %s %s field!";
        if (checkNotNull(inputInteraction.getType(), "The inputInteraction type is null!")
                .toString().isEmpty()) {
            throw new IllegalArgumentException("The inputInteraction type is not set!");
        }
        switch (inputInteraction.getType()) {
            case TEXT:
                if (checkNotNull(inputInteraction.getText(), String.format(ERROR_MESSAGE, "TEXT",
                        "a null", "text")).isEmpty()) {
                    throw new IllegalArgumentException(String.format(ERROR_MESSAGE, "TEXT",
                            "an empty", "text"));
                }
                break;
            case ACTION:
                if (checkNotNull(inputInteraction.getActionList(), String.format(ERROR_MESSAGE,
                        "ACTION", "a null", "action")).isEmpty()) {
                    throw new IllegalArgumentException(String.format(ERROR_MESSAGE, "ACTION",
                            "an empty", "action"));
                }
                break;
            case AUDIO:
                if (checkNotNull(inputInteraction.getAudioBytes(), String.format(ERROR_MESSAGE,
                        "AUDIO", "a null", "audio")).isEmpty()) {
                    throw new IllegalArgumentException(String.format(ERROR_MESSAGE, "AUDIO",
                            "an empty", "audio"));
                }
                break;
            default:
                throw new IllegalArgumentException("Unrecognised interaction type.");
        }
    }

    /**
     * Return Query input for any type of inputInteraction the user may get: audio, text or action.
     *
     * @param inputInteraction - A data structure (implemented in log.proto) holding the
     *         incoming interaction that is being sent to an agent.
     * @return queryInput - A data structure which holds the query that needs to be send to
     *         Dialogflow.
     * @throws IllegalArgumentException
     */
    private QueryInput provideQuery(InputInteraction inputInteraction) {
        validateInputInteraction(inputInteraction);
        // Get a response from a Dialogflow agent for a particular request (inputInteraction type).
        switch (inputInteraction.getType()) {
            case TEXT:
                TextInput.Builder textInput = TextInput.newBuilder().setText(inputInteraction
                        .getText())
                        .setLanguageCode(inputInteraction.getLanguageCode());
                return QueryInput.newBuilder().setText(textInput).build();
            case AUDIO:
//                    InputAudioConfig inputAudioConfig = InputAudioConfig.newBuilder()
//                            .setLanguageCode(inputInteraction.getLanguageCode())
//                            .setAudioEncoding() // Needs an argument AudioEncoding
//                            .setSampleRateHertz() // Needs an argument int32
//                            .build();
//                    return QueryInput.newBuilder().setAudioConfig(inputAudioConfig).build();
                throw new IllegalArgumentException("The AUDIO method for DialogFlow is not " +
                        "yet supported" +
                        "."); // TODO(Adam): implement;
            case ACTION:
//                    EventInput eventInput = EventInput.newBuilder()
//                            .setLanguageCode(inputInteraction.getLanguageCode())
//                            .setName() // Needs an argument String
//                            .setParameters() // Optional, needs a Struct
//                            .build();
//                    return QueryInput.newBuilder().setEvent(eventInput).build();
                throw new IllegalArgumentException("The ACTION method for DialogFlow is not" +
                        " yet supported" +
                        "."); // TODO(Adam): implement;
            default: // TODO(Adam): Can be delited, as we validate the inputInteraction in a
                // separate method.
                throw new IllegalArgumentException("Unrecognised interaction type.");
        }
    }

    /**
     * TODO(Adam): set the Message_status value of the ResponseLog, when the message is somewhat
     * unsuccessful.
     *
     * @throws IllegalArgumentException - The exception is being thrown when the type of the
     *         interaction requested is not recognised or supported.
     */
    @Override
    public ResponseLog getResponseFromAgent(InputInteraction inputInteraction) throws
            IllegalArgumentException {
        QueryInput queryInput = provideQuery(inputInteraction);
        // Set up Dialogflow classes' instances used for obtaining the response.
        // TODO(Adam): What do do her when things go wrong?  Handle RPC errors?  Throw an exception?
        DetectIntentResponse response = _sessionsClient.detectIntent(_session, queryInput);
        QueryResult queryResult = response.getQueryResult();

        Timestamp timestamp = Timestamp.newBuilder()
                .setSeconds(Instant.now()
                        .getEpochSecond())
                .setNanos(Instant.now()
                        .getNano())
                .build();

        ResponseLog.Builder responseLogBuilder = ResponseLog.newBuilder()
                .setResponseId(response.getResponseId())
                .setTime(timestamp)
                .setServiceProvider(ServiceProvider.DIALOGFLOW)
                .setRawResponse(response.toString());

        SystemAct.Builder systemActBuilder = SystemAct.newBuilder()
                .setAction(queryResult.getAction())
                .setInteraction(OutputInteraction.newBuilder()
                        .setType(InteractionType.TEXT) // TODO(Adam): If more advanced Dialogflow
                        // agents can send a response with different interaction type, this needs
                        // to be changed.
                        .setText(queryResult.getFulfillmentText())
                        .build());

        for (Context context : queryResult.getOutputContextsList()) {
            // Set the slot's name and value for every Slot.
            for (Map.Entry<String, Value> parameterEntry : context.getParameters()
                    .getFieldsMap().entrySet()) {
                systemActBuilder.addSlot(Slot.newBuilder()
                        .setName(parameterEntry.getKey())
                        .setValue(parameterEntry.getValue().toString())
                        .build());
            }
        }
        responseLogBuilder.addAction(systemActBuilder.build());
        return responseLogBuilder.setMessageStatus(MessageStatus.SUCCESSFUL).build();
    }
}