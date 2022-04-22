package com.bol.mancala;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.web.reactive.server.WebTestClient;

public abstract class AbstractIT {

  @Autowired
  protected WebTestClient webTestClient;

  @LocalServerPort
  protected Integer port;
}
