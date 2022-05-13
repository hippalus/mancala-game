package com.bol.mancala.infra.adapter.data.mongo;

import com.bol.mancala.game.Board;
import com.bol.mancala.game.BoardImpl;
import com.bol.mancala.game.Game;
import com.bol.mancala.game.GameImpl;
import com.bol.mancala.game.GameOptions;
import com.bol.mancala.game.Move;
import com.bol.mancala.game.Pit;
import com.bol.mancala.game.Player;
import com.bol.mancala.game.Players;
import com.bol.mancala.game.PlayersImpl;
import com.bol.mancala.game.Winner;
import com.bol.mancala.game.exception.DataNotFoundException;
import com.bol.mancala.game.port.MancalaGamePort;
import com.bol.mancala.infra.adapter.data.mongo.document.MancalaGameDocument;
import com.bol.mancala.infra.adapter.data.mongo.document.PitDocument;
import com.bol.mancala.infra.adapter.data.mongo.document.PlayerDocument;
import com.bol.mancala.infra.adapter.data.mongo.document.WinnerDocument;
import com.bol.mancala.infra.adapter.data.mongo.respository.MancalaMongoRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class MancalaMongoAdapter implements MancalaGamePort {

  private final MancalaMongoRepository mancalaMongoRepository;

  @Override
  public Mono<Game> create(final GameOptions gameOptions) {
    final Board board = new BoardImpl(gameOptions.stoneAmount(), gameOptions.pitAmount());
    final Players players = new PlayersImpl(gameOptions.firstPlayer(), gameOptions.secondPlayer());
    final Game game = new GameImpl(null, board, players);
    return this.mancalaMongoRepository.save(MancalaMongoAdapter.toDocument(game)).map(MancalaGameDocument::toModel);
  }

  @Override
  public Mono<Game> retrieve(final String gameId) {
    return this.mancalaMongoRepository.findById(gameId)
        .map(MancalaGameDocument::toModel)
        .switchIfEmpty(Mono.error(new DataNotFoundException("Game not found for id: " + gameId)));
  }

  @Override
  public Mono<Game> play(final String gameId, final int position) {
    return this.retrieve(gameId)
        .map(game -> {
          final Player current = game.players().current();
          final Game playedGame = game.play(Move.of(current, position));
          return MancalaMongoAdapter.toDocument(playedGame);
        })
        .flatMap(this.mancalaMongoRepository::save)
        .map(MancalaGameDocument::toModel);

  }


  private static MancalaGameDocument toDocument(final Game game) {
    return MancalaGameDocument.builder()
        .id(game.id())
        .pits(game.board().getPits().stream().map(MancalaMongoAdapter::toDocument).toList())
        .players(game.players().players().stream().map(MancalaMongoAdapter::toDocument).toList())
        .gameOver(game.isGameOver())
        .winner(MancalaMongoAdapter.toDocument(game.winner()))
        .build();
  }

  private static WinnerDocument toDocument(final Optional<Winner> winner) {
    return winner.stream()
        .map(w -> new WinnerDocument(MancalaMongoAdapter.toDocument(w.winner()), w.score()))
        .findFirst()
        .orElse(null);
  }

  private static PitDocument toDocument(final Pit pit) {
    return new PitDocument(pit.position(), pit.getStones());
  }

  private static PlayerDocument toDocument(final Player player) {
    return new PlayerDocument(player.name(), player.bigPitPosition());
  }
}
