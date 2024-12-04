package com.doublehexa.game.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.http.CacheControl;

import java.util.concurrent.TimeUnit;

@Configuration  // Marca esta classe como uma classe de configuração do Spring
public class WebConfig implements WebMvcConfigurer {

    /**
     * Configura o gerenciamento de recursos estáticos (static resources)
     * como arquivos JS, CSS, imagens, etc.
     *
     * @param registry O registro de manipuladores de recursos
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**")  // Define o padrão de URL para acessar recursos
                .addResourceLocations("classpath:/static/")  // Define onde os recursos estão localizados
                .setCacheControl(CacheControl.maxAge(365, TimeUnit.DAYS))  // Configura cache para recursos estáticos
                .resourceChain(true);  // Habilita o encadeamento de recursos para otimização

        // Configuração específica para arquivos JavaScript
        registry.addResourceHandler("/js/**")
                .addResourceLocations("classpath:/static/js/")
                .setCacheControl(CacheControl.noCache())  // Desabilita cache durante desenvolvimento
                .resourceChain(true);
    }
}