package no.nav.syfo.pdfgen.api.endpoints

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.response.respondText
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import no.nav.syfo.pdfgen.application.ApplicationState

const val POD_LIVENESS_PATH = "/internal/is_alive"
const val POD_READINESS_PATH = "/internal/is_ready"

fun Routing.registerPodApi(applicationState: ApplicationState) {
    get(POD_LIVENESS_PATH) {
        if (applicationState.alive) {
            call.respondText("I'm alive")
        } else {
            call.respondText("I'm dead x_x", status = HttpStatusCode.InternalServerError)
        }
    }
    get(POD_READINESS_PATH) {
        if (applicationState.ready) {
            call.respondText("I'm ready")
        } else {
            call.respondText(
                "Please wait! I'm not ready :(",
                status = HttpStatusCode.ServiceUnavailable,
            )
        }
    }
}
