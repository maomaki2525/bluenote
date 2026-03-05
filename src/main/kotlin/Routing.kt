package com.example

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {

        get("/") {
            val html = this::class.java.classLoader.getResource("web/threads.html")!!.readText()
            call.respondText(html, ContentType.Text.Html)
        }

        get("/style.css") {
            val css = this::class.java.classLoader.getResource("web/style.css")!!.readText()
            call.respondText(css, ContentType.Text.CSS)
        }

        get("/thread") {
            val html = this::class.java.classLoader.getResource("web/thread.html")!!.readText()
            call.respondText(html, ContentType.Text.Html)
        }

    }
}