package edu.gla.kail.ad.core;

import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.Tuple;
import com.google.cloud.dialogflow.v2beta1.DetectIntentResponse;
import com.google.cloud.dialogflow.v2beta1.QueryInput;
import com.google.cloud.dialogflow.v2beta1.QueryResult;
import com.google.cloud.dialogflow.v2beta1.SessionName;
import com.google.cloud.dialogflow.v2beta1.SessionsClient;
import com.google.cloud.dialogflow.v2beta1.SessionsSettings;
import com.google.cloud.dialogflow.v2beta1.TextInput;
import edu.gla.kail.ad.core.Client.InteractionRequest;
import edu.gla.kail.ad.core.Log.LogEntry;
import edu.gla.kail.ad.core.Log.ResponseLog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * It's a class used to talk to Dialogflow Agents.
 * The responses from Agents are added to the log, but not saved.
 */
public class DialogflowDialogManager implements DialogManagerInterface {
    private List<Tuple<SessionsClient, SessionName>> _listOfSessionsClientsAndSessionsNames;
    private String _sessionId;
    private LogEntry.Builder _logEntryBuilder;

    /**
     * Constructor which initializes a ready to work DialogflowDialogManager.
     * Creates new LogEntry instance!
     */
    public DialogflowDialogManager(String sessionId,
                                   List<Tuple<String, String>>
                                           listOfProjectIdAndAuthorizationFile) throws
            FileNotFoundException, IOException, Exception {
        _logEntryBuilder = LogEntry.newBuilder();
        _sessionId = sessionId;
        setUpAgents(listOfProjectIdAndAuthorizationFile);
    }

    /**
     * Puts the SessionClients and SessionNames of corresponding project ids' and the
     * localisation of their files into the map.
     *
     * @param listOfProjectIdAndAuthorizationFile
     */
    private void setUpAgents(List<Tuple<String, String>>
                                     listOfProjectIdAndAuthorizationFile) throws
            FileNotFoundException, IOException, Exception {
        if (listOfProjectIdAndAuthorizationFile.isEmpty()) {
            throw new IllegalArgumentException("List of agents is empty!");
        } else {
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

                // Authorize access to the agent currently tested.
                CredentialsProvider credentialsProvider = FixedCredentialsProvider.create(
                        (ServiceAccountCredentials
                                .fromStream(new FileInputStream(jsonKeyFileLocation))));
                SessionsSettings sessionsSettings = SessionsSettings.newBuilder()
                        .setCredentialsProvider(credentialsProvider).build();

                // Create SessionClient.
                SessionsClient sessionsClient = SessionsClient.create(sessionsSettings);
                SessionName session = SessionName.of(projectId, _sessionId);
                _listOfSessionsClientsAndSessionsNames.add(Tuple.of(sessionsClient, session));
            }
        }
    }






    /* Get the response from Agent in response to a request.
    TODO(Adam) input as a request object - probably from the log.*/
    public List<ResponseLog> getResponsesFromAgents(InteractionRequest request) { //TODO String
        // textPassed,
        // String languageCode
        // Append the response from each agent to the list of responses.
        for (Tuple<SessionsClient, SessionName> tupleOfSessionClientsAndSessionNames :
                _listOfSessionsClientsAndSessionsNames) {
            SessionsClient sessionsClient = tupleOfSessionClientsAndSessionNames.x();
            SessionName session = tupleOfSessionClientsAndSessionNames.y();
            // The core code that creates a DialogFlow request from the input text and sends it
            // to Assistant Server.
            // The result is the response result.
            TextInput.Builder textInput = TextInput.newBuilder().setText(textPassed)
                    .setLanguageCode(checkNotNull(languageCode,
                            "Language code not specified! Example of a language code \"en-US\""));
            QueryInput queryInput = QueryInput.newBuilder().setText(textInput).build();
            DetectIntentResponse response = sessionsClient.detectIntent(session, queryInput);
            QueryResult queryResult = response.getQueryResult();


            /**
             * Storing the output in the log file
             */
            //TODO(Adam) remove
            System.out.println(response.toString());
            /* TODO(ADAM) implementation of the logging part. Useful functions:
            queryResult.getAction()
                .getQueryText()
                .getIntent().getDisplayName()
                .getIntentDetectionConfidence()
                .getFulfillmentText()*/
        }

    }


}
