package edu.gla.kail.ad.uploader;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

public class FirestoreUploader {
    private static final String _AVAILABLE_COMMANDS = "\nAvailable commands:\n" +
            "\nquit - Exit the application immediately and stop all processes." +
            "\nnew - Add a path to the tsv file to be processed and process it." +
            "\nwait - Wait for the application to finish processing all the requests and then " +
            "quit." +
            "\nnumber - Get the number of files to be processed." +
            "\nhelp - Get the available commands list.";

    private static Firestore _database;


    public static void main(String[] args) {


        Runnable userInterfaceRunnable = () -> {
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
                }

            }
            while (true) {
                String command = scanner.nextLine();
                switch (command) {
                    case "help":
                        System.out.println(client._AVAILABLE_COMMANDS);
                        break;
                    case "quit":
                        System.out.println("Bye bye!");
                        System.exit(0);
                    case "new":
                        System.out.println("Type the path to the logEntry file: ");
                        String providedLogEntryDirectory = scanner.nextLine();
                        directory = new File(providedLogEntryDirectory);
                        if (!directory.exists()) {
                            System.out.println("The provided path to the log file is invalid:\n" +
                                    directory.toString() + "\nTry again!\n");
                        } else {
                            client._logEntryFilePathQueue.add(directory);
                        }
                        break;
                    case "wait":
                        client._quit = true;
                        System.out.println("The application will quit once all the requests are " +
                                "processed. In the meantime you can still interact with the " +
                                "applciation.\nCurrent number of running threads: " + client
                                ._numberOfThreadsRunning.get());
                        break;
                    case "number":
                        System.out.println("The number of logReplayer threads currently running: " +
                                "" + client._numberOfThreadsRunning.get());
                        break;
                    default:
                        System.out.println("Unrecognised command, try again!\n" + client
                                ._AVAILABLE_COMMANDS);
                }
                System.out.println("Enter a command or type 'help' to get the list of commands: ");
            }
        };


    }

}
