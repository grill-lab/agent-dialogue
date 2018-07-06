package edu.gla.kail.ad.simulator

import com.google.protobuf.Timestamp
import edu.gla.kail.ad.Client.InputInteractionOrBuilder
import edu.gla.kail.ad.Client.InteractionRequest
import edu.gla.kail.ad.Client.InteractionRequest.InteractionStatus.IN_PROGRESS
import edu.gla.kail.ad.Client.InteractionRequestOrBuilder
import java.time.Instant

fun getInteractionRequestBuilder() : InteractionRequestOrBuilder? {
    var interactionRequestOrBuilder = InteractionRequest.newBuilder()
            .setInteractionStatus(IN_PROGRESS)
            .setTime(Timestamp.newBuilder()
                    .setSeconds(Instant.now()
                            .getEpochSecond())
                    .setNanos(Instant.now()
                            .getNano())
                    .build())
    return null;
}

fun getInputInteractionBuilder() : InputInteractionOrBuilder {
    var inputInteractionBuilder = InputInteraction.newBuilder()
            .setLanguage
}