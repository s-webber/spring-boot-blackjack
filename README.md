# Spring Boot Blackjack
![Build Status](https://github.com/s-webber/spring-boot-blackjack/actions/workflows/github-actions.yml/badge.svg)

## About

This project has been developed using Spring Boot and Java to provide a RESTful interface for an implementation of the blackjack card game.
The application is built using Gradle, tested using JUnit and Mockito, documented using Swagger and Spring REST Docs, and can be monitored using JMX.

## Purpose

This project has been developed for the purpose of improving my understanding of how Spring Boot, Java, Swagger and Gradle can be used together to construct RESTful services.
It is not the aim of this project to produce a production-ready system.
The source code comments contain `TODO`s that refer to how the system could be improved.

## Getting Started

To build this project you will first require Java 17 or higher.

The project can be built and started by using the command:

```
gradlew bootRun
```

Once the application is running you can use the Swagger documentation to interact with it by visiting: 

```
http://localhost:8080/swagger-ui.html
```

**Note:** When prompted for a username and password any value for username will be acceptable and the correct password value will be `password`

## Resources

The API documentation, generated during a `gradlew build` using Spring REST Docs, will be located at:

```
build/docs/blackjack-api.html
```

The test coverage report, generated during a `gradlew build` using JaCoCo, will be located at: 

```
build/jacoco/test/html/index.html
```

Here are some links that I found useful during the development of this project:

* Building an Application with Spring Boot: https://spring.io/guides/gs/spring-boot/
* Usage of Swagger 2.0 in Spring Boot Applications to document APIs: http://heidloff.net/article/usage-of-swagger-2-0-in-spring-boot-applications-to-document-apis/
* Example of replacing Spring Boot "whitelabel" error page with custom error responses: https://gist.github.com/jonikarppinen/662c38fb57a23de61c8b
* Securing REST APIs With Spring Boot: http://ryanjbaxter.com/2015/01/06/securing-rest-apis-with-spring-boot/
* Spring REST Docs: https://spring.io/projects/spring-restdocs
* GitHub Actions for Java with Gradle: https://docs.github.com/en/actions/automating-builds-and-tests/building-and-testing-java-with-gradle
