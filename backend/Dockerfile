# syntax=docker/dockerfile:1

# ---------- Stage 1: build del frontend ----------
FROM node:22-alpine AS frontend-build
WORKDIR /app/frontend
COPY frontend/package.json frontend/package-lock.json ./
RUN npm ci
COPY frontend/ ./
RUN npm run build

# ---------- Stage 2: build del backend (incluye el dist del frontend embebido) ----------
FROM eclipse-temurin:21-jdk-alpine AS backend-build
WORKDIR /app/backend
COPY backend/.mvn/ .mvn/
COPY backend/mvnw ./
COPY backend/pom.xml ./
RUN chmod +x mvnw && ./mvnw -B dependency:go-offline
COPY backend/src/ src/

# El dist del frontend debe copiarse a src/main/resources/static ANTES de
# empaquetar para que Spring Boot lo sirva como recurso estatico embebido en el jar.
COPY --from=frontend-build /app/frontend/dist/ src/main/resources/static/

RUN ./mvnw -B -DskipTests package \
    && cp target/*.jar target/app.jar

# ---------- Stage 3: imagen final ----------
# Se usa una base Debian (no Alpine) porque Piper (TTS neuronal de la voz en off) depende de
# ONNX Runtime, que Microsoft no publica compilado para musl/Alpine: en Alpine el binario de
# Piper no arranca (simbolos de glibc faltantes), asi que se necesita libc real.
FROM eclipse-temurin:21-jre-jammy AS final
WORKDIR /app
RUN apt-get update && apt-get install -y --no-install-recommends \
        ffmpeg fonts-dejavu-core ca-certificates curl \
    && curl -fsSL -o /usr/local/bin/gosu "https://github.com/tianon/gosu/releases/download/1.17/gosu-amd64" \
    && chmod +x /usr/local/bin/gosu \
    && groupadd -r spring && useradd -r -g spring spring \
    && rm -rf /var/lib/apt/lists/*
ENV FFMPEG_FONT_PATH=/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf

# Piper (TTS neuronal local) + voz es_MX-claude-high (calidad alta) para la narracion
# opcional del video-slideshow de marketing. Se descargan en build time para no inflar el
# repositorio con binarios grandes (el modelo de voz pesa ~63MB).
ENV PIPER_PATH=/opt/piper/piper
ENV PIPER_VOZ_MODELO_PATH=/opt/piper/es_MX-claude-high.onnx
ENV LD_LIBRARY_PATH=/opt/piper
RUN mkdir -p /opt/piper \
    && curl -fsSL -o /tmp/piper.tar.gz "https://github.com/rhasspy/piper/releases/download/2023.11.14-2/piper_linux_x86_64.tar.gz" \
    && tar -xzf /tmp/piper.tar.gz -C /opt/piper --strip-components=1 \
    && rm /tmp/piper.tar.gz \
    && curl -fsSL -o "$PIPER_VOZ_MODELO_PATH" "https://huggingface.co/rhasspy/piper-voices/resolve/main/es/es_MX/claude/high/es_MX-claude-high.onnx" \
    && curl -fsSL -o "$PIPER_VOZ_MODELO_PATH.json" "https://huggingface.co/rhasspy/piper-voices/resolve/main/es/es_MX/claude/high/es_MX-claude-high.onnx.json"

COPY --from=backend-build /app/backend/target/app.jar app.jar
COPY backend/docker-entrypoint.sh /docker-entrypoint.sh
RUN chmod +x /docker-entrypoint.sh && chown -R spring:spring /app

# Se arranca como root a proposito: el entrypoint ajusta el dueno del
# volumen de uploads (que en runtime puede pisar los permisos fijados
# aqui) y luego baja privilegios a "spring" con gosu antes de
# ejecutar la aplicacion.
EXPOSE 8080
ENTRYPOINT ["/docker-entrypoint.sh"]
