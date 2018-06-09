package edu.gla.kail.ad.core;

/*
 Class responsible for logging and reading logs.
 TODO(Adam) make the class thread safe
*/

public class ConversationLogger {
    public String _logFileDirectory;

    /* Store a single response ResponseDataStructure from Dialogflow in a specified directory.*/
    public void writeConversationResponse(ResponseDataStructure responseDataStructure) throws Exception {
        //TODO(Adam) storing the DialogflowRepsonse: maybe using some data from responseDataStructure class to create a unique name of the log?
        System.out.println("This function would store the responseDataStructure in the log at " + _logFileDirectory +
                ", but needs to be implemented: CoversationLogger.writeConversationResponse(responseDataStructure).");
        throw new Exception("Writing conversation responses is not yet supported");
    }
}
