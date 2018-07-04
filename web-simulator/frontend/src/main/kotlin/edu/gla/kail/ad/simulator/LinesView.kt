package edu.gla.kail.ad.simulator

interface LinesView {
    var presenter: LinesPresenter
    var inputText: String
    fun focusInput()
    fun addLine(lineText: String)
    fun clearLines()
    fun dispose()
}