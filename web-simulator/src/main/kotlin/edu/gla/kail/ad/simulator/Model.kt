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

/**
 * Get response from agents and update conversationStateHolder when the textInput is passed from the user.
 */
fun getResponseFromTextInput(textInput: String) {
    var interactionRequest = getInteractionRequestFromText(textInput)
    conversationStateHolder._listOfMessages.add(Pair(textInput, interactionRequest))
    val pendingPair = Pair("...", Any())
    conversationStateHolder._listOfMessages.add(pendingPair)
    var interactionResponse = conversationStateHolder._client
            .getInteractionResponse(getInteractionRequestFromText(textInput))
    handleResponse(interactionResponse)
    conversationStateHolder._listOfMessages.remove(pendingPair) // TODO(Adam): check if this works properly.
}

/**
 * Handle response received from the ClientService.
 * Update parameters in conversationStateHolder by calling handleOutputInteraction method.
 */
fun handleResponse(interactionResponse: InteractionResponse) {
    when (interactionResponse.messageStatus) {
        ClientMessageStatus.ERROR -> {
            conversationStateHolder._listOfMessages.add(Pair(interactionResponse.errorMessage, interactionResponse))
        }
        ClientMessageStatus.SUCCESSFUL -> {
            var outputInteractionList = interactionResponse.interactionList
            for (outputInteraction in outputInteractionList) {
                handleOutputInteraction(outputInteraction, interactionResponse)
            }
        }
        else -> {
            conversationStateHolder._listOfMessages.add(Pair("There was an error, contact the developer: " + interactionResponse.toString(), interactionResponse))
        }
    }
}

/**
 * Handle single outputInteraction (passed from outputInteractionList obtained from InteractionRequest).
 */
fun handleOutputInteraction(outputInteraction: OutputInteraction, interactionResponse: InteractionResponse) {
    when (outputInteraction.type) {
        InteractionType.TEXT -> {
            conversationStateHolder._listOfMessages.add(Pair(outputInteraction.text, interactionResponse))
        }
    // TODO(Adam): Implement handling audio and action output.
        InteractionType.ACTION -> {
            throw Exception("Handling actions not available yet!")
        }
        InteractionType.AUDIO -> {
            throw Exception("Handling audio not available yet!")
        }
        else -> {
            throw Exception("There was an error! Not recognised OutputInteraction Type!")
        }
    }
}

/**
 * Helper method: create InteractionRequests from text Input.
 */
fun getInteractionRequestFromText(textInput: String): InteractionRequest {
    return (getInteractionRequestBuilder((getInputInteractionBuilder() as InputInteraction.Builder)
            .setType(InteractionType.TEXT)
            .setText(textInput)
            .build()) as InteractionRequest.Builder)
            .build()
}

/**
 * Helper method: create InteractionRequestBuilders.
 */
fun getInteractionRequestBuilder(inputInteraction: InputInteraction): InteractionRequestOrBuilder {
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

/**
 * Helper method: create InputInteraction with parameters chosen in OptionsView.
 */
private fun getInputInteractionBuilder(): InputInteractionOrBuilder {
    return InputInteraction.newBuilder()
            .setLanguageCode(conversationStateHolder._language.getValue())
            .setDeviceType(deviceType())
}

/**
 * Return the current user name.
 * TODO(Adam): Implement getting username from database or having a username object!
 */
fun returnUserName(): String {
    return "sampleUserName-to-be-implemented"
}

/**
 * Return the device type.
 * TODO(Adam): Implement getting device type.
 */
fun deviceType(): String {
    return "sampleDeviceType-to-be-implemented"
}