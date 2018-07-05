package edu.gla.kail.ad.simulator

import kotlin.browser.document

class MainApplication {
    private lateinit var view: WebLinesView

    fun start() {
        view = WebLinesView(document.getElementById("lines")!!, document.getElementById("addForm")!!)
    }
}