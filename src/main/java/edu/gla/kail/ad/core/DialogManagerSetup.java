package edu.gla.kail.ad.core;

import static com.google.common.base.Preconditions.checkNotNull;

import edu.gla.kail.ad.core.Log.LogEntry;

/**
 * Constructor of this class creates LogEntry.Builder which is used for loggin of the entire conversation.
 */
public class DialogManagerSetup {
    private String _languageCode;
    private String _sessionId;
    private LogEntry.Builder _logEntryBuilder;

    public DialogManagerSetup(String languageCode, String sessionId) throws Exception {
        this._languageCode = checkNotNull(languageCode,
                "Language code not specified! Example of a language code \"en-US\"");
        if (sessionId.isEmpty()) {
            throw new Exception("The session id needs to be defined!");
        } else {
            this._sessionId = sessionId;
        }
        this._logEntryBuilder = LogEntry.newBuilder();
    }

    public String get_languageCode() {
        return _languageCode;
    }

    public String get_sessionId() {
        return _sessionId;
    }

    public LogEntry.Builder get_logEntry() {
        return _logEntryBuilder;
    }
}
