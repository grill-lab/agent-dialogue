package edu.gla.kail.ad.core;

import com.google.cloud.Tuple;
import com.google.protobuf.Timestamp;
import edu.gla.kail.ad.Client.InputInteraction;
import edu.gla.kail.ad.Client.InteractionRequest;
import edu.gla.kail.ad.Client.InteractionType;
import edu.gla.kail.ad.Client.OutputInteraction;
import edu.gla.kail.ad.core.Log.LogEntry;
import edu.gla.kail.ad.core.Log.LogEntryOrBuilder;
import edu.gla.kail.ad.core.Log.RequestLog;
import edu.gla.kail.ad.core.Log.ResponseLog;
import edu.gla.kail.ad.core.Log.ResponseLog.Builder;
import edu.gla.kail.ad.core.Log.ResponseLog.MessageStatus;
import edu.gla.kail.ad.core.Log.ResponseLog.ServiceProvider;
import edu.gla.kail.ad.core.Log.ResponseLogOrBuilder;
import edu.gla.kail.ad.core.Log.Slot;
import edu.gla.kail.ad.core.Log.SystemAct;
import edu.gla.kail.ad.core.Log.Turn;
import edu.gla.kail.ad.core.Log.TurnOrBuilder;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The manager is configured to have conversations with specified agents (of certain agent type,
 * * e.g. Dialogflow or Alexa).
 * Conversation management includes handling session state, including starting sessions (assigning
 * session IDs), as well as request identifiers. It also handles serialization of conversation log
 * data.
 *
 * Instruction of usage:
 * 1) Set up the Dialog agents using setUpAgents method.
 * 2) Call the getResponseFromAgent for passed input.
 * 3)
 * TODO(Adam): further part needs to be implemented
 *
 * Example usage :
 * DialogAgentManager dialogAgentManager = new DialogAgentManager();
 * dialogAgentManager.setUpAgents(_configurationTuples);
 * dialogAgentManager.getResponse(interactionRequest);
 **/

public class DialogAgentManager {
    // List of instances of used Dialog agents.
    private List<AgentInterface> _agents;
    // Session ID is a unique identifier of a session which is assigned by the method
    // startSession() called by DialogAgentManager constructor.
    private String _sessionId;
    // One LogEntry is stored per session.
    private LogEntryOrBuilder _logEntryBuilder;

    /**
     * Create a unique session ID generated with startSession() method.
     */
    public DialogAgentManager() {
        startSession();
        _logEntryBuilder = LogEntry.newBuilder();
    }

    /**
     * Method used for testing the server.
     * TODO(Adam): Delete after testing is done.
     *
     * @param interactionRequest - interactionRequest sent by the client.
     * @return interactionResponse - dummy instance of InteractionResponse created for testing
     *         output.
     */
    public ResponseLog getResponseFromAgentAsInteractionResponse(InteractionRequest
                                                                         interactionRequest) {
        return ResponseLog.newBuilder()
                .setMessageStatus(MessageStatus.SUCCESSFUL)
                .setResponseId("Setting response Id was successful")
                .setTime(getCurrentTimeStamp())
                .setClientId("Setting Client Id was successful")
                .setServiceProvider(ServiceProvider.NOTSET)
                .setRawResponse("RawRespose set by getResponseFromAgentAsInteractionResponse " +
                        "function of DialogAgentManager.")
                .addAction(SystemAct.newBuilder()
                        .setAction("The name of the action we get from the Agent's API.")
                        .setInteraction(OutputInteraction.newBuilder()
                                .setType(InteractionType.TEXT)
                                .setText("Setting OutputInteraction text was succrssful")
                                .addAction("Adding OutputInteraction Action was successful")
                                .build())
                        .addSlot(Slot.newBuilder()
                                .setName("Name of slot set by getRespo...")
                                .setValue("Value set by getRespo...").build()).build())
                .build();
    }

    /**
     * Create a unique sessionId.
     */
    private void startSession() {
        _sessionId = getRandomID();
    }

    /**
     * Called before the end of the session to store the log in the file.
     */
    public void endSession() {
        // ((LogEntry.Builder) _logEntryBuilder).build().writeTo();
        // TODO(Adam): Creation of configuration file - to be done later on.
        // TODO(Adam): Updating/Saving to the log file?
    }

    /**
     * Creates random ID used for session ID and request ID.
     *
     * @return String - A random ID consisting of timestamp and a random id generated by UUID.
     */
    private String getRandomID() {
        return (new java.sql.Timestamp(System.currentTimeMillis())).toString() + UUID.randomUUID
                ().toString();
    }

    /**
     * Set up (e.g. authenticate) all agents and store them to the list of agents.
     * TODO(Jeff): What to do when sb calls this function a couple of times? Each time setting up agents? Make the function private?
     *
     * @param configurationTuples - The list stores the entities of ConfigurationTuple,
     *         which holds data required by each agent.
     * @throws IllegalArgumentException - Raised by _agents.add(new
     *         DialogflowAgent(_sessionId, agentSpecificData.get(0)));
     * @throws IOException, IllegalArgumentException
     */
    public void setUpAgents(List<ConfigurationTuple> configurationTuples) throws
            IllegalArgumentException, IOException {
        _agents = new ArrayList();
        for (ConfigurationTuple configurationTuple : configurationTuples) {
            switch (configurationTuple.get_agentType()) {
                case NOTSET:
                    break;
                case DIALOGFLOW:
                    List<Tuple> agentSpecificData = checkNotNull(configurationTuple
                            .get_agentSpecificData(), "The Dialogflow specific data is null!");
                    if (agentSpecificData.size() != 1) {
                        throw new IllegalArgumentException("The Dialogflow agent specific data " +
                                "passed is not valid for Dialogflow! It has to be project ID and " +
                                "Service Account key file directory.");
                    }
                    _agents.add(new DialogflowAgent(_sessionId, agentSpecificData.get(0)));
                    break;
                case DUMMYAGENT: // TODO(ADAM): Delete these agents after testing is done.
                    _agents.add(new DummyAgent());
                    break;
                case FAILINGEXCEPTIONDUMMYAGENT:
                    _agents.add(new FailingExceptionDummyAgent());
                    break;
                case FAILINGNULLDUMMYAGENT:
                    _agents.add(new FailingNullDummyAgent());
                    break;
                case FAILINGTIMEDUMMYAGENT:
                    _agents.add(new FailingTimeDummyAgent());
                    break;
                default:
                    throw new IllegalArgumentException("The type of the agent provided " +
                            "\"" +
                            configurationTuple
                                    .get_agentType() + "\" is not currently supported " +
                            "(yet)!");
            }
        }
    }

    /**
     * Take the request from the service and send back chosen response.
     * Add the Turn to the LogEntry stored within the instance.
     *
     * @param interactionRequest - The request sent by the client.
     * @return ResponseLog - The response chosen with a particular method from the list of responses
     *         obtained by calling all the agents.
     */
    public ResponseLog getResponse(InteractionRequest interactionRequest) throws Exception {
        RequestLog requestLog = RequestLog.newBuilder()
                .setRequestId(getRandomID())
                .setTime(getCurrentTimeStamp())
                .setClientId(interactionRequest.getClientId())
                .setInteraction(interactionRequest.getInteraction()).build();

        List<ResponseLog> responses = getResponsesFromAgents(interactionRequest.getInteraction());
        ResponseLog chosenResponse = chooseOneResponse(responses); // TODO(Jeff): This may throw exception. Should I leave the error and propagate to higher levels, or do something here?
        TurnOrBuilder turnBuilder = Turn.newBuilder()
                .setRequestLog(requestLog)
                .setResponseLog(chosenResponse);
        for (ResponseLog response : responses) {
            ((Turn.Builder) turnBuilder).addCandidateResponse(response);
        }
        Turn turn = ((Turn.Builder) turnBuilder).build();
        ((LogEntry.Builder) _logEntryBuilder).addTurn(turn);
        // TODO(Adam): Add writing turns to the file - storing as a log.

        return chosenResponse;
    }

    /**
     * Get Request from Client and convert it to the RequestLog.
     * Return the list of responses for a given request.
     * TODO(Adam): Maybe store the logs after each conversation; need to decide later on.
     *
     * @param inputInteraction - The a data structure (implemented in log.proto) holding
     *         the interaction input passed to agents.
     * @return List<ResponseLog> - The list of responses of all agents set up on the
     *         setUpAgents(...) method call.
     */
    private List<ResponseLog> getResponsesFromAgents(InputInteraction inputInteraction) {
        if (checkNotNull(_agents, "Agents are not set up! Use the method" +
                " setUpAgents() first.").isEmpty()) {
            throw new IllegalArgumentException("The list of agents is empty!");
        }
        List<ResponseLog> listOfResponseLogs = asynchronousAgentCaller(inputInteraction);
        // TODO(Adam) Remove when the log saving is implemented. Currently we can see the output.
        listOfResponseLogs.forEach(System.out::println);
        return listOfResponseLogs;
    }

    /**
     * Return the responses by calling agents asynchronously.
     *
     * @param inputInteraction - The a data structure (implemented in log .proto) holding
     *         the interaction input sent to the agent.
     * @return List<ResponseLog> - The list of responses of all agents set up on the
     *         setUpAgents(...) method call.
     */
    private List<ResponseLog> asynchronousAgentCaller(InputInteraction inputInteraction) {
        Observable<AgentInterface> agentInterfaceObservable = Observable.fromIterable(_agents);
        return (agentInterfaceObservable.flatMap(agentObservable -> Observable
                .just(agentObservable)
                .subscribeOn(Schedulers.computation())
                .take(5, TimeUnit.SECONDS) // Take only the observable emitted (completed)
                // within specified time.
                .map(agent -> callForResponseAndValidate(agent, inputInteraction))
        ).toList().blockingGet());
    }

    /**
     * Return the responses by calling agents synchronously.
     *
     * @param inputInteraction - The a data structure (implemented in log .proto) holding
     *         the interaction input sent to the agent.
     * @return List<ResponseLog> - The list of responses of all agents set up on the
     *         setUpAgents(...) method call.
     */
    private List<ResponseLog> synchronousAgentCaller(InputInteraction inputInteraction) {
        List<ResponseLog> listOfResponseLogs = new ArrayList();
        for (AgentInterface agent : _agents) {
            listOfResponseLogs.add(callForResponseAndValidate(agent, inputInteraction));
        }
        return listOfResponseLogs;
    }

    /**
     * Return a valid response from an agent within a set time period or return and unsuccessful
     * response.
     *
     * @param agent - The agent which
     * @param inputInteraction - The a data structure (implemented in log .proto) holding
     *         the interaction input sent to the agent.
     * @return ResponseLog - Response from the agent or unsuccessful reponse.
     */
    private ResponseLog callForResponseAndValidate(AgentInterface agent, InputInteraction
            inputInteraction) {
        // TODO(Jeff): Resend a call if unsuccessful?
        Callable<ResponseLog> callableCallForResponseAndValidate = () -> {
            try {
                return checkNotNull(agent.getResponseFromAgent(inputInteraction),
                        "The response from Agent was null!");
            } catch (Exception exception) {
                return ResponseLog.newBuilder()
                        .setMessageStatus(MessageStatus.UNSUCCESFUL)
                        .setErrorMessage(exception.getMessage())
                        .setServiceProvider(agent.getServiceProvider())
                        .setTime(Timestamp.newBuilder()
                                .setSeconds(Instant.now()
                                        .getEpochSecond())
                                .setNanos(Instant.now()
                                        .getNano())
                                .build())
                        .build();
            }
        };

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<ResponseLog> future = executor.submit(callableCallForResponseAndValidate);
        ResponseLog responseLog;
        try {
            responseLog = future.get(5, TimeUnit.SECONDS);
        } catch (Exception exception) {
            future.cancel(true); // Cancel and send a thread interrupt.
            ResponseLogOrBuilder responseLogBuilder = ResponseLog.newBuilder()
                    .setMessageStatus(MessageStatus.UNSUCCESFUL)
                    .setServiceProvider(agent.getServiceProvider())
                    .setTime(getCurrentTimeStamp());
            if (exception.getMessage() == null) {
                ((Builder) responseLogBuilder).setErrorMessage(exception.toString());
            } else {
                ((Builder) responseLogBuilder).setErrorMessage(exception.getMessage());
            }
            responseLog = ((Builder) responseLogBuilder).build();
        } finally {
            executor.shutdownNow();
        }
        return responseLog;
/*
      // TODO(Adam): Delete once received confirmation from Jeff.
      // TODO(Jeff): Check if the entire method is ok.
        try {
            ResponseLog responseLog = checkNotNull(agent.getResponseFromAgent(inputInteraction),
                    "The response from Agent was null!");
            return responseLog;
        } catch (Exception exception) {
            return ResponseLog.newBuilder()
                    .setMessageStatus(MessageStatus.UNSUCCESFUL)
                    .setErrorMessage(exception.getMessage())
                    .setServiceProvider(agent.getServiceProvider())
                    .setTime(getCurrentTimeStamp())
                    .build();
        }*/
    }


    /**
     * Choose the response from the list obtained from all the agents.
     *
     * @param responses - The list of ResponseLog responses obtained from agents.
     * @return ResponseLog - One of the responses chosen using specified ranking/choosing method.
     * @throws Exception - Throw when the list is not initialized or empty.
     */
    private ResponseLog chooseOneResponse(List<ResponseLog> responses) throws Exception {
        if (checkNotNull(responses, "The list passed to the chooseOneResponse function is not " +
                "initialized!").isEmpty()) {
            throw new IllegalArgumentException("The list of responses is empty!");
        }
        return chooseFirstValidResponse(responses);
    }

    /**
     * Choose the first successful response.
     *
     * @param responses - The list of ResponseLog responses obtained from agents.
     * @return ResponseLog - The first successful response or unsuccessful response if none of the
     *         provided responses were successful.
     */
    private ResponseLog chooseFirstValidResponse(List<ResponseLog> responses) {
        for (ResponseLog responseLog : responses) {
            if (responseLog.getMessageStatus() == MessageStatus.SUCCESSFUL) {
                return responseLog;
            }
        }
        return ResponseLog.newBuilder()
                .setMessageStatus(MessageStatus.UNSUCCESFUL)
                .setErrorMessage("None of the passed responses had a successful call to the agent.")
                .setTime(getCurrentTimeStamp())
                .build();
    }

    private Timestamp getCurrentTimeStamp() {
        return Timestamp.newBuilder()
                .setSeconds(Instant.now()
                        .getEpochSecond())
                .setNanos(Instant.now()
                        .getNano())
                .build();
    }

    // TODO(Adam): store the conversation in the log as a single Turn
}