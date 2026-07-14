package no.nav.syfo.pdfgen.core.pdf

import com.fasterxml.jackson.databind.JsonNode
import com.github.jknack.handlebars.Context
import com.github.jknack.handlebars.JsonNodeValueResolver
import com.github.jknack.handlebars.context.MapValueResolver
import no.nav.syfo.pdfgen.core.PDFGenCore
import no.nav.syfo.pdfgen.core.metrics.HANDLEBARS_RENDERING_SUMMARY
import no.nav.syfo.pdfgen.core.objectMapper
import java.nio.file.Files

fun createHtml(
    template: String,
    directoryName: String,
    jsonPayload: JsonNode,
): String? = render(directoryName, template, jsonPayload)

fun createHtmlFromTemplateData(
    template: String,
    directoryName: String,
): String? {
    val jsonNode = hotTemplateData(directoryName, template)
    return render(directoryName, template, jsonNode)
}

fun render(
    directoryName: String,
    template: String,
    jsonNode: JsonNode,
): String? =
    HANDLEBARS_RENDERING_SUMMARY
        .startTimer()
        .use {
            PDFGenCore.environment.templates[directoryName to template]?.apply(
                Context
                    .newBuilder(jsonNode)
                    .resolver(
                        JsonNodeValueResolver.INSTANCE,
                        MapValueResolver.INSTANCE,
                    ).build(),
            )
        }

private fun hotTemplateData(
    applicationName: String,
    template: String,
): JsonNode {
    val dataFile = PDFGenCore.environment.dataRoot.getPath("$applicationName/$template.json")
    val data =
        objectMapper.readValue(
            if (Files.exists(dataFile)) {
                Files.readAllBytes(dataFile)
            } else {
                "{}".toByteArray(Charsets.UTF_8)
            },
            JsonNode::class.java,
        )
    return data
}
