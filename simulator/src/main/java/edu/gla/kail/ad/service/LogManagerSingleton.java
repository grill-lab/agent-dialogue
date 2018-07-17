package edu.gla.kail.ad.service;

import edu.gla.kail.ad.Client.InteractionRequest;
import edu.gla.kail.ad.Client.InteractionResponse;
import org.joda.time.DateTime;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Manage Client interactions and store them in a certain directory/path.
 * TODO(Adam): Implement the use of LogStash, or maybe log4j, or make this thread safe.
 * TODO(Adam): Setting log storage directory to be changed to a different directory/database.
 */
public class LogManagerSingleton {
    private static LogManagerSingleton _instance;
    private static OutputStream _outputStream;

    /**
     * Get instance of this class.
     *
     * @return LogManagerSingleton - An instance of the class itself.
     */
    static synchronized LogManagerSingleton getLogManagerSingleton() {
        if (_instance == null) {
            _instance = new LogManagerSingleton();
            // Directory to the folder with logs.
            String logTurnPath = System.getProperty("user.dir") + "/Logs/client_interaction_logs";
            directoryExistsOrCreate(logTurnPath);
            logTurnPath += DateTime.now().toString();
            try {
                _outputStream = new FileOutputStream(logTurnPath);
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
            directory.mkdir();
        }
    }


    /**
     * Add the interaction to the Output buffer. In case of null parameters, nothing happens.
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
            interactionRequest.writeDelimitedTo(_outputStream);
        }
        if (interactionResponse != null) {
            interactionResponse.writeDelimitedTo(_outputStream);
        }
    }


    /**
     * Close the stream = save the file; set the instance to null.
     *
     * @throws IOException - Thrown when... TODO
     */
    public void saveAndExit() throws IOException {
        // TODO(Adam): Handle the exception.
        _outputStream.close();
        _instance = null;
    }

}