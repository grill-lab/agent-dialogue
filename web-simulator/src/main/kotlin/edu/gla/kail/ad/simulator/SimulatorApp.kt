package edu.gla.kail.ad.simulator

import tornadofx.App
import tornadofx.launch

/**
 * This class in the entry point to the Simulator Application.
 * SimulatorView is the primary view of the Simulator App
 */
class SimulatorApp : App(SimulatorView::class)


fun main(args: Array<String>) {
    launch<SimulatorApp>(args)
}