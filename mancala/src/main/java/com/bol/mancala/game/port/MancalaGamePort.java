package com.bol.mancala.game.port;

import com.bol.mancala.game.Game;
import com.bol.mancala.game.GameOptions;
import reactor.core.publisher.Mono;

public interface MancalaGamePort {

  Mono<Game> create(GameOptions gameOptions);

  Mono<Game> retrieve(String gameId);

  Mono<Game> play(String gameId, int position);
}
