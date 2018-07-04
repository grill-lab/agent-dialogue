package edu.gla.kail.ad.simulator

interface Presenter<out ViewType : Any, StateType: Any> {
    val view: ViewType

    fun dispose(): StateType
    fun restore(state: StateType)
}