package edu.gla.kail.ad.core;

import com.google.api.core.SettableApiFuture;
import com.google.cloud.firestore.*;
import edu.gla.kail.ad.Client;
import edu.gla.kail.ad.Client.InteractionRequest;
import edu.gla.kail.ad.Client.InteractionType;
import edu.gla.kail.ad.Client.OutputInteraction;
import edu.gla.kail.ad.core.Log.ResponseLog;
import edu.gla.kail.ad.CoreConfiguration.Agent;
import edu.gla.kail.ad.CoreConfiguration.ServiceProvider;
import edu.gla.kail.ad.core.Log.Slot;
import edu.gla.kail.ad.core.Log.SystemAct;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import com.google.protobuf.Timestamp;

import javax.annotation.Nullable;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * This is a Wizard-of-Oz agent created for experiments. It allows multiple users to chat
 * with one another.
 * It uses Firestore for storing the messages and to listen for events on shared chat 'documents'.
 */
class WizardAgent implements AgentInterface {

  private Firestore _database;

  private String _firebaseCredentialsPath =
          "src/main/resources/agentdialogue-2cd4b-firebase-adminsdk-z39zw" +
          "-4d5427d1fc.json";

  // Analogous to the "wizard project" - something like a project id in a dialogflow agent.
  private String _projectId = "Ct5UbiTWQmkDbmF0aJrt";

  // A unique ID passed set in the constructor, passed by DialogAgentManager.
  private String _sessionId = "1";

  private String _conversationId = "NslkRXnAntZJa2QRvet6";

  private static final long TIMEOUT_SECONDS = 180;

  /**
   * Construct a new WizardAgent.
   *
   * @param sessionId
   * @throws Exception
   */
  public WizardAgent(String sessionId) throws Exception {
    _sessionId = sessionId;
    initAgent();
  }

  /**
   * Initialize the agent.
   *
   * @throws Exception
   */
  private void initAgent() throws Exception {
    GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(_firebaseCredentialsPath));
    checkNotNull(credentials, "Credentials used to initialise FireStore are null.");

    FirebaseOptions options = new FirebaseOptions.Builder()
            .setCredentials(credentials)
            .build();
    FirebaseApp.initializeApp(options);
    _database = FirestoreClient.getFirestore();
  }

  private CollectionReference getDbCollection() {
    CollectionReference collectionReference = _database.collection("wizard")
            .document(_projectId)
            .collection("conversations")
            .document(_conversationId)
            .collection("messages");
    return collectionReference;
  }

  @Override
  public ServiceProvider getServiceProvider() {
    return ServiceProvider.WIZARD;
  }

  @Override
  public ResponseLog getResponseFromAgent(InteractionRequest interactionRequest) throws InterruptedException, ExecutionException, TimeoutException {
    String responseId = "made up response id";
      if (userExit(interactionRequest)) {
        Map<String, Object> data = new HashMap<>();
        data.put("interaction_text", "Goodbye!");
        return buildResponse(responseId, data);
      }
      return addUserRequestWaitForReply(interactionRequest, isRequestFromUser(interactionRequest));
  }

  private boolean isRequestFromUser(InteractionRequest interactionRequest) {
    return interactionRequest.getUserId().startsWith("ADwizard");
  }

  private boolean userExit(InteractionRequest interactionRequest) {
    return interactionRequest.getInteraction().getText().toLowerCase().equals("exit");
  }

  private ResponseLog addUserRequestWaitForReply(InteractionRequest interactionRequest, boolean isFromUser) throws InterruptedException, ExecutionException, TimeoutException {
    final SettableApiFuture<ResponseLog> future = SettableApiFuture.create();
    System.out.println("Handling user request.");
    checkNotNull(interactionRequest, "The passed interaction request is null!");
    String responseId = "made up response id";
    DocumentReference documentReference = addInteractionRequestToDatabase(_conversationId, interactionRequest, isFromUser);
    // Now we wait for a response!
    CollectionReference conversationCollection = getDbCollection();
    Query query = conversationCollection
            .whereGreaterThan("timestamp", com.google.cloud.Timestamp.now());
           // .whereEqualTo("from_user", !isFromUser);
    System.out.println("Waiting on listener: " + query.toString());
    ListenerRegistration registration = query.addSnapshotListener(new EventListener<QuerySnapshot>() {
      @Override
      public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirestoreException e) {
        if (e != null) {
          System.err.println("Listen failed: " + e);
          return;
        }
        ResponseLog response = null;
        if (snapshots != null && !snapshots.isEmpty()) {
          List<DocumentChange> documentChanges = snapshots.getDocumentChanges();
          System.out.println("num document change:" + documentChanges.size());
          DocumentChange lastChange = documentChanges.get(documentChanges.size() - 1);
          switch (lastChange.getType()) {
            case ADDED:
              response = buildResponse(responseId, lastChange.getDocument().getData());
          }
          if (!future.isDone()) {
            future.set(response);
          }
        }
      }
    });

    // Stop listening to changes
    //  registration.remove();
    return future.get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
  }

  private ResponseLog buildResponse(String responseId, Map<String, Object> data) {
    Timestamp timestamp = Timestamp.newBuilder()
            .setSeconds(Instant.now()
                    .getEpochSecond())
            .setNanos(Instant.now()
                    .getNano())
            .build();

    String responseString = data.getOrDefault("interaction_text",
            "I'm sorry, there's an error.").toString();

    ResponseLog.Builder responseLogBuilder = ResponseLog.newBuilder()
            .setResponseId(responseId)
            .setTime(timestamp)
            .setServiceProvider(ServiceProvider.DIALOGFLOW)
            .setRawResponse("Wizard responds with: " + responseString);

    return ResponseLog.newBuilder()
            .setResponseId(responseId)
            .setTime(timestamp)
            .setClientId(Client.ClientId.EXTERNAL_APPLICATION)
            .setServiceProvider(ServiceProvider.WIZARD)
            .setRawResponse(responseString)
            .addAction(SystemAct.newBuilder().setInteraction(OutputInteraction.newBuilder()
                    .setType(InteractionType.TEXT) // TODO(Jeff): If more advanced response for wizards.
                    .setText(responseString)
                    .build())
            ).build();
  }

  private DocumentReference addInteractionRequestToDatabase(String responseId, InteractionRequest interactionRequest, boolean isFromUser) {
    DocumentReference chatReference = getDbCollection().document(responseId);
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
    data.put("from_user", isFromUser);
    chatReference.set(data);
    return chatReference;
  }

}
