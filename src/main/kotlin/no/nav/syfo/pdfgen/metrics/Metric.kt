package no.nav.syfo.pdfgen.metrics

import io.micrometer.core.instrument.Timer
import io.micrometer.prometheusmetrics.PrometheusConfig
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry

const val METRICS_NS = "ispdfgen"

val METRICS_REGISTRY = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)

val HANDLEBARS_RENDERING_TIMER: Timer =
    Timer
        .builder("${METRICS_NS}_core_handlebars_rendering")
        .description("Time it takes for handlebars to render the template")
        .register(METRICS_REGISTRY)
