package edu.gla.kail.ad.simulator

import com.google.protobuf.Timestamp
import edu.gla.kail.ad.Client
import edu.gla.kail.ad.Client.ClientId.WEB_SIMULATOR
import edu.gla.kail.ad.Client.InputInteraction
import edu.gla.kail.ad.Client.InteractionRequest
import edu.gla.kail.ad.Client.InteractionResponse
import edu.gla.kail.ad.Client.InteractionType
import edu.gla.kail.ad.service.AgentDialogueClientService
import javafx.application.Application
import javafx.scene.web.WebView
import tornadofx.App
import tornadofx.Stylesheet.Companion.line
import tornadofx.View
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

class SimulatorInterfaceApp : App() {
    override val primaryView = SimulatorInterfaceView::class
}


fun main(args: Array<String>) = Application.launch(SimulatorInterfaceApp::class.java)