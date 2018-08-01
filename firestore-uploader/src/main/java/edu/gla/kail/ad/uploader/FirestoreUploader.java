package edu.gla.kail.ad.uploader;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
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
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static com.google.common.base.Preconditions.checkNotNull;


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
    private static AtomicInteger _numberOfScheduledOperations = new AtomicInteger(0);

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
                                directory.toString() + "\nTry again! Type new command:\n\n");
                    } else {
                        _tsvFilePathQueue.add(directory);
                        _numberOfScheduledOperations.incrementAndGet();
                    }
                    break;
                case "wait":
                    _quit = true;
                    System.out.println("The application will quit once all the requests are " +
                            "processed. In the meantime you can still interact with the " +
                            "application.\nCurrent number of files to be processed: " +
                            _numberOfScheduledOperations.get());
                    break;
                case "number":
                    System.out.println("The number of FirestoreUploader threads currently running and scheduled in the queue: " +
                            "" + _numberOfScheduledOperations.get());
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
            if (_quit && _numberOfScheduledOperations.get() == 0) {
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
                        "correct path to the JSON Authorization file for the Firestore " +
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
            ArrayList<String> arrayOfParameters = new ArrayList<>();
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
            System.out.println("\nThe database was uploaded successfully with the following file: \n\n" + tsvFile.getAbsolutePath() + "\nType new command:\n");
            tsvFileBufferedReader.close();
            _numberOfScheduledOperations.decrementAndGet();
        } catch (FileNotFoundException fileNotFoundException) {
            System.out.println("File could not be found: " + tsvFile.getAbsolutePath());
        } catch (IOException ioException) {
            System.out.println("The data in the file:\n" + tsvFile.getAbsolutePath() + "\nis " +
                    "incorrectly formatted. The first line could not be read correctly.\n");
        }
    }

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

        String taskId = null;
        HashMap<String, Object> updateHelperMap = new HashMap<>();
        HashMap<String, Object> turnMap = new HashMap<>();
        ArrayList<Object> turnsArray = new ArrayList<>();
        while (nextRow != null) {
            stringTokenizer = new StringTokenizer(nextRow, "\t");
            HashMap<String, Object> supportingHelperMap = new HashMap<>();
            ArrayList<String> dataArray = new ArrayList<>();
            while (stringTokenizer.hasMoreElements()) {
                dataArray.add(stringTokenizer.nextElement().toString());
            }
            for (int i = 0; i < dataArray.size(); i++) {
                supportingHelperMap.put(arrayOfParameters.get(i), dataArray.get(i));
            }
            // If line is empty, then read next line and continue executing while loop.
            if (dataArray.size() == 0) {
                nextRow = tsvFileBufferedReader.readLine();
                continue;
            }
            checkNotNull(supportingHelperMap.get("taskId"), "Table not formatted correctly; " +
                    "taskId " +
                    "non" +
                    " existent.");
            // If task if of the next data row is different from the previous task id, then add
            // the turn to the database.
            if (taskId != null && !taskId.equals(supportingHelperMap.get("taskId").toString())) {
                addTurnToDatabase(supportingHelperMap, updateHelperMap);
                updateHelperMap.clear();
            }
            // Update taskId.
            taskId = supportingHelperMap.get("taskId").toString();


            String type_of_turn = supportingHelperMap.remove("type_of_turn").toString();
            String utterance = supportingHelperMap.remove("utterance").toString();
            String time_seconds = supportingHelperMap.remove("time_seconds").toString();

            // If it is a new task, then populate updateHelperMap.
            if (updateHelperMap.isEmpty()) {
                for (Entry<String, Object> entry : supportingHelperMap.entrySet()) {
                    updateHelperMap.put(entry.getKey(), entry.getValue());
                }
            } else {
                turnsArray = (ArrayList<Object>) updateHelperMap.get("turns");
            }
            if (type_of_turn.equals("request") || type_of_turn.equals("response")) {
                turnMap.put(type_of_turn, utterance);
                turnMap.put("time_seconds", Integer.valueOf(time_seconds));
                turnMap.put("clientId", supportingHelperMap.get("clientId"));
                turnMap.put("deviceType", supportingHelperMap.get("deviceType"));
                turnMap.put("language_code", supportingHelperMap.get("language_code"));
            } else {
                System.out.println("Unrecognised type of turn: " + type_of_turn);
                throw new IOException();
            }
            turnsArray.add(turnMap);
            updateHelperMap.put("turns", turnsArray);

            nextRow = tsvFileBufferedReader.readLine();
            if (nextRow == null) {
                addTurnToDatabase(supportingHelperMap, updateHelperMap);
            }
            turnMap.clear();
            turnsArray.clear();
        }
    }

    private static void addTurnToDatabase(HashMap<String, Object> supportingHelperMap,
                                          HashMap<String, Object> updateHelperMap) {
        // Update or create experiment in experiments database.
        String experimentId = supportingHelperMap.get("experimentId").toString();
        ArrayList<String> taskIds = new ArrayList<>();
        DocumentReference experimentDocRef = _database
                .collection("clientWebSimulator")
                .document("agent-dialogue-experiments")
                .collection("experiments")
                .document(experimentId);
        Map<String, Object> createExperimentHelperMap = new HashMap<>();
        if (!verifyExperimentExistence(experimentId)) {
            // If not, then create experiment and add taskIds list.
            taskIds.add(supportingHelperMap.get("taskId").toString());
            createExperimentHelperMap.put("experimentId", experimentId);
            createExperimentHelperMap.put("taskIds", taskIds);
            experimentDocRef.set(createExperimentHelperMap);
        } else {
            // Get taskIds list and update it.
            try {
                if (experimentDocRef.get().get().getData().containsKey("taskIds")) {
                    taskIds = (ArrayList<String>) experimentDocRef.get().get().getData().get
                            ("taskIds");
                }
                taskIds.add(supportingHelperMap.get("taskId").toString());
                createExperimentHelperMap.put("taskIds", taskIds);
                experimentDocRef.update(createExperimentHelperMap);
            } catch (Exception exception) {
                System.out.println("There was a problem with getting taskIds list for " +
                        "the: " + experimentId + "\n" + exception.getStackTrace());
            }
        }

        // Create a new task.
        DocumentReference turnDocRef = _database
                .collection("clientWebSimulator")
                .document("agent-dialogue-experiments")
                .collection("tasks")
                .document(updateHelperMap.get("taskId").toString());
        turnDocRef.set(updateHelperMap);
    }

    private static Boolean verifyExperimentExistence(String experimentId) {
        if (experimentId == null || experimentId.equals("")) {
            return false;
        }
        DocumentReference experimentDocRef = _database
                .collection("clientWebSimulator")
                .document("agent-dialogue-experiments")
                .collection("experiments")
                .document(experimentId);
        ApiFuture<DocumentSnapshot> future = experimentDocRef.get();
        try {
            if (future.get().exists()) {
                return true;
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return false;
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
            ArrayList<String> dataArray = new ArrayList<>();
            while (stringTokenizer.hasMoreElements()) {
                dataArray.add(stringTokenizer.nextElement().toString());
            }
            for (int i = 0; i < dataArray.size(); i++) {
                updateHelperMap.put(arrayOfParameters.get(i), dataArray.get(i));
            }
            // If line is empty, then read next line and continue executing while loop.
            if (updateHelperMap.size() == 0) {
                nextRow = tsvFileBufferedReader.readLine();
                continue;
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
     * .collection("experiments")
     * .document(updateHelperMap.get("experimentId").toString());
     * Depending on future development of the program, these functions may be combined.
     */
    private static void handleExperiments(BufferedReader tsvFileBufferedReader, ArrayList<String>
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
            ArrayList<String> dataArray = new ArrayList<>();
            while (stringTokenizer.hasMoreElements()) {
                dataArray.add(stringTokenizer.nextElement().toString());
            }
            for (int i = 0; i < dataArray.size(); i++) {
                updateHelperMap.put(arrayOfParameters.get(i), dataArray.get(i));
            }
            // If line is empty, then read next line and continue executing while loop.
            if (updateHelperMap.size() == 0) {
                nextRow = tsvFileBufferedReader.readLine();
                continue;
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