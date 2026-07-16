package no.nav.syfo.pdfgen.core.util

import com.openhtmltopdf.outputdevice.helper.BaseRendererBuilder

data class FontMetadata(
    val family: String,
    val path: String,
    val weight: Int,
    val style: BaseRendererBuilder.FontStyle,
    val subset: Boolean,
)
