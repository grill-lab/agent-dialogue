package edu.gla.kail.ad.simulator

import javafx.geometry.Orientation
import javafx.scene.control.ToggleGroup
import javafx.scene.layout.Priority
import tornadofx.View
import tornadofx.action
import tornadofx.bind
import tornadofx.button
import tornadofx.constraintsForColumn
import tornadofx.constraintsForRow
import tornadofx.field
import tornadofx.fieldset
import tornadofx.form
import tornadofx.gridpane
import tornadofx.label
import tornadofx.listview
import tornadofx.radiobutton
import tornadofx.textarea
import tornadofx.vbox
import tornadofx.vgrow


/**
 * Singleton class
 * Contains a hierarchy of Nodes
 */
class SimulatorView : View() {
    var _topView = TopView()
    var _chatView = ChatView()
    var _inputView = TextInputView()
    var _optionsView = OptionsView()
    override val root = gridpane {
        isGridLinesVisible = false
        
        addRow(0, _topView.root)
        addRow(2, _chatView.root)
        addRow(4, _inputView.root)
        
        addColumn(1, _optionsView.root)
        addRow(6)
        constraintsForRow(0).percentHeight = 3.0
        constraintsForRow(1).percentHeight = 5.0
        constraintsForRow(2).percentHeight = 60.0
        constraintsForRow(3).percentHeight = 3.0
        constraintsForRow(4).percentHeight = 25.0
        constraintsForColumn(0).percentWidth = 75.0
        constraintsForRow(6).percentHeight = 25.0
        constraintsForColumn(1).percentWidth = 25.0
    }
}


class TopView : View() {
    override val root = vbox()
}


class ChatView : View() {
    override val root = listview<String> {
        label("Chat with agents.")
        // TODO(Adam): Implement this view!
        items = conversationStateHolder._listOfInterfaceMessages
    }
}

/**
 * View holding text input box.
 */
class TextInputView : View() {
    private val _inputBoxRowNumber: Double = 5.toDouble()
    
    override val root = vbox {
        form {
            fieldset("Input field", labelPosition = Orientation.HORIZONTAL) {
                field("Chat with agents") {
                    textarea {
                        prefRowCount = _inputBoxRowNumber.toInt()
                        bind(conversationStateHolder._userTextInput)
                    }
                    
                    button("Send") {
                        prefHeight = _inputBoxRowNumber * 20.2
                        prefWidth = 55.toDouble()
                        minWidth = 55.toDouble()
                        action {
                            runAsync {
                                // TODO(Adam): Call the function you want to use.
                                getResponseFromTextInput(conversationStateHolder._userTextInput.get())
                            } ui {
                                // TODO(Adam): Apply the result when the process is do e.
                                // loadedText -> dwadaw.text = loadedText
                            }
                        }
                        vgrow = Priority.ALWAYS
                    }
                    vgrow = Priority.ALWAYS
                }
            }
        }
    }
}

/**
 * View holding available options, such as language, etc.
 */
class OptionsView : View() {
    private val toggleGroup = ToggleGroup()
    override val root = vbox {
        vbox {
            label("Language")
            radiobutton("English - US", toggleGroup) {
                action {
                    conversationStateHolder._language.set("en-US")
                }
                isSelected = true
            }
            radiobutton("English - GB", toggleGroup)
            {
                action {
                    conversationStateHolder._language.set("en-GB")
                }
            }
        }
    }
    
}



