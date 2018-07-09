package edu.gla.kail.ad.simulator

import com.google.protobuf.Timestamp
import edu.gla.kail.ad.Client.ClientId.WEB_SIMULATOR
import edu.gla.kail.ad.Client.InputInteraction
import edu.gla.kail.ad.Client.InputInteractionOrBuilder
import edu.gla.kail.ad.Client.InteractionRequest
import edu.gla.kail.ad.Client.InteractionRequestOrBuilder
import edu.gla.kail.ad.Client.InteractionResponse
import edu.gla.kail.ad.Client.InteractionResponse.ClientMessageStatus
import edu.gla.kail.ad.Client.InteractionType
import edu.gla.kail.ad.Client.OutputInteraction
import java.time.Instant


fun getResponseFromTextInput(textInput: String) {
    var interactionRequest = getInteractionRequestFromText(textInput)
    conversationStateHolder._listOfMessages.add(Pair(textInput, interactionRequest))
    
    var responsePair = Pair("...", Any())
    conversationStateHolder._listOfMessages.add(responsePair)
    
    var interactionResponse = conversationStateHolder._client
            .getInteractionResponse(getInteractionRequestFromText(textInput))
    responsePair = handleResponse(interactionResponse) // TODO(Adam): Check if the responsePair
    // in the list is changed when the response pair is updated here!
}

fun handleResponse(interactionResponse: InteractionResponse): Pair<String, Any> {
    when (interactionResponse.messageStatus) {
        ClientMessageStatus.ERROR -> Pair(interactionResponse.errorMessage, interactionResponse)
        ClientMessageStatus.SUCCESSFUL -> {
            var outputInteractionList = interactionResponse.interactionList
            for (outputInteraction in outputInteractionList) {
            
            }
        }
        else -> Pair("There was an error, contact the developer: " + interactionResponse.toString(),
                interactionResponse)
    }
}

fun handleOutputInteraction(outputInteraction: OutputInteraction) {
    when (outputInteraction.type) {
        InteractionType.TEXT -> sda;
        // TODO(Adam): Implement handling audio and action output.
    }
    
}

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