package edu.gla.kail.ad.simulator

import kotlin.browser.document
import kotlin.dom.hasClass


fun main(args: Array<String>) {
    var application: ApplicationBase? = null

    val state: dynamic = module.hot?.let { hot ->
        hot.accept() // get the data from
        hot.data
    }

    if (document.body != null) {
        application = start(state)
    } else {
        application = null
        document.addEventListener("DOMContentLoaded", { application = start(state) })
    }
}

fun start(state: dynamic): ApplicationBase? {
    if (document.body?.hasClass("testApp") ?: false) {
        val application = MainApplication()

        @Suppress("UnsafeCastFromDynamic")
        application.start(state?.appState ?: emptyMap())

        return application
    } else {
        return null
    }
}

