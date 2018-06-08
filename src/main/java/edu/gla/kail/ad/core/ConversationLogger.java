package edu.gla.kail.ad.core;

// Class responsible for logging and reading logs.

public class ConversationLogger {
    private String _logFileDirectory;


    public ConversationLogger(String logFileDirectory) {
        this._logFileDirectory = logFileDirectory;
    }

    // Store a single response ResponseDataStructure from Dialogflow in a specified directory _logFileDirectory.
    public void storeDialogflowResponse(ResponseDataStructure responseDataStructure) {
        //TODO storing the DialogflowRepsonse: maybe using some data from responseDataStructure class to create a unique name of the log?
        System.out.println("This function would store the responseDataStructure in the log at " + _logFileDirectory +
                ", but needs to be implemented: CoversationLogger.storeDialogflowResponse(responseDataStructure).");
    }
}
