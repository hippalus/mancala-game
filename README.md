# Habip Hakan Mancala Game

## Installation and Running

```bash
cd mancala/ 

./mvnw clean install -Dmaven.test.skip=true

cd ../

docker-compose -f docker/docker-compose.yml up

```

## Playing

Go to http://localhost:8080/ . Create player and start game.

## Running Tests

```bash
cd mancala/

./mvnw clean test
  ```

## Tech Stack

**Server:** Java >= 17, Maven >= 3.8.1 Spring Boot 2.6.7, Mongo DB , Project Reactor, Docker.

**Client:** Angular 13, RxJs 7.5.0, SCSS

## API Reference

- http://localhost:8081/swagger-doc/webjars/swagger-ui/index.html


