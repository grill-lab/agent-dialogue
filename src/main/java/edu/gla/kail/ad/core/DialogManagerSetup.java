package edu.gla.kail.ad.core;

import static com.google.common.base.Preconditions.checkNotNull;

import edu.gla.kail.ad.core.Log.LogEntry;

public class DialogManagerSetup {
    private String _languageCode;
    private String _sessionId;
    private Log.LogEntry _logEntry;

    public DialogManagerSetup(String languageCode, String sessionId, LogEntry logEntry) throws Exception {
        this._languageCode = checkNotNull(languageCode,
                "Language code not specified! Example of a language code \"en-US\"");
        if (sessionId.isEmpty()) {
            throw new Exception("The session id needs to be defined!");
        } else {
            this._sessionId = sessionId;
        }
        this._logEntry = checkNotNull(logEntry, "You must provide Log.Builder for the initialization" +
                " of the class! Current log is null!");

    }

    public String get_languageCode() {
        return _languageCode;
    }

    public String get_sessionId() {
        return _sessionId;
    }

    public Log.LogEntry get_logEntry() {
        return _logEntry;
    }
}
