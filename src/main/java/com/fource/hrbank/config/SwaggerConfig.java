package com.fource.hrbank.config;

import io.swagger.v3.oas.models.tags.Tag;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("HR Bank API")
                .description("HR Bank API 문서")
                .version("v1.0")
            )
            .tags(List.of(
                new Tag().name("직원 관리").description("직원 관리 API"),
                new Tag().name("부서 관리").description("부서 관리 API"),
                new Tag().name("데이터 백업 관리").description("데이터 백업 관리 API"),
                new Tag().name("직원 정보 수정 이력 관리").description("직원 정보 수정 이력 관리 API"),
                new Tag().name("파일 관리").description("파일 관리 API")
            ));

    }
}
