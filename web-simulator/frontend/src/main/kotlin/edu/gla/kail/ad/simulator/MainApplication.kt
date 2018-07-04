package edu.gla.kail.ad.simulator

import kotlin.browser.document

class MainApplication : ApplicationBase() {
    private lateinit var view: WebLinesView
    private lateinit var presenter: LinesPresenter

    override val stateKeys = listOf("lines")

    override fun start(state: Map<String, Any>) {
        view = WebLinesView(document.getElementById("lines")!!, document.getElementById("addForm")!!)
        presenter = LinesPresenter(view)

        state["lines"]?.let { linesState ->
            @Suppress("UNCHECKED_CAST")
            presenter.restore(linesState as Array<String>)
        }
    }

    override fun dispose() = mapOf(
            "lines" to presenter.dispose()
    )
}