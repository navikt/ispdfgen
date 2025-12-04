FROM ghcr.io/navikt/pdfgen:2.0.101

ENV JDK_JAVA_OPTIONS="-XX:MaxRAMPercentage=75 -XX:InitialRAMPercentage=75 -Dlogback.configurationFile=logback-remote.xml"
# use the following two lines to enable testing in dev-gcp
# ENV DISABLE_PDF_GET=false
# COPY data /app/data

COPY templates /app/templates
COPY fonts /app/fonts
COPY resources /app/resources
