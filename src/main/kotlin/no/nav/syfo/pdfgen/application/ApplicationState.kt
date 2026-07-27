package no.nav.syfo.pdfgen.application

data class ApplicationState(
    var alive: Boolean = true,
    var ready: Boolean = false,
)
