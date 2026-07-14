package no.nav.syfo.pdfgen.api

import com.github.jknack.handlebars.Template
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.serialization.jackson.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import no.nav.syfo.pdfgen.Environment
import no.nav.syfo.pdfgen.api.endpoints.registerMetricApi
import no.nav.syfo.pdfgen.api.endpoints.registerPodApi
import no.nav.syfo.pdfgen.api.pdf.registerGeneratePdfApi
import no.nav.syfo.pdfgen.application.ApplicationState

fun Application.apiModule(
    environment: Environment,
    applicationState: ApplicationState,
    templates: Map<Pair<String, String>, Template>,
) {
    installContentNegotiation()
    installStatusPages(templates)

    routing {
        registerPodApi(applicationState)
        registerMetricApi()
        route("/api/v1/genpdf") {
            registerGeneratePdfApi(environment)
        }
    }
}

fun Application.installContentNegotiation() {
    install(ContentNegotiation) {
        jackson {}
    }
}

fun Application.installStatusPages(templates: Map<Pair<String, String>, Template>) {
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.application.log.error("Caught exception", cause)
            call.respond(HttpStatusCode.InternalServerError, cause.message ?: "Unknown error")
        }
        status(HttpStatusCode.NotFound) { call, _ ->
            call.respond(
                TextContent(
                    messageFor404(templates, call.request.path()),
                    ContentType.Text.Plain.withCharset(Charsets.UTF_8),
                    HttpStatusCode.NotFound,
                ),
            )
        }
    }
}

private fun messageFor404(
    templates: Map<Pair<String, String>, Template>,
    path: String,
): String =
    "Unkown path '$path'. Known templates:\n" +
        templates
            .map { (app, _) -> "/api/v1/genpdf/%s/%s".format(app.first, app.second) }
            .joinToString("\n")
