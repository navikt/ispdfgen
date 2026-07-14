package no.nav.syfo.pdfgen

data class Environment(
    val disablePdfGet: Boolean = System.getenv("DISABLE_PDF_GET")?.let { it == "true" } ?: true,
)
