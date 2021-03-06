package com.numble.team3.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig implements WebMvcConfigurer {

  @Bean
  public Docket pawsApi() {
    return new Docket(DocumentationType.SWAGGER_2)
      .useDefaultResponseMessages(false)
      .groupName("Paws API")
      .select()
      .apis(RequestHandlerSelectors.any())
      .paths(PathSelectors.ant("/api/**"))
      .build()
      .apiInfo(this.pawsApiInfo());

  }

  private ApiInfo pawsApiInfo() {
    return new ApiInfoBuilder()
      .title("Paws API")
      .description("Paws API")
      .version("1.0")
      .build();
  }
}
