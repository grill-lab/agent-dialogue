package edu.gla.kail.ad.agents;

import com.google.api.core.SettableApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.*;
import com.google.cloud.firestore.EventListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import com.google.protobuf.Timestamp;
import com.google.protobuf.Value;
import edu.gla.kail.ad.Client;
import edu.gla.kail.ad.Client.InteractionRequest;
import edu.gla.kail.ad.Client.InteractionType;
import edu.gla.kail.ad.Client.OutputInteraction;
import edu.gla.kail.ad.CoreConfiguration.AgentConfig;
import edu.gla.kail.ad.CoreConfiguration.ServiceProvider;
import edu.gla.kail.ad.core.AgentInterface;
import edu.gla.kail.ad.core.Log.ResponseLog;
import edu.gla.kail.ad.core.Log.ResponseLog.MessageStatus;
import edu.gla.kail.ad.core.Log.SystemAct;

import javax.annotation.Nullable;
import java.net.URL;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * This is a Wizard-of-Oz agent created for experiments. It allows multiple users to chat
 * with one another.
 * It uses Firestore for storing the messages and to listen for events on shared chat 'documents'.
 */
class WizardAgent implements AgentInterface {

  private static final Logger LOGGER = LoggerFactory.getLogger(WizardAgent.class);

  // The firestore database connection.
  private Firestore _database;

  // Analogous to the "wizard project" - something like a project id in a dialogflow agent.
  private String _projectId;

  // Default timeout to wait for a response in seconds.
  private static final long DEFAULT_TIMEOUT_SECONDS = 60;

  // Hold the wizard agent's configuration, including necessary db credentials.
  private AgentConfig _agent;

  // Gold the hardcoded agent ID.
  private String _agentId = null;

  /**
   * Construct a new WizardAgent.
   *
   * @param sessionId
   * @throws Exception
   */
  public WizardAgent(String sessionId, AgentConfig agent) throws Exception {
    _agent = agent;
    _projectId = _agent.getProjectId();
    _agentId = _agent.getProjectId();
    initAgent();
  }

  /**
   * Initialize the agent.
   *
   * @throws Exception
   */
  private void initAgent() throws Exception {
    URL configFileURL = new URL(_agent.getConfigurationFileURL());
    GoogleCredentials credentials = GoogleCredentials.fromStream(configFileURL.openStream());
    checkNotNull(credentials, "Credentials used to initialise FireStore are null.");

    FirebaseOptions options = new FirebaseOptions.Builder()
            .setCredentials(credentials)
            .build();
    if (FirebaseApp.getApps().isEmpty()) {
      FirebaseApp.initializeApp(options);
    }
    _database = FirestoreClient.getFirestore();
  }

  /**
   * Specify the firestore configuration where messages will be read / written.
   * <p>
   * TODO(Jeff): Make the db location configurable from an agent config file.
   *
   * @param conversationId
   * @return
   */
  private CollectionReference getDbCollection(String conversationId) {
    CollectionReference collectionReference = _database.collection("wizard")
            .document(_projectId)
            .collection("conversations")
            .document(conversationId)
            .collection("messages");
    return collectionReference;
  }

  @Override
  public String getAgentId() {
    return _agentId;
  }

  @Override
  public ServiceProvider getServiceProvider() {
    return _agent.getServiceProvider();
  }

  @Override
  public ResponseLog getResponseFromAgent(InteractionRequest interactionRequest) throws Exception {
    String responseId = ResponseIdGenerator.generate();
    if (userExit(interactionRequest)) {
      Map<String, Object> data = new HashMap<>();
      data.put("interaction_text", "Goodbye!");
      return buildResponse(responseId, data);
    }
    return addUserRequestWaitForReply(responseId, interactionRequest);
  }


  /**
   * Determine whether a request is from a wizard or not. This should be contained
   * in the request parameters.
   * TODO(Jeff): Fix how a wizard is detected
   *
   * @param interactionRequest
   * @return true if the request is from the user (and not a wizard)
   */
  private boolean isRequestFromUser(InteractionRequest interactionRequest) {
    return !interactionRequest.getUserId().startsWith("ADwizard");
  }

  /**
   * Simple intent detector to exit the the conversation.
   *
   * @param interactionRequest
   * @return true if the intent is to exit the conversation
   */
  private boolean userExit(InteractionRequest interactionRequest) {
    return interactionRequest.getInteraction().getText().toLowerCase().equals("exit");
  }

  /**
   * The core of the wizard. This adds the current message to the database and waits for a reply.
   *
   * It listens on the conversation messages. When a new message is added it returns a response
   * with the given input text.
   *
   * @param responseId
   * @param interactionRequest
   * @return
   * @throws InterruptedException
   * @throws ExecutionException
   * @throws TimeoutException
   */
  private ResponseLog addUserRequestWaitForReply(String responseId,
                                                 InteractionRequest interactionRequest)
          throws InterruptedException, ExecutionException, TimeoutException {
    checkNotNull(interactionRequest, "The passed interaction request is null!");

    LOGGER.debug("Handling request from client" + interactionRequest.getClientId());

    // Create a future for our aysnc response.
    final SettableApiFuture<ResponseLog> future = SettableApiFuture.create();

    // Get the conversation id from the request parameters.
    Map<String, Value> fieldsMap = interactionRequest.getAgentRequestParameters().getFieldsMap();
    if (!fieldsMap.containsKey("conversationId")) {
      throw new IllegalArgumentException("Request must specify the conversationId in the agent request parameters.");
    }
    String conversationId = fieldsMap.get("conversationId").getStringValue();

    DocumentReference documentReference =
            addInteractionRequestToDatabase(responseId, conversationId, interactionRequest);

    // Now we wait for a response!
    CollectionReference conversationCollection = getDbCollection(conversationId);

    // Wait for a response after the current time (filter out past messages).
    Query query = conversationCollection
            .whereGreaterThan("timestamp", com.google.cloud.Timestamp.now());
    LOGGER.debug("Waiting on listener: " + query.toString());
    // TODO(Jeff): Replace with lambda.
    ListenerRegistration registration = query.addSnapshotListener(new EventListener<QuerySnapshot>() {
      @Override
      public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirestoreException e) {
        if (e != null) {
          LOGGER.error("Listen failed: " + e);
          return;
        }
        ResponseLog response = null;
        if (snapshots != null && !snapshots.isEmpty()) {
          List<DocumentChange> documentChanges = snapshots.getDocumentChanges();
          LOGGER.debug("Num document changes:" + documentChanges.size());
          DocumentChange lastChange = documentChanges.get(documentChanges.size() - 1);
          Map<String, Object> changeData = lastChange.getDocument().getData();

          if (changeData.get("user_id").equals(interactionRequest.getUserId())) {
            // Message is from the same user, don't build a response from the same user.
            LOGGER.info("Ignoring change from the same user:" + interactionRequest.getUserId());
            return;
          }
          switch (lastChange.getType()) {
            case ADDED:
              response = buildResponse(responseId, changeData);
          }
          // Why do we need to do this?
          if (!future.isDone()) {
            future.set(response);
          }
        }
      }
    });
    final ResponseLog response = future.get(DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS);
    // Stop listening to changes
    registration.remove();
    return response;
  }

  /**
   * Builds the response message from a firestore database message.
   *
   * Currently, this only sends back the interaction_text from the data.
   *
   * @param responseId
   * @param data
   * @return
   */
  private ResponseLog buildResponse(String responseId, Map<String, Object> data) {
    Timestamp timestamp = Timestamp.newBuilder()
            .setSeconds(Instant.now()
                    .getEpochSecond())
            .setNanos(Instant.now()
                    .getNano())
            .build();

    // Get the input text, use a fallback
    Object messageText = data.get("interaction_text");
    String responseString = null;
    if (messageText != null) {
      responseString = (String) messageText;
    } else {
      LOGGER.error("Message does not contain text. Returning fallback. For response: " + responseId);
      responseString = "I'm sorry, message does not contain text.";
    }

    return ResponseLog.newBuilder()
            .setResponseId(responseId)
            .setTime(timestamp)
            .setClientId(Client.ClientId.EXTERNAL_APPLICATION)
            .setServiceProvider(ServiceProvider.WIZARD)
            .setMessageStatus(MessageStatus.SUCCESSFUL)
            .setRawResponse("Text response: " + responseString)
            .addAction(SystemAct.newBuilder().setInteraction(OutputInteraction.newBuilder()
                    .setType(InteractionType.TEXT) // TODO(Jeff): Support more advanced actions.
                    .setText(responseString)
                    .build())
            ).build();
  }

  /**
   * Add an interaction request to the message database.
   * @param responseId
   * @param conversationId
   * @param interactionRequest
   * @return Reference to the document added.
   */
  private DocumentReference addInteractionRequestToDatabase(String responseId, String conversationId, InteractionRequest interactionRequest) {
    DocumentReference chatReference = getDbCollection(conversationId).document(responseId);
    Map<String, Object> data = new HashMap();
    data.put("response_id", responseId);
    data.put("client_id", interactionRequest.getClientIdValue());
    data.put("time_seconds", interactionRequest.getTime().getSeconds());
    data.put("time_nanos", interactionRequest.getTime().getNanos());
    data.put("user_id", interactionRequest.getUserId());
    data.put("interaction_type", interactionRequest.getInteraction().getTypeValue());
    data.put("interaction_device_type", interactionRequest.getInteraction().getDeviceType
            ());
    data.put("interaction_language_code", interactionRequest.getInteraction()
            .getLanguageCode());
    data.put("interaction_text", interactionRequest.getInteraction().getText());
    data.put("interaction_audio_bytes", interactionRequest.getInteraction().getAudioBytes
            ());
    data.put("interaction_action_list", interactionRequest.getInteraction().getActionList
            ().toString());
    data.put("timestamp", com.google.cloud.Timestamp.now());
    chatReference.set(data);
    return chatReference;
  }

}