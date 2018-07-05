package edu.gla.kail.ad.simulator

import kotlin.browser.document
import kotlin.dom.hasClass

fun main(args: Array<String>) {
    var view: WebLinesView?

    if (document.body != null) {
        view = start()
    } else {
        view = null
        document.addEventListener("DOMContentLoaded", { view = start() })
    }
}

fun start(): WebLinesView? {
    if (document.body?.hasClass("agentDialogue") ?: false) {
        @Suppress("UnsafeCastFromDynamic")
        return WebLinesView(document.getElementById("lines")!!, document.getElementById("addForm")!!)
    } else {
        return null
    }
}

