package com.mikebway.ktor.handlers

import kotlinx.html.body
import kotlinx.html.h1
import kotlinx.html.head
import kotlinx.html.p
import kotlinx.html.title
import org.jetbrains.ktor.application.ApplicationCall
import org.jetbrains.ktor.application.call
import org.jetbrains.ktor.html.respondHtml
import org.jetbrains.ktor.pipeline.PipelineInterceptor

/**
 * Implements the home page DSL.
 */
val home: PipelineInterceptor<ApplicationCall> = {
    call.respondHtml {
        head {
            title { +"HTML Application" }
        }
        body {
            h1 { +"Sample application with HTML builders" }
            p { +"Ktor shows promise as a lightweight but sophisticated Web framework." }
        }
    }
}