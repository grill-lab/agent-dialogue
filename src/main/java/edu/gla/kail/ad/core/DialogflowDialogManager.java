package edu.gla.kail.ad.core;

import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.dialogflow.v2beta1.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;


/* It's a class used to talk to Dialogflow Agents. */
public class DialogflowDialogManager {
    private String _languageCode;
    private String _sessionId;
    private Map<SessionsClient, SessionName> _mapOfSessionClientsAndSessionNames;

    // TODO(Adam) write a comment etc.
    public DialogflowDialogManager(String languageCode, String sessionId, Map<String, String> listOfAgentsByProjectIdAndAuthenticationKeyFile) throws Exception {
        this._languageCode = checkNotNull(languageCode, "en-US");

        if (sessionId.isEmpty()) {
            throw new Exception("The session id needs to be defined!");
        } else {
            this._sessionId = sessionId;
        }

        if (listOfAgentsByProjectIdAndAuthenticationKeyFile.isEmpty()) {
            throw new Exception("List of agents is empty!");
        } else {
            for (Map.Entry<String, String> agentInformation : listOfAgentsByProjectIdAndAuthenticationKeyFile.entrySet()) {
                String projectId = agentInformation.getKey();
                String jsonKeyFileLocation = agentInformation.getValue();

                // Authorize access to the agent currently tested.
                CredentialsProvider credentialsProvider = FixedCredentialsProvider.create((ServiceAccountCredentials
                        .fromStream(new FileInputStream(jsonKeyFileLocation))));
                SessionsSettings sessionsSettings = SessionsSettings.newBuilder().setCredentialsProvider(credentialsProvider).build();

                // Create SessionClient.
                SessionsClient sessionsClient = SessionsClient.create(sessionsSettings);
                SessionName session = SessionName.of(projectId, _sessionId);
                _mapOfSessionClientsAndSessionNames.put(sessionsClient, session);
            }
        }

    }

    public void set_languageCode(String _languageCode) {
        this._languageCode = _languageCode;
    }

    public void set_sessionId(String _sessionId) {
        this._sessionId = _sessionId;
    }


    /* Get the response from Agent in response to a request.
    TODO(Adam).*/
    public List<ResponseDataStructure> getResponsesFromAgentsFromText(String textPassed) {
        List<ResponseDataStructure> listOfResponses = new ArrayList();

        // Append the response from each agent to the list of responses.
        for (Map.Entry<SessionsClient, SessionName> mapOfSessionClientsAndSessionNames : _mapOfSessionClientsAndSessionNames.entrySet()) {
            SessionsClient sessionsClient = mapOfSessionClientsAndSessionNames.getKey();
            SessionName session = mapOfSessionClientsAndSessionNames.getValue();
            // The core code that creates a DialogFlow request from the input text and sends it to Assistant Server.
            // The result is the response result.
            TextInput.Builder textInput = TextInput.newBuilder().setText(textPassed).setLanguageCode(_languageCode);
            QueryInput queryInput = QueryInput.newBuilder().setText(textInput).build();
            DetectIntentResponse response = sessionsClient.detectIntent(session, queryInput);
            QueryResult queryResult = response.getQueryResult();

            //TODO(Adam) remove or make a debug log.
            System.out.println(response.toString());
            //TODO(Adam) save all the output as an instance of the ResponseDataStructure class
            ResponseDataStructure responseDataStructure = new ResponseDataStructure();

            /* TODO(ADAM) implementation of the logging part. Useful functions:
            queryResult.getAction()
                .getQueryText()
                .getIntent().getDisplayName()
                .getIntentDetectionConfidence()
                .getFulfillmentText()*/


            listOfResponses.add(responseDataStructure);
        }


        return listOfResponses;
    }


}
