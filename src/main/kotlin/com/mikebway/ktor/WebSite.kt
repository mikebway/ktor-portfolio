package com.mikebway.ktor

import com.mikebway.ktor.handlers.home
import org.jetbrains.ktor.application.Application
import org.jetbrains.ktor.application.install
import org.jetbrains.ktor.features.DefaultHeaders
import org.jetbrains.ktor.logging.CallLogging
import org.jetbrains.ktor.routing.Routing
import org.jetbrains.ktor.routing.get

/**
 * Ktor application extension invoked by ${org.jetbrains.ktor.netty.DevelopmentHost}
 * at start up.
 */
fun Application.main() {
    install(DefaultHeaders)
    install(CallLogging)
    install(Routing) {
        get("/", home)
    }
}