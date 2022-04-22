package com.bol.mancala;

import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextClosedEvent;
import org.testcontainers.containers.MongoDBContainer;

@Slf4j
public class MongodbContainerInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

  @SuppressWarnings("resource")
  @Override
  public void initialize(final ConfigurableApplicationContext configurableApplicationContext) {
    final var mongoDBContainer = new MongoDBContainer("mongo")
        .withStartupTimeout(Duration.ofSeconds(60));

    mongoDBContainer.start();

    configurableApplicationContext
        .addApplicationListener((ApplicationListener<ContextClosedEvent>) event -> mongoDBContainer.stop());

    log.debug("mongoDBContainer.getReplicaSetUrl():" + mongoDBContainer.getReplicaSetUrl("mancala-db"));

    TestPropertyValues
        .of("spring.data.mongodb.uri=" + mongoDBContainer.getReplicaSetUrl("mancala-db"))
        .applyTo(configurableApplicationContext.getEnvironment());
  }
}
