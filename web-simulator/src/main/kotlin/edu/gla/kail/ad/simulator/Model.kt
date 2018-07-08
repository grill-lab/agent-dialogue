package edu.gla.kail.ad.simulator

import com.google.protobuf.Timestamp
import edu.gla.kail.ad.Client.ClientId.WEB_SIMULATOR
import edu.gla.kail.ad.Client.InputInteraction
import edu.gla.kail.ad.Client.InputInteractionOrBuilder
import edu.gla.kail.ad.Client.InteractionRequest
import edu.gla.kail.ad.Client.InteractionRequestOrBuilder
import edu.gla.kail.ad.Client.InteractionType.TEXT
import java.time.Instant


fun getInteractionRequestFromText(textInput: String): InteractionRequest {
    return (getInteractionRequest((getInputInteractionBuilder() as InputInteraction.Builder)
            .setType(TEXT)
            .setText(textInput)
            .build()) as InteractionRequest.Builder)
            .build()
}


fun getInteractionRequest(inputInteraction: InputInteraction): InteractionRequestOrBuilder {
    return InteractionRequest.newBuilder()
            .setClientId(WEB_SIMULATOR)
            .setUserID(returnUserName())
            .setTime(Timestamp.newBuilder()
                    .setSeconds(Instant.now()
                            .getEpochSecond())
                    .setNanos(Instant.now()
                            .getNano())
                    .build())
            .setInteraction(inputInteraction);
}

private fun getInputInteractionBuilder(): InputInteractionOrBuilder {
    return InputInteraction.newBuilder()
            .setLanguageCode(conversationStateHolder._language.getValue())
            .setDeviceType(deviceType())
}

fun returnUserName(): String {
    // TODO(Adam): Implement getting username.
    return "sampleUserName-to-be-implemented"
}

fun deviceType(): String {
    // TODO(Adam): Implement getting device type.
    return "sampleDeviceType-to-be-implemented"
}