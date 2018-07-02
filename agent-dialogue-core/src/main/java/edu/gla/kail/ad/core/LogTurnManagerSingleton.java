package edu.gla.kail.ad.core;

import edu.gla.kail.ad.core.Log.Turn;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Manage turns and store them in the output stream.
 * TODO(Adam): Imlement the use of LogStash, or maybe log4j, or make this thread safe.
 * TODO(Adam): Setting log storage directory to be changed to a different directory/database.
 */
public final class LogTurnManagerSingleton {
    private static LogTurnManagerSingleton _instance;
    private static OutputStream _outputStream;

    /**
     * Get instance of this class.
     *
     * @return LogTurnManagerSingleton - An instance of the class itself.
     */
    static synchronized LogTurnManagerSingleton getLogTurnManagerSingleton() {
        if (_instance == null) {
            _instance = new LogTurnManagerSingleton();
            // Directory to the folder with logs.
            String logTurnPath = System.getProperty("user.dir") + "/Logs/DailyTurns";
            directoryExistsOrCreate(logTurnPath);
            try {
                _outputStream = new FileOutputStream(logTurnPath);
            } catch (Exception exception) {
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
     * Add the message (Turn) to the Output buffer.
     *
     * @param turn - The instance of Turn from proto buffer to be saved.
     * @throws IOException - Thrown when... TODO
     */
    public synchronized void addTurn(Turn turn) throws IOException {
        // TODO(Adam): Handle the exception.
        turn.writeDelimitedTo(_outputStream);
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
