package no.nav.syfo.pdfgen.api.pdf

import com.fasterxml.jackson.databind.JsonNode
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.request.receive
import io.ktor.server.response.*
import io.ktor.server.routing.*
import no.nav.syfo.pdfgen.Environment
import no.nav.syfo.pdfgen.core.pdf.createHtml
import no.nav.syfo.pdfgen.core.pdf.createHtmlFromTemplateData
import no.nav.syfo.pdfgen.core.pdf.createPDFA

fun Route.registerGeneratePdfApi(env: Environment = Environment()) {
    if (!env.disablePdfGet) {
        get("/{applicationName}/{template}") {
            val template = call.parameters["template"]!!
            val applicationName = call.parameters["applicationName"]!!
            createHtmlFromTemplateData(template, applicationName)?.let { document ->
                call.response.header("Content-Type", ContentType.Application.Pdf.toString())
                call.respond(createPDFA(document, env.disablePdfAValidation))
            }
                ?: call.respondText(
                    "Template or application not found",
                    status = HttpStatusCode.NotFound,
                )
        }
    }
    post("/{applicationName}/{template}") {
        val startTime = System.currentTimeMillis()
        val template = call.parameters["template"]!!
        val applicationName = call.parameters["applicationName"]!!
        val jsonNode: JsonNode = call.receive()

        createHtml(template, applicationName, jsonNode)?.let { document ->
            call.response.header("Content-Type", ContentType.Application.Pdf.toString())
            call.respond(createPDFA(document, env.disablePdfAValidation))
            call.application.log.info(
                "Done generating PDF in ${System.currentTimeMillis() - startTime}ms",
            )
        }
            ?: call.respondText(
                "Template or application not found",
                status = HttpStatusCode.NotFound,
            )
    }
}
