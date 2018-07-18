package edu.gla.kail.ad.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import com.google.protobuf.Timestamp;
import edu.gla.kail.ad.Client.InteractionRequest;
import edu.gla.kail.ad.Client.InteractionResponse;
import edu.gla.kail.ad.Client.OutputInteraction;
import edu.gla.kail.ad.eval.Ratings.Rating;
import edu.gla.kail.ad.eval.Ratings.Rating.Builder;
import edu.gla.kail.ad.eval.Ratings.RatingOrBuilder;
import org.joda.time.DateTime;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Manage Client _interactions and store them in a certain directory/path.
 * TODO(Adam): Implement the use of LogStash, or maybe log4j, or make this thread safe.
 * TODO(Adam): Setting log storage directory to be changed to a different directory/database.
 */
public class LogManagerSingleton {
    private static LogManagerSingleton _instance;
    private static OutputStream _interactionsOutputStream;
    private static OutputStream _ratingsOutputStream;
    private static Firestore _database;

    /**
     * Get instance of this class.
     *
     * @return LogManagerSingleton - An instance of the class itself.
     */
    static synchronized LogManagerSingleton getLogManagerSingleton() {
        if (_instance == null) {

            GoogleCredentials credentials;
            try {
                // TODO(Adam): Change it.
                InputStream serviceAccount = new FileInputStream
                        ("/Users/Adam/Documents/Internship/agentdialogue-72c1853e868d.json");
                credentials = GoogleCredentials.fromStream(serviceAccount);
            } catch (Exception e) {
                credentials = null;
                System.out.println();
            }
            checkNotNull(credentials, "Credentials used to initialise firestore are null.");
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(credentials)
                    .build();
            FirebaseApp.initializeApp(options);

            _database = FirestoreClient.getFirestore();


            _instance = new LogManagerSingleton();
            // Directory to the folder with logs.
            String logInteractionsPath = System.getProperty("user.dir") + "/Logs/interactions_logs";
            directoryExistsOrCreate(logInteractionsPath);
            logInteractionsPath += DateTime.now().toString();
            try {
                _interactionsOutputStream = new FileOutputStream(logInteractionsPath);
            } catch (IOException exception) {
                exception.getMessage();
            }

            String logRatingsPath = System.getProperty("user.dir") + "/Logs/ratings_logs";
            directoryExistsOrCreate(logRatingsPath);
            logRatingsPath += DateTime.now().toString();
            try {
                _ratingsOutputStream = new FileOutputStream(logRatingsPath);
            } catch (IOException exception) {
                exception.getMessage();
            }
        }
        return _instance;
    }

    /**
     * Validate whether the directory exists and if not, then create it.
     */
    private static void directoryExistsOrCreate(String path) {
        File directory = new File(path);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    /**
     * Add the interaction to the Output buffer. Nullable parameters are acceptable.
     *
     * @param interactionRequest - The instance of interactionRequest from proto buffer to
     *         be saved.
     * @param interactionResponse - The instance of interactionResponse from proto buffer to
     *         be saved.
     * @throws IOException - Thrown when... TODO
     */
    public synchronized void addInteraction(@Nullable InteractionRequest interactionRequest,
                                            @Nullable InteractionResponse interactionResponse)
            throws IOException {
        // TODO(Adam): Handle the exception.
        if (interactionRequest != null) {
            interactionRequest.writeDelimitedTo(_interactionsOutputStream);
            DocumentReference docRef = _database.collection("clientInteractionRequest").document
                    (interactionRequest.getTime().toString());
            Map<String, Object> data = new HashMap<>();

            data.put("client_id", interactionRequest.getClientIdValue());
            data.put("userID", interactionRequest.getUserID());
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

            docRef.set(data);
        } else if (interactionResponse != null) {
            interactionResponse.writeDelimitedTo(_interactionsOutputStream);
            DocumentReference docRef = _database.collection("clientInteractionResponse").document
                    (interactionResponse.getTime().toString());
            Map<String, Object> data = new HashMap<>();

            data.put("response_id", interactionResponse.getResponseId());
            data.put("client_id", interactionResponse.getClientIdValue());
            data.put("message_status", interactionResponse.getMessageStatusValue());
            data.put("error_message", interactionResponse.getErrorMessage());
            data.put("userID", interactionResponse.getUserID());
            docRef.set(data);

            Integer counter = 0;
            for (OutputInteraction outputInteraction : interactionResponse.getInteractionList()) {
                Map<String, Object> outputInteractionData = new HashMap<>();
                outputInteractionData.put("text", outputInteraction.getText());
                outputInteractionData.put("audio_bytes", outputInteraction.getAudioBytes());
                outputInteractionData.put("type", outputInteraction.getTypeValue());
                outputInteractionData.put("action_list", outputInteraction.getActionList()
                        .toString());
                docRef.collection("output_interaction").document(counter.toString()).set(data);
                counter++;
            }
        }
    }


    /**
     * Add the ratings to the rating Output buffer.
     *
     * @param ratingScore
     * @param responseId
     * @param experimentId
     * @param requestId
     * @throws IOException
     */
    public synchronized void addRating(String ratingScore, String responseId, String
            experimentId, @Nullable String requestId) throws
            IOException {
        RatingOrBuilder ratingOrBuilder = Rating.newBuilder()
                .setScore(ratingScore)
                .setResponseId(responseId)
                .setTime(Timestamp.newBuilder()
                        .setSeconds(Instant.now()
                                .getEpochSecond())
                        .setNanos(Instant.now()
                                .getNano())
                        .build())
                .setExperimentId(experimentId);
        if (requestId != null) {
            ((Builder) ratingOrBuilder).setRequestId(requestId);
        }
        Rating rating = ((Builder) ratingOrBuilder).build();
        rating.writeDelimitedTo(_ratingsOutputStream);

        DocumentReference docRef = _database.collection("clientRatings").document
                (rating.getTime().toString());
        Map<String, Object> data = new HashMap<>();

        data.put("experiment_id", rating.getExperimentId());
        data.put("response_id", rating.getResponseId());
        data.put("score", rating.getScore());
        data.put("request_id", rating.getRequestId());

        docRef.set(data);
    }


    /**
     * Close the stream = save the file; set the instance to null.
     *
     * @throws IOException - Thrown when... TODO
     */
    public void saveAndExit() throws IOException {
        // TODO(Adam): Handle the exception.
        _interactionsOutputStream.close();
        _instance = null;
    }

}