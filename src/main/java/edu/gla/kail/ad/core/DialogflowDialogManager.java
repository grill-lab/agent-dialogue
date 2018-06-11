package edu.gla.kail.ad.core;

import com.google.cloud.dialogflow.v2beta1.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.gla.kail.ad.core.Log.LogEntry;

/**
 * It's a class used to talk to Dialogflow Agents.
 * The responses from Agents are added to the log, but not saved.
 */
public class DialogflowDialogManager {
    private String _languageCode;
    private Map<SessionsClient, SessionName> _mapOfSessionClientsAndSessionNames;
    private LogEntry.Builder _logEntry;

    /**
     * Constructor which initializes a ready to work DialogflowDialogManager.
     *
     * @param dialogflowDialogManagerSetup
     * @throws Exception
     */
    public DialogflowDialogManager(DialogflowDialogManagerSetup dialogflowDialogManagerSetup) {
        _languageCode = dialogflowDialogManagerSetup.get_languageCode();
        _mapOfSessionClientsAndSessionNames = dialogflowDialogManagerSetup.get_mapOfSessionClientsAndSessionNames();
        _logEntry = dialogflowDialogManagerSetup.get_logEntryBuilder();
    }


    /* Get the response from Agent in response to a request.
    TODO(Adam).*/
    public void getResponsesFromAgentsFromText(String textPassed) {
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
