package edu.gla.kail.ad.core;

import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.dialogflow.v2beta1.SessionName;
import com.google.cloud.dialogflow.v2beta1.SessionsClient;
import com.google.cloud.dialogflow.v2beta1.SessionsSettings;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

import edu.gla.kail.ad.core.Log.LogEntry;

import static com.google.common.base.Preconditions.checkNotNull;

public class DialogflowDialogManagerSetup {
    private Map<SessionsClient, SessionName> _mapOfSessionClientsAndSessionNames = new HashMap();
    private DialogManagerSetup dialogManagerSetup;

    /**
     * @param languageCode
     * @param sessionId
     * @param mapOfProjectIdAndAuthorizationFile
     * @throws Exception
     */
    public DialogflowDialogManagerSetup(Map<String, String> mapOfProjectIdAndAuthorizationFile, DialogManagerSetup dialogManagerSetup) throws Exception {
        this._languageCode = languageCode;
        this._sessionId = sessionId;
        if (mapOfProjectIdAndAuthorizationFile.isEmpty()) {
            throw new Exception("List of agents is empty!");
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
        this.get_logEntry() = get_logEntry()
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

    public LogEntry get_logEntry() {
        return _logEntry;
    }

}
