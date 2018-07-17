package edu.gla.kail.ad.core;

import edu.gla.kail.ad.core.Log.LogEntry;
import edu.gla.kail.ad.core.Log.LogEntry.Builder;
import edu.gla.kail.ad.core.Log.LogEntryOrBuilder;
import edu.gla.kail.ad.core.Log.Turn;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Reformat all the turns from a file generated by LogTurnManagerSingleton to separate files of
 * LogEntries for each session.
 */
public final class LogEntryManager {
    /**
     * Segregate turns into LogEntries and safe LogEntries in a particular directory.
     *
     * @param readDirectory - Directory of a file to be read.
     * @param writeDirectory - Directory in which LogEntry files are going to be stored.
     * @throws IOException - TODO(Adam): Find out about the exception and handle it.
     */
    public static void segregateFiles(String readDirectory, String writeDirectory) throws
            IOException {
        InputStream inputStream = new FileInputStream(readDirectory);
        Map<String, List<Turn>> mapOfTurns = new HashMap<>();
        // Store all the turns in the map: key - sessionID, value - turn.
        while (true) {
            Turn turn = Turn.parseDelimitedFrom(inputStream);
            if (turn == null) {
                break;
            }
            if (!mapOfTurns.containsKey(turn.getSessionId())) {
                mapOfTurns.put(turn.getSessionId(), new ArrayList<Turn>());
            }
            mapOfTurns.get(turn.getSessionId()).add(turn);
        }

        // Write all the turns to separate LogEntry files.
        directoryExistsOrCreate(writeDirectory);
        for (List<Turn> listOfTurns : mapOfTurns.values()) {
            LogEntryOrBuilder logEntryOrBuilder = LogEntry.newBuilder().setSessionId(listOfTurns
                    .get(0).getSessionId());
            listOfTurns.sort((turn1, turn2) -> turn1.getRequestLog().getTime().getSeconds() >
                    turn2.getRequestLog().getTime().getSeconds() ? 1 : turn1.getRequestLog()
                    .getTime().getNanos() > turn2.getRequestLog().getTime().getNanos() ? 1 : -1);
            for (Turn turn : listOfTurns) {
                ((Builder) logEntryOrBuilder).addTurn(turn);
            }
            OutputStream outputStream = new FileOutputStream(writeDirectory + "/" +
                    listOfTurns.get(0).getSessionId() + ".log");
            ((Builder) logEntryOrBuilder).build().writeTo(outputStream);
            outputStream.close();
        }
    }

    /**
     * Validate whether the directory exists and if not, then create it.
     *
     * @param path - The directory to be validated.
     */
    private static void directoryExistsOrCreate(String path) {
        File directory = new File(path);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

}
