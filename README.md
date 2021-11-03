## Description
This is the back-end part of nightori's link shortener, a small and nice URL shortening service which is hosted [here](https://cc.nightori.ru).

User features:
 - No registration or captcha!
 - No ads or other annoyance!
 - Custom URL aliases as well as auto-generated ones!
 - Link deletion with a password!
 - Nobody knows about it, so there's a ton of free aliases!
 
 Technical features:
 - Configurable rate limiting to prevent request spam
 - Caching some requests to improve performance
 - Using bCrypt to safely work with passwords
 - Regex validation for all incoming data

## Dependencies
This application is built with [Spring Boot](https://spring.io/projects/spring-boot). The following modules are used:
- Spring Web
- Spring Data JPA
- Spring Security
- Validation
- Spring cache abstraction
- Testing

The data is stored in a MySQL database, so [JDBC Driver for MySQL](https://www.mysql.com/products/connector/) is used for connection.

[Ehcache](https://www.ehcache.org/documentation/3.0/107.html) is used as a JCache provider.

[Lombok](https://projectlombok.org) is used to make life easier.

[H2](https://www.h2database.com/html/main.html) is used as an in-memory database for testing purposes.

## Running it yourself
Use Gradle to run or build this, it's a regular Spring Boot application. You will also need to set the required env variables.

Use [this](src/main/resources/application.properties.sample) as a sample. Don't forget to put your own credentials for the datasource.
