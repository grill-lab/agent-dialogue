package edu.gla.kail.ad.simulator

class LinesPresenter(override val view: LinesView) : Presenter<LinesView> {
    init {
        view.presenter = this
    }

    private val lines = mutableListOf<String>() // Stores lines with user input.

    fun addButtonClicked() {
        val lineText = view.inputText

        lines.add(lineText)
        view.addLine(lineText)

        view.inputText = "Text present in inputText field"
    }

}