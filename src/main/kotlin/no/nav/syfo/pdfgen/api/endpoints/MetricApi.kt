package no.nav.syfo.pdfgen.api.endpoints

import io.ktor.server.application.call
import io.ktor.server.response.respondText
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import no.nav.syfo.pdfgen.metrics.METRICS_REGISTRY

const val METRICS_PATH = "/internal/metrics"

fun Routing.registerMetricApi() {
    get(METRICS_PATH) {
        call.respondText(METRICS_REGISTRY.scrape())
    }
}
