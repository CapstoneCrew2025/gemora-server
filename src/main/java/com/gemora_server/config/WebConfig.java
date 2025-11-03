package com.gemora_server.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:./uploads/");

        // Backward compatibility: serve /files/** from the same uploads directory
        registry.addResourceHandler("/files/**")
                .addResourceLocations("file:./uploads/");
    }

    
}
