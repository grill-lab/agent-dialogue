package edu.gla.kail.ad.core;

import java.util.Date;

// Class stores all the data about one response.

public class ResponseDataStructure {
    //TODO(Adam) everything in the RepsonseDataStructure class
    private Date _currentTime = new java.util.Date();
    private String _projectId;
    private String _jsonKeyFileLocation;
    private String _sessionId;
    private String _languageCode;
    private String _utteranceText;
    private String _fulfillmentText;
    private String _responseId;
    private String _context;
    private String _intent;
    private String _action;
    private String _confidence;
    private String _event;
    private String _parameters;
}
