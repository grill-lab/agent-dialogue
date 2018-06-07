package Metabot_core;

public class ConversationLogger {
    private String _logFileDirectory;


    public ConversationLogger(String logFileDirectory) {
        this._logFileDirectory = logFileDirectory;
    }

    public void storeDialogflowResponse(ResponseDataStructure responseDataStructure) {
        //TODO storing the DialogflowRepsonse: maybe using some data from responseDataStructure class to create a unique name of the log?
        System.out.println("This function would store the responseDataStructure in the log at " + _logFileDirectory +
                ", but needs to be implemented: CoversationLogger.storeDialogflowResponse(responseDataStructure).");
    }
}
