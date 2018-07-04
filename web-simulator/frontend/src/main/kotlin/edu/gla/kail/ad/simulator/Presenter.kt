package edu.gla.kail.ad.simulator

interface Presenter<out ViewType : Any> {
    val view: ViewType
}