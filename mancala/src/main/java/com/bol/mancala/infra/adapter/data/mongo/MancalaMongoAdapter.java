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
import com.bol.mancala.game.port.MancalaGamePort;
import com.bol.mancala.infra.adapter.data.mongo.document.MancalaGameDocument;
import com.bol.mancala.infra.adapter.data.mongo.document.PitDocument;
import com.bol.mancala.infra.adapter.data.mongo.respository.MancalaMongoRepository;
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
    final Players players = new PlayersImpl(
        new Player("Player 1", board.firstBigPitPosition()),
        new Player("Player 2", board.secondBigPitPosition())
    );
    final Game game = new GameImpl(null, board, players);
    return this.mancalaMongoRepository.save(this.toDocument(game)).map(MancalaGameDocument::toModel);
  }

  @Override
  public Mono<Game> retrieve(final String gameId) {
    return this.mancalaMongoRepository.findById(gameId).map(MancalaGameDocument::toModel);
  }

  @Override
  public Mono<Game> play(final String gameId, final int position) {
    return this.retrieve(gameId)
        .map(game -> {
          final Player current = game.players().current();
          final Game playedGame = game.play(Move.of(current, position));
          return this.toDocument(playedGame);
        })
        .flatMap(this.mancalaMongoRepository::save)
        .map(MancalaGameDocument::toModel);

  }


  private MancalaGameDocument toDocument(final Game game) {
    return MancalaGameDocument.builder()
        .id(game.id())
        .pits(game.board().getPits().stream().map(this::toDocument).toList())
        .players(game.players().players())
        .build();
  }

  private PitDocument toDocument(final Pit pit) {
    return new PitDocument(pit.position(), pit.getStones());
  }
}