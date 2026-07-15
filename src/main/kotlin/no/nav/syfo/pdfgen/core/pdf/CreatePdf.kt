package no.nav.syfo.pdfgen.core.pdf

import com.fasterxml.jackson.databind.JsonNode
import com.openhtmltopdf.pdfboxout.PDFontSupplier
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder
import com.openhtmltopdf.svgsupport.BatikSVGDrawer
import no.nav.syfo.pdfgen.core.PDFGenCore
import org.apache.fontbox.ttf.TTFParser
import org.apache.pdfbox.io.RandomAccessReadBuffer
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.font.PDType0Font
import org.slf4j.LoggerFactory
import org.verapdf.pdfa.Foundries
import org.verapdf.pdfa.flavours.PDFAFlavour
import org.verapdf.pdfa.results.TestAssertion
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

private val log = LoggerFactory.getLogger("no.nav.syfo.pdfgen.core.pdf.CreatePdf")

fun createPDFA(
    template: String,
    directoryName: String,
    jsonPayload: JsonNode? = null,
): ByteArray? {
    val html =
        jsonPayload?.let { createHtml(template, directoryName, it) }
            ?: createHtmlFromTemplateData(template, directoryName)
    return html?.let { createPDFA(it) }
}

fun createPDFA(html: String): ByteArray {
    val pdf =
        ByteArrayOutputStream()
            .apply {
                PdfRendererBuilder()
                    .apply {
                        for (font in PDFGenCore.environment.fonts) {
                            val ttf =
                                TTFParser()
                                    .parse(
                                        RandomAccessReadBuffer(
                                            PDFGenCore.environment.fontBytesByPath.getValue(font.path),
                                        ),
                                    ).also { it.isEnableGsub = false }
                            useFont(
                                PDFontSupplier(PDType0Font.load(PDDocument(), ttf, font.subset)),
                                font.family,
                                font.weight,
                                font.style,
                                font.subset,
                            )
                        }
                    }.usePdfAConformance(PdfRendererBuilder.PdfAConformance.PDFA_2_A)
                    .usePdfUaAccessibility(true)
                    .useColorProfile(PDFGenCore.environment.colorProfile)
                    .useSVGDrawer(BatikSVGDrawer())
                    .withHtmlContent(html, null)
                    .toStream(this)
                    .run()
            }.toByteArray()
    require(verifyCompliance(pdf)) { "Non-compliant PDF/A :(" }
    return pdf
}

private fun verifyCompliance(
    input: ByteArray,
    flavour: PDFAFlavour = PDFAFlavour.PDFA_2_A,
): Boolean {
    val pdf = ByteArrayInputStream(input)
    val validator = Foundries.defaultInstance().createValidator(flavour, false)
    val result = Foundries.defaultInstance().createParser(pdf).use { validator.validate(it) }
    val failures = result.testAssertions.filter { it.status != TestAssertion.Status.PASSED }
    failures.forEach { test ->
        log.warn(test.message)
        log.warn("Location {} {}", test.location.context, test.location.level)
        log.warn("Status {}", test.status)
        log.warn("Test number {}", test.ruleId.testNumber)
    }
    return failures.isEmpty()
}
