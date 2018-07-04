package edu.gla.kail.ad.simulator

interface LinesView {
    var presenter: LinesPresenter
    var inputText: String
    fun addLine(lineText: String)
}