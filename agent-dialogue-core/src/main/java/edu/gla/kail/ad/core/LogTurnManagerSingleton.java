package edu.gla.kail.ad.core;

import edu.gla.kail.ad.core.Log.Turn;
import org.joda.time.DateTime;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Paths;

/**
 * Manage turns and store them in the output stream.
 * TODO(Adam): Implement the use of LogStash, or maybe log4j, or make this thread safe.
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
    public static synchronized LogTurnManagerSingleton getLogTurnManagerSingleton() {
        if (_instance == null) {
            _instance = new LogTurnManagerSingleton();
            // Directory to the folder with logs.
            String logTurnPath = Paths
                    .get(LogTurnManagerSingleton
                            .class
                            .getProtectionDomain()
                            .getCodeSource()
                            .getLocation()
                            .getPath())
                    .getParent()
                    .getParent()
                    .toString() + "/Logs/DailyTurns";
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
            directory.mkdirs();
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
        _outputStream.flush();
    }

    /**
     * Close the stream = save the file; set the instance to null.
     *
     * @throws IOException - Thrown when... TODO
     */
    public void saveAndExit() throws IOException {
        // TODO(Adam): Handle the exception.
        // TODO(Adam): Code below is buggy - it can create issues. Resolve it!
        _outputStream.close();
        _instance = null;
    }
}
