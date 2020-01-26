# ![RealWorld Example App](vertx-logo.png)

> ### Eclipse Vert.x tool-kit codebase containing real world examples (CRUD, auth, advanced patterns, etc) that adheres to the [RealWorld](https://github.com/gothinkster/realworld) spec and API.


### [Demo](https://github.com/gothinkster/realworld)&nbsp;&nbsp;&nbsp;&nbsp;[RealWorld](https://github.com/gothinkster/realworld)


This codebase was created to demonstrate a fully fledged fullstack application built with [Eclipse Vert.x](https://vertx.io/)) including CRUD operations, authentication, routing, pagination, and more.

We've gone to great lengths to adhere to the Eclipse Vert.x community styleguides & best practices.

For more information on how to this works with other frontends/backends, head over to the [RealWorld](https://github.com/gothinkster/realworld) repo.

[![Build Status](https://travis-ci.org/diegocamara/vertx-springboot-realworld-example-app.svg?branch=master)](https://travis-ci.org/diegocamara/vertx-springboot-realworld-example-app)

# How it works

This application demonstrates an approach to using Eclipse Vert.x infrastructure to implement a web api. 

Some modules used in this app:

* [Spring](https://spring.io/) to perform dependency injection 
* [Jackson](https://github.com/FasterXML/jackson) to perform Serialization/Deserialization on the web layer
* [Flyway](https://flywaydb.org/) to perform database version control
* [H2 Database Engine](https://www.h2database.com) to persist data
* [JBCrypt](https://github.com/jeremyh/jBCrypt) for hash passwords
* [Slugify](https://github.com/slugify/slugify) to get title slugs
* [Vert.x JDBC Client](https://vertx.io/docs/vertx-jdbc-client/java/) to execute async queries
* [Vert.x Service Proxy](https://vertx.io/docs/vertx-service-proxy/java/) to expose services on the event bus
* [Vert.x RxJava](https://vertx.io/docs/vertx-rx/java2/) to perform async operations in business layer

### Project Structure
```
main/generated   ->   VertxEBProxy, VertxProxyHandler and Request/Response converters
+--java
|  +--com
|     +--example
|        +--realworld
|           +-- application     ->     spec logic implementation
|           +-- domain          ->     domain model objects
|           +-- infrastructure  ->     technical details package
```

# Getting started

### Start local server

```bash
./mvnw spring-boot:run
```
The server should be running at http://localhost:8080

### Running the application's integrated tests

``` 
./mvnw test
```

### Running postman collection tests

```
./collections/run-api-tests.sh
```

### Building jar file

```
./mvnw package
```

#### Database changes can be made to the application.yml file.

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb;DB_CLOSE_ON_EXIT=FALSE;DB_CLOSE_DELAY=-1
    username: sa
    password:
```

## Help
Improvements are welcome, feel free to contribute.

