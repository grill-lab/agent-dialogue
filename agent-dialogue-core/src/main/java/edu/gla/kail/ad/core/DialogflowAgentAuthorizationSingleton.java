package edu.gla.kail.ad.core;

import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.Tuple;
import com.google.cloud.dialogflow.v2beta1.SessionsClient;
import com.google.cloud.dialogflow.v2beta1.SessionsSettings;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Modified singleton-design-pattern package-private class: verifies the creation of new instance of
 * the class based on tuple tupleOfProjectIdAndAuthenticationFile occurrence in the
 * _agentAuthorizationInstances. The only accessible method is getProjectIdAndSessionsClient(),
 * which returns the Tuple of projectID and SessionClient.
 * The class is thread safe.
 */
final class DialogflowAgentAuthorizationSingleton {
    private static Map<Tuple<String, String>, DialogflowAgentAuthorizationSingleton>
            _agentAuthorizationInstances;
    private SessionsClient _sessionsClient;
    private String _projectId;

    /**
     * Create the SessionClients and project for all the agents which project ID and Service Account
     * key file directories are provided.
     *
     * @param tupleOfProjectIdAndAuthenticationFile - A tuple specific for DialogflowAgent.
     *         It holds the project ID of a particular agent and the directory location of the file
     *         with Service Account key for this particular agent.
     * @throws FileNotFoundException
     * @throws IllegalArgumentException
     */
    private DialogflowAgentAuthorizationSingleton(Tuple<String, String>
                                                          tupleOfProjectIdAndAuthenticationFile)
            throws FileNotFoundException, IllegalArgumentException, IOException {
        checkNotNull(tupleOfProjectIdAndAuthenticationFile, "The passed tuple is null!");
        _projectId = checkNotNull(tupleOfProjectIdAndAuthenticationFile.x(), "The project " +
                "ID is null!");
        String jsonKeyFileLocation = checkNotNull(tupleOfProjectIdAndAuthenticationFile.y(), "The" +
                " JSON file location is null!");
        if (_projectId.isEmpty()) {
            throw new IllegalArgumentException("The provided project ID of the service is empty!");
        }
        if (!new File(jsonKeyFileLocation).isFile()) {
            throw new FileNotFoundException("The location of the JSON key file provided does not " +
                    "exist: " + jsonKeyFileLocation);
        }

        CredentialsProvider credentialsProvider = FixedCredentialsProvider.create(
                (ServiceAccountCredentials.fromStream(new FileInputStream(jsonKeyFileLocation))));
        SessionsSettings sessionsSettings = SessionsSettings.newBuilder().setCredentialsProvider
                (credentialsProvider).build(); // TODO(Adam) - handle the error given by this
        // method, when the authorization fails.
        _sessionsClient = SessionsClient.create(sessionsSettings);
    }

    /**
     * Thread safe method, verifying the existence of an instance of
     * DialogflowAgentAuthorizationSingleton
     * corresponding to a passed tuple, which returns the Tuple of projectID and SessionClient.
     *
     * @param tupleOfProjectIdAndAuthenticationFile - A tuple specific for DialogflowAgent.
     *         It holds the project ID of a particular agent and the directory location of the file
     *         with Service Account key for this particular agent.
     * @return Tuple<String
                       *
                       *                                       ,
                       *
                       *                                       SessionsClient> - A data structure
     *         holding projectID and
     *         SessionClient required for
     *         the authorization.
     * @throws IOException - When a projectID or the Service Account key is either null or
     *         empty, appropriate exception is thrown.
     */
    static synchronized Tuple<String, SessionsClient> getProjectIdAndSessionsClient
    (Tuple<String, String> tupleOfProjectIdAndAuthenticationFile) throws IOException {
        if (_agentAuthorizationInstances == null) {
            _agentAuthorizationInstances = new HashMap();
        }
        if (!_agentAuthorizationInstances.containsKey(tupleOfProjectIdAndAuthenticationFile)) {
            _agentAuthorizationInstances.put(tupleOfProjectIdAndAuthenticationFile, new
                    DialogflowAgentAuthorizationSingleton(tupleOfProjectIdAndAuthenticationFile));
        }
        DialogflowAgentAuthorizationSingleton _agentAuthorizationInstance =
                _agentAuthorizationInstances.get(tupleOfProjectIdAndAuthenticationFile);
        return Tuple.of(_agentAuthorizationInstance._projectId, _agentAuthorizationInstance
                ._sessionsClient);
    }
}
