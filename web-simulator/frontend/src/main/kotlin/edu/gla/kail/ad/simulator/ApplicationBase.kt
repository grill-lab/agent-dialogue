package edu.gla.kail.ad.simulator

abstract class ApplicationBase {
    abstract val stateKeys: List<String>

    abstract fun start(state: Map<String, Any>)
}