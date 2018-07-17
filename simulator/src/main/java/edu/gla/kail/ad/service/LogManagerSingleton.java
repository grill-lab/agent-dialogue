package edu.gla.kail.ad.service;

import com.google.protobuf.Timestamp;
import edu.gla.kail.ad.Client.InteractionRequest;
import edu.gla.kail.ad.Client.InteractionResponse;
import edu.gla.kail.ad.eval.Ratings.Rating;
import edu.gla.kail.ad.eval.Ratings.Rating.Builder;
import edu.gla.kail.ad.eval.Ratings.RatingOrBuilder;
import org.joda.time.DateTime;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.Instant;

/**
 * Manage Client _interactions and store them in a certain directory/path.
 * TODO(Adam): Implement the use of LogStash, or maybe log4j, or make this thread safe.
 * TODO(Adam): Setting log storage directory to be changed to a different directory/database.
 */
public class LogManagerSingleton {
    private static LogManagerSingleton _instance;
    private static OutputStream _interactionsOutputStream;
    private static OutputStream _ratingsOutputStream;

    /**
     * Get instance of this class.
     *
     * @return LogManagerSingleton - An instance of the class itself.
     */
    static synchronized LogManagerSingleton getLogManagerSingleton() {
        if (_instance == null) {
            _instance = new LogManagerSingleton();
            // Directory to the folder with logs.
            String logInteractionsPath = System.getProperty("user.dir") + "/simulator/Logs/interactions_logs";
            directoryExistsOrCreate(logInteractionsPath);
            logInteractionsPath += DateTime.now().toString();
            try {
                _interactionsOutputStream = new FileOutputStream(logInteractionsPath);
            } catch (IOException exception) {
                exception.getMessage();
            }

            String logRatingsPath = System.getProperty("user.dir") + "/simulator/Logs/ratings_logs";
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
        } else if (interactionResponse != null) {
            interactionResponse.writeDelimitedTo(_interactionsOutputStream);
        }
    }

    /**
     * Add the ratings to the rating Output buffer.
     * @param ratingScore
     * @param responseId
     * @param experimentId
     * @param utteranceId
     * @param requestId
     * @throws IOException
     */
    public synchronized void addRating(String ratingScore, String responseId, String
            experimentId, @Nullable String utteranceId, @Nullable String requestId) throws
            IOException {
        RatingOrBuilder ratingOrBuilder = Rating.newBuilder()
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
        if (utteranceId != null) {
            ((Builder) ratingOrBuilder).setUtteranceId(utteranceId);
        }
        ((Builder) ratingOrBuilder).build().writeDelimitedTo(_ratingsOutputStream);
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