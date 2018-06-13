package edu.gla.kail.ad.core;

import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.Tuple;
import com.google.cloud.dialogflow.v2beta1.Context;
import com.google.cloud.dialogflow.v2beta1.DetectIntentResponse;
import com.google.cloud.dialogflow.v2beta1.QueryInput;
import com.google.cloud.dialogflow.v2beta1.QueryResult;
import com.google.cloud.dialogflow.v2beta1.SessionName;
import com.google.cloud.dialogflow.v2beta1.SessionsClient;
import com.google.cloud.dialogflow.v2beta1.SessionsSettings;
import com.google.cloud.dialogflow.v2beta1.TextInput;
import com.google.protobuf.Timestamp;
import com.google.protobuf.Value;
import edu.gla.kail.ad.core.Client.InputInteraction;
import edu.gla.kail.ad.core.Client.InteractionType;
import edu.gla.kail.ad.core.Client.OutputInteraction;
import edu.gla.kail.ad.core.Log.ResponseLog;
import edu.gla.kail.ad.core.Log.ResponseLog.ServiceProvider;
import edu.gla.kail.ad.core.Log.Slot;
import edu.gla.kail.ad.core.Log.SystemAct;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;


/**
 * A class used to talk to Dialogflow Agents.
 * The responses from Agents are added to the log, but not saved.
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
     * @param sessionId                            A unique ID passed to the function by
     *                                             DialogAgentManager.
     * @param tupleOfProjectIdAndAuthorizationFile A tuple specific for DialogflowAgent.
     *                                             It holds the project ID of a particular
     *                                             Agent and the directory location of the file
     *                                             with Service Account key for this particular
     *                                             Agent.
     * @throws Exception the setUpAgent function may throw exception if the data passed in the
     *                   tupleOfProjectIdAndAuthorizationFile is invalid.
     */
    public DialogflowAgent(String sessionId,
                           Tuple<String, String>
                                   tupleOfProjectIdAndAuthorizationFile) throws Exception {
        _sessionId = sessionId;
        setUpAgent(tupleOfProjectIdAndAuthorizationFile);
    }

    /**
     * Create the SessionClients and SessionNames for all the Agents which project ID and Service
     * Account key file directories are provided.
     *
     * @param tupleOfProjectIdAndAuthenticationFile A tuple specific for DialogflowAgent.
     *                                              It holds the project ID of a particular
     *                                              Agent and the directory location of the file
     *                                              with Service Account key for this particular
     *                                              Agent.
     * @throws Exception When a projectID or the Service Account key is either null or empty,
     *                   appropriate exception is thrown.
     */
    private void setUpAgent(Tuple<String, String>
                                    tupleOfProjectIdAndAuthenticationFile) throws Exception {
        checkNotNull(tupleOfProjectIdAndAuthenticationFile, "The passed tuple is null!");
        String projectId = checkNotNull(tupleOfProjectIdAndAuthenticationFile.x(), "The project " +
                "ID is null!");
        String jsonKeyFileLocation = checkNotNull(tupleOfProjectIdAndAuthenticationFile.y(), "The" +
                " JSON file location is null!");
        if (projectId.isEmpty()) {
            throw new Exception("The provided project ID of the service is empty!");
        }
        if (!new File(jsonKeyFileLocation).isFile()) {
            throw new FileNotFoundException("The location of the JSON key file provided " +
                    "does not exist: " + jsonKeyFileLocation);
        }

        // Authenticate the access to the Agent.
        CredentialsProvider credentialsProvider = FixedCredentialsProvider.create(
                (ServiceAccountCredentials
                        .fromStream(new FileInputStream(jsonKeyFileLocation))));
        SessionsSettings sessionsSettings = SessionsSettings.newBuilder()
                .setCredentialsProvider(credentialsProvider).build();

        _sessionsClient = SessionsClient.create(sessionsSettings);
        _session = SessionName.of(projectId, _sessionId);
    }


    /**
     * @throws IllegalArgumentException The exception is being thrown when the type of the
     *                                  interaction requested is not recognised or supported.
     */
    @Override
    public ResponseLog getResponseFromAgent(InputInteraction inputInteraction) throws
            IllegalArgumentException {
        // Get a response from a Dialogflow Agent for a particular request.
        QueryInput queryInput = null;
        switch (inputInteraction.getType()) {
            case TEXT:
                TextInput.Builder textInput = TextInput.newBuilder().setText(inputInteraction
                        .getText())
                        .setLanguageCode(inputInteraction.getLanguageCode());
                queryInput = QueryInput.newBuilder().setText(textInput).build();
                break;
            case AUDIO:
//                    InputAudioConfig inputAudioConfig = InputAudioConfig.newBuilder()
//                            .setLanguageCode(inputInteraction.getLanguageCode())
//                            .setAudioEncoding() // Needs an argument AudioEncoding
//                            .setSampleRateHertz() // Needs an argument int32
//                            .build();
//                    queryInput = QueryInput.newBuilder().setAudioConfig(inputAudioConfig).build();
                throw new IllegalArgumentException("The AUDIO function for DialogFlow is not " +
                        "yet supported" +
                        "."); //TODO(Adam): implement;
                // break;
            case ACTION:
//                    EventInput eventInput = EventInput.newBuilder()
//                            .setLanguageCode(inputInteraction.getLanguageCode())
//                            .setName() // Needs an argument String
//                            .setParameters() // Optional, needs a Struct
//                            .build();
//                    queryInput = QueryInput.newBuilder().setEvent(eventInput).build();
                throw new IllegalArgumentException("The ACTION function for DialogFlow is not" +
                        " yet supported" +
                        "."); //TODO(Adam): implement;
                // break;
            case UNRECOGNIZED:
                throw new IllegalArgumentException("Unrecognised interaction type.");
        }

        // Set up Dialogflow classes' instances used for obtaining the response.
        DetectIntentResponse response = _sessionsClient.detectIntent(_session, queryInput);
        QueryResult queryResult = response.getQueryResult();

        // Get current time.
        long millis = System.currentTimeMillis();
        Timestamp timestamp = Timestamp.newBuilder().setSeconds(millis / 1000)
                .setNanos((int) ((millis % 1000) * 1000000)).build();

        // Safe values to response log builder instance.
        ResponseLog.Builder responseLogBuilder = ResponseLog.newBuilder()
                .setResponseId(response.getResponseId())
                .setTime(timestamp)
                .setServiceProvider(ServiceProvider.DIALOGFLOW)
                .setRawResponse(response.toString());

        // Create SystemAct builder with values of
        SystemAct.Builder systemActBuilder = SystemAct.newBuilder()
                .setAction(queryResult.getAction())
                .setInteraction(OutputInteraction.newBuilder()
                        .setType(InteractionType.TEXT) //TODO(Adam): If more advanced
                        // Dialogflow Agents can send a response with differnet interaction
                        // type, this needs to be changed.
                        .setText(queryResult.getQueryText())
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

        //TODO(Adam) Remove after the log writing to files is implemented; now we can see
        // the output.
        System.out.println(response.toString());
        return responseLogBuilder.build();
    }
}
