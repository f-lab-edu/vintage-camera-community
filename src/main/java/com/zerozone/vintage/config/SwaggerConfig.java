package com.zerozone.vintage.config;

import com.zerozone.vintage.dto.CustomResDto;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SwaggerConfig{
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSchemas("CustomResDto", createCustomResDtoSchema()))
                .info(new Info()
                        .title("VINCAMO API")
                        .description("Vintage Camera Community REST API")
                        .version("1.0.0"));
    }

    private Schema<?> createCustomResDtoSchema() {
        Schema<?> schema = new Schema<>();
        schema.setDescription("공통 응답 DTO");
        schema.addProperty("code", new Schema<>().type("integer").description("응답 코드"));
        schema.addProperty("message", new Schema<>().type("string").description("응답 메시지"));
        schema.addProperty("data", new Schema<>().description("응답 데이터"));
        return schema;
    }

}
