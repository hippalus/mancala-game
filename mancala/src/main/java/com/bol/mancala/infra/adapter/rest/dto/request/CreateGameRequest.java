package com.bol.mancala.infra.adapter.rest.dto.request;

import com.bol.mancala.game.GameOptions;
import javax.validation.constraints.Positive;

public record CreateGameRequest(
    @Positive int stoneAmount,
    @Positive int pitAmount) {

  public GameOptions toGameOptions() {
    return new GameOptions(this.stoneAmount, this.pitAmount);
  }
}
