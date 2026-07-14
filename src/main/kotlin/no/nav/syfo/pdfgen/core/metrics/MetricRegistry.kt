package no.nav.syfo.pdfgen.core.metrics

import io.prometheus.client.Summary

val HANDLEBARS_RENDERING_SUMMARY: Summary =
    Summary
        .Builder()
        .name("pdfgen_core_handlebars_rendering")
        .help("Time it takes for handlebars to render the template")
        .register()
