package edu.gla.kail.ad.simulator

import edu.gla.kail.ad.service.AgentDialogueClientService
import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.bind


/**
 * A Singleton Class responsible for holding the setting of the conversation
 * as well as the conversation itself for the ongoing session.
 */
object conversationStateHolder {
    // The language of the request.
    var _language = SimpleStringProperty("en-US")
    // The User text input.
    var _userTextInput = SimpleStringProperty("")
    // The instance of Client Service used to comunicate with the Agent-dialogue-core server.
    val _client: AgentDialogueClientService = AgentDialogueClientService("localhost", 8080)
    // List of messages sent and requests bound to these messages.
    // A pending message has String "..."
    var _listOfMessages = SimpleListProperty<Pair<String, Any>>()
}


