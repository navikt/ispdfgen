#!/bin/bash

CURRENT_PATH=$(pwd)

winpty docker pull ghcr.io/navikt/pdfgen:2.0.81
winpty docker run \
        -v "/$CURRENT_PATH/templates:/app/templates" \
        -v "/$CURRENT_PATH/fonts:/app/fonts" \
        -v "/$CURRENT_PATH/data:/app/data" \
        -v "/$CURRENT_PATH/resources:/app/resources" \
        -p 8080:8080 \
        -e DISABLE_PDF_GET=false \
        -e JDK_JAVA_OPTIONS \
        -it \
        --rm \
        ghcr.io/navikt/pdfgen:2.0.81

