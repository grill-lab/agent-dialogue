package edu.gla.kail.ad.core;

import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.Tuple;
import com.google.cloud.dialogflow.v2beta1.SessionsClient;
import com.google.cloud.dialogflow.v2beta1.SessionsSettings;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

@RunWith(JUnit4.class)
public class DialogflowAgentAuthorizationSingletonTest {
    String jsonKeyFileLocation;
    String projectId;

    /**
     * Set up jsonKeyFileLocation and projectID for the myquotemaster-13899 project.
     */
    @Before
    public void setUp() {
        jsonKeyFileLocation = "/Users/Adam/Documents/Internship/myquotemaster-13899" +
                "-04ed41718e57.json";
        projectId = "myquotemaster-13899";
    }

    /**
     * Test myquotemaster-13899 DialogflowAgentAuthorizationSingleton initialization.
     */
    @Test
    public void testInitialization() {
        Tuple<String, String> tupleOfProjectIdAndAuthenticationFile = Tuple.of(projectId,
                jsonKeyFileLocation);
        try {
            Tuple<String, SessionsClient> tupleOfSessionIDAndSessionClient =
                    DialogflowAgentAuthorizationSingleton
                            .getProjectIdAndSessionsClient(tupleOfProjectIdAndAuthenticationFile);
        } catch (FileNotFoundException fileNotFoundException) {
            Assert.fail("The specified file directory doesn't exist or the file is missing: " +
                    jsonKeyFileLocation);
        } catch (IOException iOException) {
            Assert.fail("The creation of CredentialsProvider, SessionsSettings or SessionsClient " +
                    "for projectID: " + projectId + " failed.");
        }
    }

    /**
     * Test myquotemaster-13899 Dialogflow's CredentialsProvider initialization.
     * TODO(Adam): Include this test also in DialogflowAgentTest class!
     */
    @Test
    public void testDialogflowCredentialsProviderInitialization() {
        try {
            CredentialsProvider credentialsProvider = FixedCredentialsProvider.create(
                    (ServiceAccountCredentials.fromStream(new FileInputStream
                            (jsonKeyFileLocation))));
        } catch (IOException iOException) {
            Assert.fail("The creation of CredentialsProvider with given jsonKeyFileLocation: " +
                    jsonKeyFileLocation + " failed!");
        }
    }

    /**
     * Test myquotemaster-13899 Dialogflow's SessionsSettings initialization.
     * TODO(Adam): Include this test also in DialogflowAgentTest class!
     */
    @Test
    public void testDialogflowSessionsSettingsInitialization() {
        try {
            CredentialsProvider credentialsProvider = FixedCredentialsProvider.create(
                    (ServiceAccountCredentials.fromStream(new FileInputStream
                            (jsonKeyFileLocation))));
            SessionsSettings sessionsSettings = SessionsSettings.newBuilder().setCredentialsProvider
                    (credentialsProvider).build();
        } catch (IOException iOException) {
            Assert.fail("The creation of SessionsSettings (or CredentialsProvider) with given " +
                    "jsonKeyFileLocation: " + jsonKeyFileLocation + " failed!");
        }
    }

    /**
     * Test myquotemaster-13899 Dialogflow's SessionsClient initialization.
     * TODO(Adam): Include this test also in DialogflowAgentTest class!
     */
    @Test
    public void testDialogflowSessionsClientInitialization() {
        try {
            CredentialsProvider credentialsProvider = FixedCredentialsProvider.create(
                    (ServiceAccountCredentials.fromStream(new FileInputStream
                            (jsonKeyFileLocation))));
            SessionsSettings sessionsSettings = SessionsSettings.newBuilder().setCredentialsProvider
                    (credentialsProvider).build();
            SessionsClient sessionsClient = SessionsClient.create(sessionsSettings);
        } catch (IOException iOException) {
            Assert.fail("The creation of SessionsClient (or SessionsSettings or " +
                    "CredentialsProvider) with given jsonKeyFileLocation: " + jsonKeyFileLocation
                    + " failed!");
        }
    }

    /**
     * Test when a wrong jsonKeyFileLocation is provided.
     */
    @Test(expected = FileNotFoundException.class)
    public void testHandlingNonexistentFileDirection() {
        String jsonKeyFileLocation = "NonExisting file directory.";
        Tuple<String, String> tupleOfProjectIdAndAuthenticationFile = Tuple.of(projectId,
                jsonKeyFileLocation);
        try {
            Tuple<String, SessionsClient> tupleOfSessionIDAndSessionClient =
                    DialogflowAgentAuthorizationSingleton.getProjectIdAndSessionsClient
                            (tupleOfProjectIdAndAuthenticationFile);
        } catch (IOException e) {
        }
    }

    /**
     * Test when a null projectID is provided.
     */
    @Test(expected = NullPointerException.class)
    public void testNullProjectId() {
        String projectId = null;
        Tuple<String, String> tupleOfProjectIdAndAuthenticationFile = Tuple.of(projectId,
                jsonKeyFileLocation);
        try {
            Tuple<String, SessionsClient> tupleOfSessionIDAndSessionClient =
                    DialogflowAgentAuthorizationSingleton.getProjectIdAndSessionsClient
                            (tupleOfProjectIdAndAuthenticationFile);
        } catch (IOException e) {
        }
    }

    /**
     * Test when an empty projectID is provided.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testEmptyProjectId() {
        String projectId = "";
        Tuple<String, String> tupleOfProjectIdAndAuthenticationFile = Tuple.of(projectId,
                jsonKeyFileLocation);
        try {
            Tuple<String, SessionsClient> tupleOfSessionIDAndSessionClient =
                    DialogflowAgentAuthorizationSingleton.getProjectIdAndSessionsClient
                            (tupleOfProjectIdAndAuthenticationFile);
        } catch (IOException e) {
        }
    }

    /**
     * Test when a null jsonKeyFileLocation is provided.
     */
    @Test(expected = NullPointerException.class)
    public void testNullJsonKeyFileLocation() {
        String jsonKeyFileLocation = null;
        Tuple<String, String> tupleOfProjectIdAndAuthenticationFile = Tuple.of(projectId,
                jsonKeyFileLocation);
        try {
            Tuple<String, SessionsClient> tupleOfSessionIDAndSessionClient =
                    DialogflowAgentAuthorizationSingleton.getProjectIdAndSessionsClient
                            (tupleOfProjectIdAndAuthenticationFile);
        } catch (IOException e) {
        }
    }

    /**
     * Test when an empty jsonKeyFileLocation is provided.
     */
    @Test(expected = FileNotFoundException.class)
    public void testEmptyJsonKeyFileLocation() {
        String jsonKeyFileLocation = "";
        Tuple<String, String> tupleOfProjectIdAndAuthenticationFile = Tuple.of(projectId,
                jsonKeyFileLocation);
        try {
            Tuple<String, SessionsClient> tupleOfSessionIDAndSessionClient =
                    DialogflowAgentAuthorizationSingleton.getProjectIdAndSessionsClient
                            (tupleOfProjectIdAndAuthenticationFile);
        } catch (IOException e) {
        }
    }

    /**
     * Test when an a null Tuple is provided.
     */
    @Test(expected = FileNotFoundException.class)
    public void testNullTuple() {
        Tuple<String, String> tupleOfProjectIdAndAuthenticationFile = null;
        try {
            Tuple<String, SessionsClient> tupleOfSessionIDAndSessionClient =
                    DialogflowAgentAuthorizationSingleton.getProjectIdAndSessionsClient
                            (tupleOfProjectIdAndAuthenticationFile);
        } catch (IOException e) {
        }
    }
}
