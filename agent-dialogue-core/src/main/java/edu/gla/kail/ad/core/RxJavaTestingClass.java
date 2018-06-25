/*
package edu.gla.kail.ad.core;

import com.google.protobuf.Timestamp;
import edu.gla.kail.ad.Client;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import rx.Subscriber;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkNotNull;


public class RxJavaTestingClass {
    private List<AgentInterface> _agents;

    public static int waitIf2(Object integer1) {
        int integer = Integer.valueOf(integer1.toString());
        if (integer == 2) {
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (Exception e) {
            }
        }
        return integer;
    }

    public static void main(String... names) {
        Integer[] numbers = {0, 1, 2, 3, 4, 5};
        Observable numberObservable = Observable.from(numbers);

        numberObservable.subscribe(
                (incomingNumber) -> {
                    System.out.println("incomingNumber " + incomingNumber);
                    int number = waitIf2(incomingNumber);
                    System.out.println(number);
                },
                (error) -> System.out.println("Something went wrong" + ((Throwable) error)
                        .getMessage()),
                () -> System.out.println("This observable is finished")
        );


        Observable.OnSubscribe<String> subscribeFunction = (s) -> asyncProcessingOnSubscribe(s);

        Observable asyncObservable = Observable.create(subscribeFunction);

        asyncObservable.skip(5).subscribe((incomingValue) -> System.out.println(incomingValue));

        private void asyncProcessingOnSubscribe (Subscriber s){
            final Subscriber subscriber = (Subscriber) s;
            Thread thread = new Thread(() -> produceSomeValues(subscriber));
            thread.start();
        }

        private void produceSomeValues (Subscriber subscriber){
            for (int ii = 0; ii < 10; ii++) {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext("Pushing value from async thread " + ii);
                }
            }
        }
    }

    private String getRandomID() {
        return (new java.sql.Timestamp(System.currentTimeMillis())).toString() + UUID.randomUUID
                ().toString();
    }

    public List<Log.ResponseLog> getResponsesFromAgents(Client.InteractionRequest
                                                                interactionRequest) throws
            IllegalArgumentException, Exception {
        if (checkNotNull(_agents, "Agents are not set up! Use the method" +
                " setUpAgents() first.").isEmpty()) {
            throw new IllegalArgumentException("The list of agents is empty!");
        }

        Timestamp timestamp = Timestamp.newBuilder()
                .setSeconds(Instant.now()
                        .getEpochSecond())
                .setNanos(Instant.now()
                        .getNano())
                .build();

        // Save data from InteractionRequest to RequestLog.
        Log.RequestLog requestLog = Log.RequestLog.newBuilder()
                .setRequestId(getRandomID())
                .setTime(timestamp)
                .setClientId(interactionRequest.getClientId())
                .setInteraction(interactionRequest.getInteraction()).build();

        // Store the responses from the agents in a list.
        List<Log.ResponseLog> listOfResponseLogs = new ArrayList();
        Client.InputInteraction inputInteraction = requestLog.getInteraction();
        // -----------------------------------------------------------------------------------------


        Observable<AgentInterface> agentInterfaceObservable = Observable.fromIterable(_agents);

        agentInterfaceObservable.flatMap(agentObservable -> Observable.just(agentObservable)
                .subscribeOn(Schedulers.newThread())
                .map(agent ->
                        listOfResponseLogs.add(agent.getResponseFromAgent(inputInteraction))
                ))
                .subscribe();

        Observable<Integer> vals = Observable.range(1, 10);

        vals.flatMap(val -> Observable.just(val)
                .subscribeOn(Schedulers.computation())
                .map(i -> intenseCalculation(i))
        ).toList()
                .subscribe(val -> System.out.println("Subscriber received "
                        + val + " on "
                        + Thread.currentThread().getName()));
*/
/*
        Observable<AgentInterface> agentsObservable = Observable.from(_agents);

        agentsObservable.flatMap(agent -> Observable.just(agent)
                .subscribeOn(Schedulers.newThread())
                .map(3 -> {
                    try {
                        listOfResponseLogs.add(agent.getResponseFromAgent
                                (inputInteraction));
                    } catch (Exception exception) {
                        listOfResponseLogs.add(ResponseLog.newBuilder()
                                .setMessageStatus(MessageStatus.UNSUCCESFUL)
                                .setErrorMessage(exception.getMessage())
                                .build());
                    }
                })).subscribe());

        Observable.from(_agents)
                .subscribe(
                        agent -> {
                            try {
                                listOfResponseLogs.add(agent.getResponseFromAgent
                                        (inputInteraction));
                            } catch (Exception exception) {
                                listOfResponseLogs.add(ResponseLog.newBuilder()
                                        .setMessageStatus(MessageStatus.UNSUCCESFUL)
                                        .setErrorMessage(exception.getMessage())
                                        .build());
                            }
                        },
                        // TODO(Adam): Implemnt these methods.
                        error -> System.out.println("Something went wrong" + error.getMessage()),
                        () -> System.out.println("This observable is finished"));
*//*



        return listOfResponseLogs;
    }

}
*/
