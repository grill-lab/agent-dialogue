package edu.gla.kail.ad.simulator

import org.w3c.dom.Element
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.events.Event
import org.w3c.dom.events.KeyboardEvent
import kotlin.browser.document


class WebLinesView(val linesHolder: Element, formRoot: Element) {
    var presenter: WebLinesView

    init {
        presenter = this
    }

    fun addButtonClicked() {
        this.addLine(this.inputText)

        this.inputText = "Text present in inputText field"
    }

    @Suppress("UNCHECKED_CAST_TO_NATIVE_INTERFACE")
    private val input = formRoot.querySelector("input") as HTMLInputElement

    @Suppress("UNCHECKED_CAST_TO_NATIVE_INTERFACE")
    private val addButton = formRoot.querySelector("button") as HTMLButtonElement

    private val buttonHandler: (Event) -> Unit = {
        presenter.addButtonClicked()
    }

    private val inputHandler: (Event) -> Unit = { e ->
        if (e is KeyboardEvent && e.keyCode == 13) { // If user pressed enter == 13.
            presenter.addButtonClicked() // Call method of LinesPresenter class.
        }
    }

    init {
        register()
    }

    var inputText: String
        get() = input.value
        set(newValue) {
            input.value = newValue
        }

    fun addLine(lineText: String) {
        document.createElement("p").apply {
            textContent = " + " + lineText

            linesHolder.appendChild(this)
        }
    }

    private fun register() {
        addButton.addEventListener("click", buttonHandler)
        input.addEventListener("keypress", inputHandler)
    }
}
