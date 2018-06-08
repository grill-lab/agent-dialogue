package edu.gla.kail.ad;

import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.dialogflow.v2beta1.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


// It's a class used to talk to Dialogflow Agents
public class DialogflowDialogManager {
    private String _languageCode;
    private String _sessionId;

    // Initialiser
    public DialogflowDialogManager(String languageCode, String sessionId) {
        this._languageCode = languageCode;
        this._sessionId = sessionId;
    }

    // Setters
    public void set_languageCode(String _languageCode) {
        this._languageCode = _languageCode;
    }

    public void set_sessionId(String _sessionId) {
        this._sessionId = _sessionId;
    }


    // getResponsesFromAgentsFromText gets responses from agents passed in the list of Agents istOfAgentsByProjectIdAndAuthenticationKeyFile
    // for a given text textPassed and TODO.
    public List<ResponseDataStructure> getResponsesFromAgentsFromText(String textPassed,
                                                                      Map<String, String> listOfAgentsByProjectIdAndAuthenticationKeyFile) throws IOException {
        List<ResponseDataStructure> listOfResponses = new ArrayList<>();

        // Get the response from each agent
        for (Map.Entry<String, String> agentInformation : listOfAgentsByProjectIdAndAuthenticationKeyFile.entrySet()) {
            String projectId = agentInformation.getKey();
            String jsonKeyFileLocation = agentInformation.getValue();

            // Authorization part
            CredentialsProvider credentialsProvider = FixedCredentialsProvider.create((ServiceAccountCredentials
                    .fromStream(new FileInputStream(jsonKeyFileLocation))));
            SessionsSettings sessionsSettings = SessionsSettings.newBuilder().setCredentialsProvider(credentialsProvider).build();

            // Creating SessionClient
            SessionsClient sessionsClient = SessionsClient.create(sessionsSettings);
            SessionName session = SessionName.of(projectId, _sessionId);

            // Setting text input, sending to the server and getting response
            TextInput.Builder textInput = TextInput.newBuilder().setText(textPassed).setLanguageCode(_languageCode);
            QueryInput queryInput = QueryInput.newBuilder().setText(textInput).build();
            DetectIntentResponse response = sessionsClient.detectIntent(session, queryInput);
            QueryResult queryResult = response.getQueryResult();


            System.out.println(response.toString());
            //TODO save all the output as an instance of the ResponseDataStructure class
            ResponseDataStructure responseDataStructure = new ResponseDataStructure();



            /* queryResult.getAction()
                .getQueryText()
                .getIntent().getDisplayName()
                .getIntentDetectionConfidence()
                .getFulfillmentText()*/


            listOfResponses.add(responseDataStructure);
        }


        return listOfResponses;
    }


}
