package com.bol.mancala.infra.adapter.rest.dto.request;

import com.bol.mancala.game.GameOptions;
import com.bol.mancala.game.Player;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

public record CreateGameRequest(
        @Positive int stoneAmount,
        @Positive int pitAmount,
        @NotNull Player firstPlayer,
        @NotNull Player secondPlayer) {

    public GameOptions toGameOptions() {
        return new GameOptions(this.stoneAmount, this.pitAmount, this.firstPlayer, this.secondPlayer);
    }
}
