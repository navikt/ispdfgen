package no.nav.syfo.pdfgen.core

import java.util.concurrent.atomic.AtomicReference

private val coreEnvironment: AtomicReference<Environment?> = AtomicReference<Environment?>(null)

class PDFGenCore {
    companion object {
        fun init(initialEnvironment: Environment) {
            coreEnvironment.set(initialEnvironment)
        }

        val environment: Environment
            get() =
                coreEnvironment.updateAndGet { current ->
                    current ?: Environment()
                }!!

        fun reloadEnvironment() {
            coreEnvironment.updateAndGet { currentEnv -> currentEnv?.copy() ?: Environment() }
        }
    }
}
