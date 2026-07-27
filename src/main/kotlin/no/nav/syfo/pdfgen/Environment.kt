package no.nav.syfo.pdfgen

data class Environment(
    val enablePdfGet: Boolean = System.getenv("ENABLE_PDF_GET")?.let { it == "true" } ?: true,
    val enablePdfAValidation: Boolean = System.getenv("ENABLE_PDFA_VALIDATION")?.let { it == "true" } ?: true,
)
