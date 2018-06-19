package kotlin.edu.gla.kail.ad.client

import io.ktor.server.jetty;


class TestingClass {
    val client = HttpClient(Jetty)
    val htmlContent = client.get<String>("https://en.wikipedia.org/wiki/Main_Page")
}