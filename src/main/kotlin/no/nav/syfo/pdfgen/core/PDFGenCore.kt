package no.nav.syfo.pdfgen.core

import com.openhtmltopdf.slf4j.Slf4jLogger
import com.openhtmltopdf.util.XRLog
import org.verapdf.gf.foundry.VeraGreenfieldFoundryProvider
import java.util.concurrent.atomic.AtomicReference

private val coreEnvironment: AtomicReference<Environment?> = AtomicReference<Environment?>(null)

class PDFGenCore {
    companion object {
        fun init(initialEnvironment: Environment = Environment()) {
            // sørger for at PDF/A-fargeprofilen håndteres konsistent på tvers av JVM-er
            System.setProperty("sun.java2d.cmm", "sun.java2d.cmm.kcms.KcmsServiceProvider")
            VeraGreenfieldFoundryProvider.initialise()
            XRLog.setLoggerImpl(Slf4jLogger())
            coreEnvironment.set(initialEnvironment)
        }

        val environment: Environment
            get() =
                coreEnvironment.updateAndGet { current ->
                    current ?: Environment()
                }!!
    }
}
