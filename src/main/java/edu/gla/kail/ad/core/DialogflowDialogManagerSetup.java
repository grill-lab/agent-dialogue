package edu.gla.kail.ad.core;

import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.dialogflow.v2beta1.SessionName;
import com.google.cloud.dialogflow.v2beta1.SessionsClient;
import com.google.cloud.dialogflow.v2beta1.SessionsSettings;
import edu.gla.kail.ad.core.Log.LogEntry;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

public class DialogflowDialogManagerSetup {
    private String _languageCode;
    private String _sessionId;
    private Map<SessionsClient, SessionName> _mapOfSessionClientsAndSessionNames = new HashMap();
    private LogEntry.Builder _logEntryBuilder;

    /**
     * @param languageCode
     * @param sessionId
     * @param mapOfProjectIdAndAuthorizationFile
     * @param logEntryBuilder
     * @throws Exception
     */
    public DialogflowDialogManagerSetup(String languageCode, String sessionId,
                                        Map<String, String> mapOfProjectIdAndAuthorizationFile, LogEntry.Builder logEntryBuilder) throws Exception {
        _languageCode = languageCode;
        _sessionId = sessionId;
        _logEntryBuilder = logEntryBuilder;

        if (mapOfProjectIdAndAuthorizationFile.isEmpty()) {
            throw new IllegalArgumentException("List of agents is empty!");
        } else {
            for (Map.Entry<String, String> agentInformation : mapOfProjectIdAndAuthorizationFile.entrySet()) {
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

    public String get_languageCode() {
        return _languageCode;
    }

    public String get_sessionId() {
        return _sessionId;
    }

    public Map<SessionsClient, SessionName> get_mapOfSessionClientsAndSessionNames() {
        return _mapOfSessionClientsAndSessionNames;
    }

    public LogEntry.Builder get_logEntryBuilder() {
        return _logEntryBuilder;
    }

}
