package edu.gla.kail.ad.simulator

import kotlin.browser.document
import kotlin.dom.hasClass

fun main(args: Array<String>) {
    if (document.body != null) {
        start()
    } else {
        document.addEventListener("DOMContentLoaded", { start() })
    }
}

fun start(): WebLinesView? {
    if (document.body?.hasClass("agentDialogue") ?: false) {
        @Suppress("UnsafeCastFromDynamic")
        WebLinesView(document.getElementById("conversation")!!, document.getElementById("requestInput")!!)
        @Suppress("UnsafeCastFromDynamic")
        return WebLinesView(document.getElementById("lines")!!, document.getElementById("addForm")!!)
    }
    return null
}

