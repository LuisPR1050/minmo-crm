package com.felipeparodi.crmbackend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

import java.io.IOException;

/**
 * Sirve el frontend (React Router / BrowserRouter) embebido en classpath:/static/.
 * Si la ruta pedida no corresponde a un archivo estatico real (ej. "/clientes/123"),
 * hace fallback a index.html para que el router del lado del cliente la resuelva.
 * No interfiere con "/uploads/**" (registrado en AlmacenamientoWebConfig) ni con
 * "/api/**": Spring prioriza siempre el patron mas especifico entre los handlers
 * registrados, y los controladores anotados tienen precedencia sobre este handler
 * de recursos.
 */
@Configuration
public class SpaWebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/")
                .resourceChain(true)
                .addResolver(new PathResourceResolver() {
                    @Override
                    protected Resource getResource(String resourcePath, Resource location) throws IOException {
                        Resource recursoPedido = location.createRelative(resourcePath);
                        if (recursoPedido.exists() && recursoPedido.isReadable()) {
                            return recursoPedido;
                        }
                        return new ClassPathResource("/static/index.html");
                    }
                });
    }
}
