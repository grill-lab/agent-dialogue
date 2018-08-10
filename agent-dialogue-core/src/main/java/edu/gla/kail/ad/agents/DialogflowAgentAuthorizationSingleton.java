package edu.gla.kail.ad.agents;

import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.Tuple;
import com.google.cloud.dialogflow.v2beta1.SessionsClient;
import com.google.cloud.dialogflow.v2beta1.SessionsSettings;
import edu.gla.kail.ad.CoreConfiguration.AgentConfig;

import java.io.IOException;
import java.net.URL;
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
public final class DialogflowAgentAuthorizationSingleton {
    private static Map<AgentConfig, DialogflowAgentAuthorizationSingleton> _agentAuthorizationInstances;
    private SessionsClient _sessionsClient;
    private String _projectId;

    /**
     * Create the SessionClients and project for all the agents which project ID and Service Account
     * key file directories are provided.
     *
     * @throws IllegalArgumentException
     */
    private DialogflowAgentAuthorizationSingleton(AgentConfig agent)
            throws IllegalArgumentException, IOException {
        _projectId = checkNotNull(agent.getProjectId(), "The project ID is null!");
        URL jsonKeyUrl = checkNotNull(new URL(agent.getConfigurationFileURL()), "The" +
                " JSON file location is null!");
        if (_projectId.isEmpty()) {
            throw new IllegalArgumentException("The provided project ID of the service is empty!");
        }

        CredentialsProvider credentialsProvider = FixedCredentialsProvider.create(
                (ServiceAccountCredentials.fromStream(jsonKeyUrl.openStream())));
        SessionsSettings sessionsSettings = SessionsSettings.newBuilder().setCredentialsProvider
                (credentialsProvider).build(); // TODO(Adam): Handle the error, when the
        // authorization fails. What to do then though?
        _sessionsClient = SessionsClient.create(sessionsSettings);
    }

    /**
     * Thread safe method, providing a Tuple of projectID and SessionClient corresponding to the
     * passed-input tuple.
     *
     * @return Tuple<> - A data structure holding projectID and SessionClient required for the
     *         authorization.
     * @throws IOException - When a projectID or the Service Account key is either null or
     *         empty, appropriate exception is thrown.
     */
    static synchronized Tuple<String, SessionsClient> getProjectIdAndSessionsClient(AgentConfig agent)
            throws IOException {
        if (_agentAuthorizationInstances == null) {
            _agentAuthorizationInstances = new HashMap<>();
        }
        if (!_agentAuthorizationInstances.containsKey(agent))
            _agentAuthorizationInstances.put(agent, new DialogflowAgentAuthorizationSingleton
                    (agent));
        DialogflowAgentAuthorizationSingleton _agentAuthorizationInstance =
                _agentAuthorizationInstances.get(agent);
        return Tuple.of(_agentAuthorizationInstance._projectId, _agentAuthorizationInstance
                ._sessionsClient);
    }
}
