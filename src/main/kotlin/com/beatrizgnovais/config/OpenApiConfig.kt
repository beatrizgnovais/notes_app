package com.beatrizgnovais.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {

    @Bean
    fun customOpenAPI(): OpenAPI = OpenAPI()
        .info(
            Info()
                .title("Notes App API")
                .description("API REST para gerenciamento de notas e usuarios, com suporte a exportacao em PDF")
                .version("1.0.0")
        )
}
