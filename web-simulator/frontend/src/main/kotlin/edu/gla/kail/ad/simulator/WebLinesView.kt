package edu.gla.kail.ad.simulator

import edu.gla.kail.ad.service.AgentDialogueClientService
import org.w3c.dom.Element
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.events.Event
import org.w3c.dom.events.KeyboardEvent
import kotlin.browser.document


class WebLinesView(val linesHolder: Element, formRoot: Element) {
    var presenter: WebLinesView = this
    var client: AgentDialogueClientService = AgentDialogueClientService("localhost", 8080)

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
        addButton.addEventListener("click", buttonHandler)
        input.addEventListener("keypress", inputHandler)
    }

    var inputText: String
        get() = input.value
        set(newValue) {
            input.value = newValue
        }

    fun addButtonClicked() {
        var inputTextString = this.inputText
        document.createElement("p").apply {
            textContent = " + " + inputTextString
            linesHolder.appendChild(this)
        }

        this.inputText = "Text present in inputText field"
    }



    fun callTheAgent(textInput: String) {
        var response: String = client.getStringResponse(textInput)


    }
}
