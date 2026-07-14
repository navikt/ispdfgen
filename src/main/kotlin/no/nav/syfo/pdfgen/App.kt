package no.nav.syfo.pdfgen

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.Netty
import no.nav.syfo.pdfgen.api.apiModule
import no.nav.syfo.pdfgen.application.ApplicationState
import no.nav.syfo.pdfgen.core.PDFGenCore
import org.slf4j.LoggerFactory

fun main(args: Array<String>) {
    val logger = LoggerFactory.getLogger("ktor.application")
    val applicationState = ApplicationState()
    val environment = Environment()
    PDFGenCore.init()

    val embeddedServer =
        embeddedServer(
            factory = Netty,
            configure = {
                connectionGroupSize = 8
                workerGroupSize = 8
                callGroupSize = 16
                responseWriteTimeoutSeconds = 70
                connector {
                    port = 8080
                }
            },
            module = {
                apiModule(
                    environment = environment,
                    applicationState = applicationState,
                    templates = PDFGenCore.environment.templates,
                )
                monitor.subscribe(ApplicationStarted) {
                    applicationState.ready = true
                    logger.info("Application is ready, running Java VM ${Runtime.version()}")
                }
                monitor.subscribe(ApplicationStopPreparing) {
                    applicationState.ready = false
                }
            },
        )
    embeddedServer.start(wait = true)
}
