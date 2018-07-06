package edu.gla.kail.ad.simulator

import edu.gla.kail.ad.Client.ClientTurn
import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleStringProperty
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
import tornadofx.getToggleGroup
import tornadofx.getValue
import tornadofx.gridpane
import tornadofx.label
import tornadofx.radiobutton
import tornadofx.textarea
import tornadofx.vbox
import tornadofx.vgrow


/**
 * Singleton class
 * Contains a hierarchy of Nodes
 */
class SimulatorView : View() {
    val clientTurns = SimpleListProperty<ClientTurn>()
    override val root = gridpane {
        isGridLinesVisible = false

        addRow(0, TopView().root)
        addRow(2, ChatView().root)
        addRow(4, InputView().root)

        addColumn(1, OptionsView().root)
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


    /*gridpane() {
        rowConstraints.add(0, RowConstraints(100.toDouble()))
        children.add(0, TopView().root)
    }*/


}


class TopView : View() {
    override val root = vbox()
}


class ChatView : View() {
    override val root = vbox {
        label("Chat with agents.")
    }
}


class InputView : View() {
    private val _inputBoxRowNumber: Double = 5.toDouble()
    var userInput = SimpleStringProperty("")

    override val root = vbox {
        form {
            fieldset("Input field", labelPosition = Orientation.HORIZONTAL) {
                field("Chat with agents") {

                    textarea {
                        prefRowCount = _inputBoxRowNumber.toInt()
                        bind(userInput)
                    }

                    button("Send") {
                        prefHeight = _inputBoxRowNumber * 20.2
                        prefWidth = 55.toDouble()
                        minWidth = 55.toDouble()
                        action {
                            runAsync {
                                System.out.println(userInput.getValue())
                                System.out.println(OptionsView().language.getValue())
                                // call the function you want to use
                                userInput = SimpleStringProperty("")
                            } ui {
                                // apply the result when the process is do e
//                                loadedText -> dwadaw.text = loadedText
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


class OptionsView : View() {
    var language = SimpleStringProperty("en-US")
    private val toggleGroup = ToggleGroup()
    override val root = vbox {
        vbox {
            label("Language")
            radiobutton("English - US", toggleGroup, "en-US") {
                bind(language)
            }
            radiobutton("English - GB", toggleGroup, "en-GB")
            {
                bind(language)
            }
        }
    }

}



