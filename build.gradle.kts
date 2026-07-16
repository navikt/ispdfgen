plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(ktorLibs.plugins.ktor)
    alias(libs.plugins.shadow)
    alias(libs.plugins.ktlint)
}

group = "no.nav.syfo"
version = "1.0.0-SNAPSHOT"

application {
    mainClass = "no.nav.syfo.pdfgen.AppKt"
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation(ktorLibs.server.core)
    implementation(ktorLibs.server.netty)
    implementation(ktorLibs.server.contentNegotiation)
    implementation(ktorLibs.server.statusPages)
    implementation(ktorLibs.server.metrics.micrometer)
    implementation(ktorLibs.serialization.jackson)

    implementation(libs.handlebars)
    implementation(libs.handlebars.jackson)

    implementation(libs.openhtmltopdf.pdfbox)
    implementation(libs.openhtmltopdf.slf4j)
    implementation(libs.openhtmltopdf.svg.support)
    constraints {
        implementation(libs.commons.io) {
            because("Due to vulnerabilities in io.github.openhtmltopdf:openhtmltopdf-svg-support")
        }
    }

    implementation(libs.jackson.core)
    implementation(libs.jackson.module.kotlin)
    implementation(libs.jackson.datatype.jsr310)
    constraints {
        implementation(libs.tools.jackson.databind) {
            because("Due to vulnerabilities")
        }
    }

    implementation(libs.jaxb.api)
    implementation(libs.jaxb.runtime)
    implementation(libs.jsoup)

    implementation(libs.micrometer.registry.prometheus)

    implementation(libs.verapdf.validation.model)
    constraints {
        implementation(libs.rhino) {
            because("Due to vulnerabilities in org.verapdf:validation-model-jakarta")
        }
    }

    implementation(libs.logback.classic)
    implementation(libs.logstash.logback.encoder)

    testImplementation(kotlin("test"))
    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.junit.jupiter.params)
    testImplementation(libs.junit.jupiter.engine)
    testImplementation(ktorLibs.server.testHost)
    testImplementation(ktorLibs.client.cio)
    testImplementation(ktorLibs.client.contentNegotiation)
}

tasks {
    register("printVersion") {
        doLast {
            println(project.version)
        }
    }

    shadowJar {
        filesMatching("META-INF/services/**") {
            duplicatesStrategy = DuplicatesStrategy.WARN
        }
        mergeServiceFiles()
        archiveFileName.set("app.jar")
        archiveClassifier.set("")
        archiveVersion.set("")
    }

    test {
        useJUnitPlatform()
    }
}

afterEvaluate {
    tasks.shadowJar {
        archiveFileName.set("app.jar")
    }
}
