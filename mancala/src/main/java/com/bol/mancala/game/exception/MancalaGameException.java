package com.bol.mancala.game.exception;

import lombok.Getter;

@Getter
public class MancalaGameException extends RuntimeException {

  private final String message;

  public MancalaGameException(final String message) {
    super(message);
    this.message = message;
  }
}
