package edu.gla.kail.ad.server;

import com.google.protobuf.Timestamp;
import com.google.protobuf.util.JsonFormat;
import edu.gla.kail.ad.core.Client.InputInteraction;
import edu.gla.kail.ad.core.Client.InteractionRequest;
import edu.gla.kail.ad.core.Client.InteractionResponse;
import edu.gla.kail.ad.core.Client.InteractionType;

import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;

/**
 * This class is not working.
 */
public class TestClass {


    public static void main(String[] args) throws Exception {
        URL url = new URL("https://localhost:8080/test");
        HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
        urlc.setDoInput(true);
        urlc.setDoOutput(true);
        urlc.setRequestMethod("POST");
        urlc.setRequestProperty("Accept", "application/x-protobuf");
        urlc.setRequestProperty("Content-Type", "application/x-protobuf");

        InteractionRequest interactionRequest = InteractionRequest.newBuilder()
                .setClientId("Random Client ID")
                .setTime(Timestamp.newBuilder()
                        .setSeconds(Instant.now()
                                .getEpochSecond())
                        .setNanos(Instant.now()
                                .getNano())
                        .build())
                .setInteraction(InputInteraction.newBuilder()
                        .setType(InteractionType.TEXT)
                        .setText("Sample text")
                        .setDeviceType("Iphone whatever")
                        .setLanguageCode("en-US")
                        .build())
                .build();
        System.out.println(JsonFormat.printer().print(interactionRequest));
        interactionRequest.writeTo(urlc.getOutputStream());
        InteractionResponse interactionResponse = InteractionResponse.newBuilder().mergeFrom(urlc.getInputStream()).build();
        System.out.println(interactionResponse.toString());
    }
}
