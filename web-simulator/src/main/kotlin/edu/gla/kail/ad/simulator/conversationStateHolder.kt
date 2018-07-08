package edu.gla.kail.ad.simulator

import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleStringProperty
import java.util.*

object conversationStateHolder {
    var _language = SimpleStringProperty("en-US")
    var _userInput = SimpleStringProperty("")
    var _listOfMessages = SimpleListProperty<Objects>() {
        override fun add(object : Objects) {

        }

    }
}


