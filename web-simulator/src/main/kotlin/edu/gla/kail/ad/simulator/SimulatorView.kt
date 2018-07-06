package edu.gla.kail.ad.simulator

import javafx.scene.web.WebView
import tornadofx.View
import tornadofx.label


/**
 * Singleton class
 * Contains a hierarchy of Nodes
 */
class SimulatorView : View() {
    override val root = WebView()
}


class ChatView : View() {
    override val root = label("Chat View")
}


class InputView :View() {
    override val root = label("Input View")
}


class OptionsView :View() {
    override val root = label("Options View")
}