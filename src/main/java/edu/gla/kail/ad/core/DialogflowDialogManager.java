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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;


/**
 * A class used to talk to Dialogflow Agents.
 * The responses from Agents are added to the log, but not saved.
 */
public class DialogflowDialogManager implements DialogManagerInterface {
    // List stores the instances of SessionsClient and SessionName, for every Agent, which are
    // needed for the dialog interaction.
    private List<Tuple<SessionsClient, SessionName>> _listOfSessionsClientsAndSessionsNames;
    // A unique ID passed set in the constructor, passed by DialogManager.
    private String _sessionId;

    /**
     * Initialize a ready-to-work DialogflowDialogManager.
     *
     * @param sessionId                           A unique ID passed to the function by
     *                                            DialogManager.
     * @param listOfProjectIdAndAuthorizationFile A list specific for DialogflowDialogManager.
     *                                            Each tuple holds the project ID of a particular
     *                                            Agent and the directory location of the file
     *                                            with Service Account key for this particular
     *                                            Agent.
     * @throws Exception the setUpAgents function may throw exception if the data passed in the
     *                   listOfProjectIdAndAuthorizationFile is invalid.
     */
    public DialogflowDialogManager(String sessionId,
                                   List<Tuple<String, String>>
                                           listOfProjectIdAndAuthorizationFile) throws Exception {
        _sessionId = sessionId;
        setUpAgents(listOfProjectIdAndAuthorizationFile);
    }

    /**
     * Create the SessionClients and SessionNames for all the Agents which project ID and Service
     * Account key file directories are provided.
     *
     * @param listOfProjectIdAndAuthorizationFile A list specific for DialogflowDialogManager.
     *                                            Each tuple holds the project ID of a particular
     *                                            Agent and the directory location of the file
     *                                            with Service Account key for this particular
     *                                            Agent.
     * @throws Exception When a projectID or the Service Account key is either null or empty,
     *                   appropriate exception is thrown.
     */
    private void setUpAgents(List<Tuple<String, String>>
                                     listOfProjectIdAndAuthorizationFile) throws Exception {
        if (listOfProjectIdAndAuthorizationFile.isEmpty()) {
            throw new IllegalArgumentException("List of Agents is empty!");
        } else {
            _listOfSessionsClientsAndSessionsNames = new ArrayList();
            for (Tuple<String, String> tupleOfProjectIdAndAuthorizationFileDirectory :
                    listOfProjectIdAndAuthorizationFile) {
                String projectId = checkNotNull(tupleOfProjectIdAndAuthorizationFileDirectory.x()
                        , "The project ID is null!");
                String jsonKeyFileLocation = checkNotNull
                        (tupleOfProjectIdAndAuthorizationFileDirectory.y(), "The JSON file " +
                                "location is null!");
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

                SessionsClient sessionsClient = SessionsClient.create(sessionsSettings);
                SessionName session = SessionName.of(projectId, _sessionId);
                _listOfSessionsClientsAndSessionsNames.add(Tuple.of(sessionsClient, session));
            }
        }
    }

    /**
     * @throws IllegalArgumentException The exception is being thrown when the type of the
     *                                  interaction requested is not recognised or supported.
     */
    @Override
    public List<ResponseLog> getResponsesFromAgents(InputInteraction inputInteraction) throws
            IllegalArgumentException {
        // Append the response from each Agent to the list of responses.
        List<ResponseLog> responseLogList = new ArrayList();
        for (Tuple<SessionsClient, SessionName> tupleOfSessionClientsAndSessionNames :
                _listOfSessionsClientsAndSessionsNames) {

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
            SessionsClient sessionsClient = tupleOfSessionClientsAndSessionNames.x();
            SessionName session = tupleOfSessionClientsAndSessionNames.y();
            DetectIntentResponse response = sessionsClient.detectIntent(session, queryInput);
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
            responseLogList.add(responseLogBuilder.build());

            //TODO(Adam) Remove after the log writing to files is implemented; now we can see
            // the output.
            System.out.println(response.toString());
        }
        return responseLogList;
    }
}
