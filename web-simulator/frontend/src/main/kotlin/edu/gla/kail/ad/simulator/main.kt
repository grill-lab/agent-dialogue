package edu.gla.kail.ad.simulator

import kotlin.browser.document
import kotlin.dom.hasClass

fun main(args: Array<String>) {
    
    var application: MainApplication?

    if (document.body != null) {
        application = start()
    } else {
        application = null
        document.addEventListener("DOMContentLoaded", { application = start() })
    }
}

fun start(): MainApplication? {
    if (document.body?.hasClass("testApp") ?: false) {
        val application = MainApplication()

        @Suppress("UnsafeCastFromDynamic")
        application.start()

        return application
    } else {
        return null
    }
}

