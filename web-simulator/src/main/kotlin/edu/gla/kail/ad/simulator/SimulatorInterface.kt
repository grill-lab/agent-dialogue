package edu.gla.kail.ad.simulator

import com.google.protobuf.Timestamp
import edu.gla.kail.ad.Client.ClientId.WEB_SIMULATOR
import edu.gla.kail.ad.Client.InputInteraction
import edu.gla.kail.ad.Client.InteractionRequest
import edu.gla.kail.ad.Client.InteractionResponse
import edu.gla.kail.ad.Client.InteractionType
import edu.gla.kail.ad.service.AgentDialogueClientService
import javafx.application.Application
import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import javafx.scene.web.WebView
import tornadofx.App
import tornadofx.Stylesheet
import tornadofx.View
import tornadofx.box
import tornadofx.c
import tornadofx.cssclass
import tornadofx.mixin
import tornadofx.px
import java.time.Instant


class SimulatorInterfaceView : View() {
    companion object {
        fun resourceLinker(path: String) = "${SimulatorInterfaceView::class.java.getResource(path)}"
    }

    val client: AgentDialogueClientService = AgentDialogueClientService("localhost", 8080)
    override val root = WebView()


    init {
        with(root) {
            setPrefSize(800.0, 600.0)
            // Set the title of the window as the HTML document title.
            titleProperty.bind(engine.titleProperty())
            var responses: ArrayList<InteractionResponse> = ArrayList<InteractionResponse>()
            val interactionRequest = InteractionRequest.newBuilder()
                    .setTime(Timestamp.newBuilder()
                            .setSeconds(Instant.now()
                                    .epochSecond)
                            .setNanos(Instant.now()
                                    .nano)
                            .build())
                    .setClientId(WEB_SIMULATOR)
                    .setInteraction(InputInteraction.newBuilder()
                            .setType(InteractionType.TEXT)
                            .setText("Hi!")
                            .setDeviceType("iPhone Google Assistant Kotlin")
                            .setLanguageCode("en-US"))
                    .build()
            responses.add(client.getInteractionResponse(interactionRequest))
            engine.loadContent(interactionResponseView(responses))
        }
    }
}

class SimulatorInterfaceApp : App(SimulatorInterfaceView::class, Styles::class)

class Styles : Stylesheet() {
    companion object {
        // Define css classes
        val heading by cssclass()

        // Define colors
        val mainColor = c("#bdbd22")
    }

    init {
        heading {
            textFill = mainColor
            fontSize = 30.px
            fontWeight = FontWeight.BOLD
        }

        button {
            padding = box(10.px, 20.px)
            fontWeight = FontWeight.BOLD
        }

        val flat = mixin {
            backgroundInsets += box(0.px)
            borderColor += box(Color.ORANGE)
        }

        s(button, textInput) {
            +flat
        }
    }
}

fun main(args: Array<String>) = Application.launch(SimulatorInterfaceApp::class.java)