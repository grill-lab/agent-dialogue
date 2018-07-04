package edu.gla.kail.ad.simulator


// external - declaration is written in pure JavaScript
external val module: Module

external interface Module {
    val hot: Hot?
}

external interface Hot {
    val data: dynamic // dynamic - used for working with JS

    fun accept()
    fun accept(dependency: String, callback: () -> Unit)
    fun accept(dependencies: Array<String>, callback: (updated: Array<String>) -> Unit)

    fun dispose(callback: (data: dynamic) -> Unit)
}

external fun require(name: String): dynamic