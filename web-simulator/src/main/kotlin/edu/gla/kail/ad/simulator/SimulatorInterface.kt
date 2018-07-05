package edu.gla.kail.ad.simulator

import javafx.scene.control.Label
import javafx.scene.layout.HBox
import tornadofx.App
import tornadofx.View


class SimulatorInterface : View() {
    override val root = HBox(Label("Welcome to the Agent Dialogue Simulator"))
}

class SimulatorInterfaceApp : App() {
    override val primaryView = SimulatorInterface::class
}