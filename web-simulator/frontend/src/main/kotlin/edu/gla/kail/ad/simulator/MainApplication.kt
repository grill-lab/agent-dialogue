package edu.gla.kail.ad.simulator

import kotlin.browser.document

class MainApplication {
    private lateinit var view: WebLinesView
    private lateinit var presenter: LinesPresenter

    val stateKeys = listOf("lines")

    fun start(state: Map<String, Any>) {
        view = WebLinesView(document.getElementById("lines")!!, document.getElementById("addForm")!!)
        presenter = LinesPresenter(view)
    }
}