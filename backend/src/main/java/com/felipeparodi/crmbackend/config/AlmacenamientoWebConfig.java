package com.felipeparodi.crmbackend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;

@Configuration
public class AlmacenamientoWebConfig implements WebMvcConfigurer {

    private final String directorioUploads;

    public AlmacenamientoWebConfig(@Value("${storage.uploads-dir}") String directorioUploads) {
        this.directorioUploads = directorioUploads;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String ubicacion = Path.of(directorioUploads).toAbsolutePath().normalize().toUri().toString();

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(ubicacion);
    }
}
