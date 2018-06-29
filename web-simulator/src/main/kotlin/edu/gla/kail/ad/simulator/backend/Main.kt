//Main.kt
// only for testing!
import com.google.gson.Gson
import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.response.header
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

fun main(args: Array<String>) {
    embeddedServer(Netty, 8080) {
        routing {
            get("/api/ping/{count?}") {
                var count: Int = Integer.valueOf(call.parameters["count"]?: "1")
                if (count < 1) {
                    count = 1
                }
                var obj = Array<Entry>(count, {i -> Entry("$i: Hello, World!")})
                val gson = Gson()
                var str = gson.toJson(obj)
                call.response.header("Access-Control-Allow-Origin", "*")
                call.respondText(str, ContentType.Application.Json)

            }
        }
    }.start(wait = true)
}

data class Entry(val message: String)