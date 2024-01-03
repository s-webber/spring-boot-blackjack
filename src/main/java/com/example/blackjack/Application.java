package com.example.blackjack;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

/** The class containing the {@code main} method used to launch this Spring Boot application. */
@EnableWebSecurity
@SpringBootApplication
public class Application {
   public static void main(String[] args) {
      SpringApplication.run(Application.class, args);
   }

   @Bean
   public GroupedOpenApi publicApi() {
      return GroupedOpenApi.builder().group("Spring Boot Blackjack").pathsToMatch("/blackjack", "/blackjack/*").build();
   }

   @Bean
   public OpenAPI springShopOpenAPI() {
      return new OpenAPI().info(new Info().title("Spring Boot Blackjack").description("An implementation of the blackjack card game.").version("v0.0.1"))
                  .externalDocs(new ExternalDocumentation().description("GitHub repository").url("https://github.com/s-webber/spring-boot-blackjack"));
   }
}
