#!/bin/sh
# Un volumen persistente montado en runtime (ej. UPLOADS_DIR en Railway)
# reemplaza lo que haya en esa ruta, incluyendo los permisos que se
# hubieran fijado en tiempo de build. Por eso el ajuste de dueno debe
# pasar aca, en cada arranque del contenedor, no en el Dockerfile.
set -e

UPLOAD_DIR="${UPLOADS_DIR:-/app/uploads}"
mkdir -p "$UPLOAD_DIR"
chown -R spring:spring "$UPLOAD_DIR"

exec gosu spring java -jar /app/app.jar
