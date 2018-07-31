package edu.gla.kail.ad.uploader;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;


/**
 * All the fields in the sheet must be filled.
 * The number of filled columns for each row must be the same.
 */
public class FirestoreUploader {
    private static final String _AVAILABLE_COMMANDS = "\nAvailable commands:\n" +
            "\nquit - Exit the application immediately and stop all processes." +
            "\nnew - Add a path to the tsv file to be processed and process it." +
            "\nwait - Wait for the application to finish processing all the requests and then " +
            "quit." +
            "\nnumber - Get the number of files to be processed." +
            "\nhelp - Get the available commands list.";

    // True if the user wants to wait for all the threads to finish and then quit.
    private static boolean _quit = false;
    private static ConcurrentLinkedQueue<File> _tsvFilePathQueue = new ConcurrentLinkedQueue<>();
    private static Firestore _database;

    // Thread running as 'frontend' - collect the input from user.
    private static Runnable _userInterfaceRunnable = () -> {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String command = scanner.nextLine();
            switch (command) {
                case "help":
                    System.out.println(_AVAILABLE_COMMANDS);
                    break;
                case "quit":
                    System.out.println("Bye bye!");
                    System.exit(0);
                case "new":
                    System.out.println("Type the path to the tsv file: ");
                    String providedLogEntryDirectory = scanner.nextLine();
                    File directory = new File(providedLogEntryDirectory);
                    if (!directory.exists()) {
                        System.out.println("The provided path to the log file is invalid:\n" +
                                directory.toString() + "\nTry again!\n");
                    } else {
                        _tsvFilePathQueue.add(directory);
                    }
                    break;
                case "wait":
                    _quit = true;
                    System.out.println("The application will quit once all the requests are " +
                            "processed. In the meantime you can still interact with the " +
                            "applciation.\nCurrent number of files to be processed: " +
                            _tsvFilePathQueue.size());
                    break;
                case "number":
                    System.out.println("The number of logReplayer threads currently running: " +
                            "" + _tsvFilePathQueue.size());
                    break;
                default:
                    System.out.println("Unrecognised command, try again!\n" +
                            _AVAILABLE_COMMANDS);
            }
            System.out.println("Enter a command or type 'help' to get the list of commands: ");
        }
    };

    // Thread running as 'backend' - takes requests from the queue at given rate.
    private static Runnable _queueCheckerRunnable = () -> {
        while (true) {
            if (_quit && _tsvFilePathQueue.size() == 0) {
                System.out.println("All the threads have finished running!\nBye bye!");
                System.exit(0);
            }
            try {
                if (!_tsvFilePathQueue.isEmpty()) {
                    File tsvFilePath = _tsvFilePathQueue.poll();
                    updateTheDatabase(tsvFilePath);
                } else {
                    // Sleep call makes the queueCheckerRunnable run requests at given rate (time
                    // period).
                    TimeUnit.SECONDS.sleep(1);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                System.exit(0);
            }
        }
    };

    private static void authorizeFirestore() {
        GoogleCredentials credentials;
        Scanner scanner = new Scanner(System.in);
        System.out.println("Hi, I'm the log replayer. Please specify the path to the JSON " +
                "Authorization file for the Firestore Database: \n");

        // Run until the user provides a correct file directory.
        while (true) {
            String pathToJsonFile = scanner.nextLine();
            InputStream serviceAccount;
            try {
                serviceAccount = new FileInputStream(pathToJsonFile);
            } catch (FileNotFoundException fileNotFoundException) {
                System.out.println("The file could not be found. Please specify the " +
                        "correctpath to the JSON Authorization file for the Firestore " +
                        "Database: \n");
                continue;
            }
            try {
                credentials = GoogleCredentials.fromStream(serviceAccount);
            } catch (IOException ioException) {
                System.out.println(ioException.getMessage());
                System.out.println("\nThe credentials for the Firebase are not valid. Try " +
                        "again by specifying the correctpath to the JSON Authorization file " +
                        "for the Firestore Database: \n");
                continue;
            }
            break;
        }
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(credentials)
                .build();
        FirebaseApp.initializeApp(options);
        _database = FirestoreClient.getFirestore();

        System.out.println("Google Firestore Authentication was successful.\nHow can I help " +
                "you?\n" + _AVAILABLE_COMMANDS);
    }

    private static void updateTheDatabase(File tsvFile) {
        try {
            BufferedReader tsvFileBufferedReader = new BufferedReader(new FileReader(tsvFile));
            String firstRow = tsvFileBufferedReader.readLine(); // Read first line.
            StringTokenizer stringTokenizer = new StringTokenizer(firstRow, "\t");
            String indicator = (String) stringTokenizer.nextElement();

            // Read parameters (keys) for the Firestore. Set tsvFileBufferedReader to third row.
            stringTokenizer = new StringTokenizer(tsvFileBufferedReader.readLine(), "\t");
            ArrayList<String> arrayOfParameters = new ArrayList<String>();
            while (stringTokenizer.hasMoreElements()) {
                arrayOfParameters.add(stringTokenizer.nextElement().toString());
            }

            // Execute depending on the content of the file.
            switch (indicator) {
                case "experiments":
                    handleExperiments(tsvFileBufferedReader, arrayOfParameters);
                    break;
                case "users":
                    handleUsers(tsvFileBufferedReader, arrayOfParameters);
                    break;
                case "tasks":
                    handleTasks(tsvFileBufferedReader, arrayOfParameters);
                    break;
                default:
                    System.out.println("Wrong data indicator: " + indicator);
                    throw new IOException();
            }
            System.out.println("The database was uploaded successfully with the following file: " +
                    "\n\n" + tsvFile.getAbsolutePath());
            tsvFileBufferedReader.close();
        } catch (FileNotFoundException fileNotFoundException) {
            System.out.println("File could not be found: " + tsvFile.getAbsolutePath());
        } catch (IOException ioException) {
            System.out.println("The data in the file:\n" + tsvFile.getAbsolutePath() + "\nis " +
                    "incorrectly formatted. The first line could not be read correctly.\n");
        }
    }

    private static void handleExperiments(BufferedReader tsvFileBufferedReader, ArrayList<String>
            arrayOfParameters) throws IOException {
        // TODO(Adam): Implement.
    }

    private static void handleUsers(BufferedReader tsvFileBufferedReader, ArrayList<String>
            arrayOfParameters) throws IOException {
        String nextRow;
        StringTokenizer stringTokenizer;
        try {
            // Read third line where the data starts.
            nextRow = tsvFileBufferedReader.readLine();
        } catch (IOException exception) {
            System.out.println("Could not read the tsv file data.\n" + exception.getMessage());
            throw new IOException();
        }

        while (nextRow != null) {
            stringTokenizer = new StringTokenizer(nextRow, "\t");
            Map<String, Object> updateHelperMap = new HashMap<>();
            ArrayList<String> dataArray = new ArrayList<String>();
            while (stringTokenizer.hasMoreElements()) {
                dataArray.add(stringTokenizer.nextElement().toString());
            }
            for (int i = 0; i < dataArray.size(); i++) {
                updateHelperMap.put(arrayOfParameters.get(i), dataArray.get(i));
            }
            DocumentReference userDocRef = _database
                    .collection("clientWebSimulator")
                    .document("agent-dialogue-experiments")
                    .collection("users")
                    .document(updateHelperMap.get("userId").toString());
            userDocRef.set(updateHelperMap);
            nextRow = tsvFileBufferedReader.readLine();
        }
    }

    /**
     * This method only differs from handleUsers by lines:
     *      .collection("experiments")
     *      .document(updateHelperMap.get("experimentId").toString());
     * Depending on future development of the program, these functions may be combined.
     */
    private static void handleTasks(BufferedReader tsvFileBufferedReader, ArrayList<String>
            arrayOfParameters) throws IOException {
        String nextRow;
        StringTokenizer stringTokenizer;
        try {
            // Read third line where the data starts.
            nextRow = tsvFileBufferedReader.readLine();
        } catch (IOException exception) {
            System.out.println("Could not read the tsv file data.\n" + exception.getMessage());
            throw new IOException();
        }

        while (nextRow != null) {
            stringTokenizer = new StringTokenizer(nextRow, "\t");
            Map<String, Object> updateHelperMap = new HashMap<>();
            ArrayList<String> dataArray = new ArrayList<String>();
            while (stringTokenizer.hasMoreElements()) {
                dataArray.add(stringTokenizer.nextElement().toString());
            }
            for (int i = 0; i < dataArray.size(); i++) {
                updateHelperMap.put(arrayOfParameters.get(i), dataArray.get(i));
            }
            DocumentReference experimentDocRef = _database
                    .collection("clientWebSimulator")
                    .document("agent-dialogue-experiments")
                    .collection("experiments")
                    .document(updateHelperMap.get("experimentId").toString());
            experimentDocRef.set(updateHelperMap);
            nextRow = tsvFileBufferedReader.readLine();
        }
    }


    public static void main(String[] args) {
        authorizeFirestore();
        new Thread(_queueCheckerRunnable).start();
        new Thread(_userInterfaceRunnable).start();
    }
}