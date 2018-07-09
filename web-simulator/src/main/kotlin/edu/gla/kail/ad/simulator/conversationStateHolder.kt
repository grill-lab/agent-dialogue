package edu.gla.kail.ad.simulator

import edu.gla.kail.ad.service.AgentDialogueClientService
import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.collections.FXCollections.observableArrayList
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
    // List of messages in the proto format.
    // A pending message is equal to: "...".
    var _listOfProtoMessages = observableArrayList<Any>()
    // List of messages typed and displayed to the user.
    var _listOfInterfaceMessages = observableArrayList<String>()
    
}


