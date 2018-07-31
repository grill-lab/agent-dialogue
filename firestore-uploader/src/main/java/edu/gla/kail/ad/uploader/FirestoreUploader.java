package edu.gla.kail.ad.uploader;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

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
    private static LinkedBlockingQueue<File> _tsvFilePathQueue = new LinkedBlockingQueue<>();
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
            if (client._quit && client._numberOfThreadsRunning.get() == 0) {
                System.out.println("All the threads have finished running!\nBye bye!");
                System.exit(0);
            }
            try {
                // Sleep call makes the queueCheckerRunnable run requests at given rate (time
                // period).
                TimeUnit.SECONDS.sleep(1);
                if (!client._logEntryFilePathQueue.isEmpty() && client
                        ._numberOfThreadsRunning.get() <= client
                        ._MAXIMUM_NUMBER_OF_ONGOING_CONVERSATIONS) {
                    File logEntryFilePath = client._logEntryFilePathQueue.poll();
                    client.startReplayerThread(logEntryFilePath, client);
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

    public static void main(String[] args) {
        // Authorize the Firestore database.
        authorizeFirestore();
        new Thread(_queueCheckerRunnable).start();
        new Thread(_userInterfaceRunnable).start();
    }
}