package com.example.blackjack;

import static springfox.documentation.builders.PathSelectors.regex;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/** The class containing the {@code main} method used to launch this Spring Boot application. */
@EnableWebSecurity
@SpringBootApplication
public class Application {
   public static void main(String[] args) {
      SpringApplication.run(Application.class, args);
   }

   @Bean
   public Docket blackjackApi() {
      return new Docket(DocumentationType.SWAGGER_2).groupName("blackjack").apiInfo(apiInfo()).select().paths(regex("/blackjack.*?.*")).build();
   }

   private ApiInfo apiInfo() {
      return new ApiInfoBuilder().title("Spring Boot Blackjack").description("An implementation of the blackjack card game.").build();
   }
}
