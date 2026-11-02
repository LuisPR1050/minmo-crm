package com.felipeparodi.crmbackend.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AlmacenamientoVideoServiceTest {

    @Mock
    private S3Client s3Client;

    @Test
    void guardaUnVideoEnElVolumenLocalCuandoR2NoEstaConfigurado(@TempDir Path directorioTemporal) throws IOException {
        AlmacenamientoArchivosService almacenamiento = new AlmacenamientoArchivosService(directorioTemporal.toString());
        AlmacenamientoVideoService servicio = new AlmacenamientoVideoService(
                almacenamiento, directorioTemporal.toString(), "", "", "", "", "");

        MockMultipartFile archivo = new MockMultipartFile("archivo", "recorrido.mp4", "video/mp4", new byte[]{1, 2, 3, 4});

        String urlPublica = servicio.guardarVideo("videos-inmueble", archivo);

        assertThat(urlPublica).startsWith("/uploads/videos-inmueble/").endsWith(".mp4");

        Path archivoGuardado = directorioTemporal.resolve(urlPublica.substring("/uploads/".length()));
        assertThat(Files.exists(archivoGuardado)).isTrue();
        assertThat(Files.readAllBytes(archivoGuardado)).isEqualTo(new byte[]{1, 2, 3, 4});

        servicio.eliminarVideo(urlPublica);
        assertThat(Files.exists(archivoGuardado)).isFalse();
    }

    @Test
    void rechazaUnTipoDeContenidoQueNoEsVideo(@TempDir Path directorioTemporal) {
        AlmacenamientoArchivosService almacenamiento = new AlmacenamientoArchivosService(directorioTemporal.toString());
        AlmacenamientoVideoService servicio = new AlmacenamientoVideoService(
                almacenamiento, directorioTemporal.toString(), "", "", "", "", "");

        MockMultipartFile archivo = new MockMultipartFile("archivo", "foto.jpg", "image/jpeg", new byte[]{1});

        assertThatThrownBy(() -> servicio.guardarVideo("videos-inmueble", archivo))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Solo se permiten videos");
    }

    @Test
    void subeAR2CuandoEstaConfiguradoYDevuelveLaUrlPublicaDelBucket(@TempDir Path directorioTemporal) {
        AlmacenamientoArchivosService almacenamiento = new AlmacenamientoArchivosService(directorioTemporal.toString());
        AlmacenamientoVideoService servicio = new AlmacenamientoVideoService(
                almacenamiento, directorioTemporal.toString(), "mi-bucket", "https://media.minmo.com", s3Client);

        when(s3Client.putObject(any(PutObjectRequest.class), any(software.amazon.awssdk.core.sync.RequestBody.class)))
                .thenReturn(PutObjectResponse.builder().build());

        MockMultipartFile archivo = new MockMultipartFile("archivo", "recorrido.mp4", "video/mp4", new byte[]{1, 2, 3});

        String urlPublica = servicio.guardarVideo("videos-inmueble", archivo);

        assertThat(urlPublica).startsWith("https://media.minmo.com/videos-inmueble/").endsWith(".mp4");
        verify(s3Client).putObject(any(PutObjectRequest.class), any(software.amazon.awssdk.core.sync.RequestBody.class));

        servicio.eliminarVideo(urlPublica);
        verify(s3Client).deleteObject(any(DeleteObjectRequest.class));
    }
}
