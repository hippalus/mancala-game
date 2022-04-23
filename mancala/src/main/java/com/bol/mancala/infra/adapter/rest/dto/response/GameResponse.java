package com.bol.mancala.infra.adapter.rest.dto.response;

import com.bol.mancala.game.Game;
import com.bol.mancala.game.Pit;
import com.bol.mancala.game.Player;
import java.util.List;

public record GameResponse(
    String id,
    List<Pit> pits,
    List<Player> players,
    Player current) {

  public static GameResponse fromModel(final Game game) {
    return new GameResponse(
        game.id(),
        game.board().getPits(),
        game.players().players(),
        game.players().current());
  }
}
