package com.bol.mancala.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.from;

import com.bol.mancala.AbstractIT;
import com.bol.mancala.IT;
import com.bol.mancala.game.Game;
import com.bol.mancala.game.GameOptions;
import com.bol.mancala.game.Pit;
import com.bol.mancala.infra.adapter.data.mongo.MancalaMongoAdapter;
import com.bol.mancala.infra.adapter.data.mongo.document.MancalaGameDocument;
import com.bol.mancala.infra.adapter.data.mongo.document.PitDocument;
import com.bol.mancala.infra.adapter.data.mongo.respository.MancalaMongoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ResourceUtils;
import reactor.test.StepVerifier;

@IT
class MancalaMongoAdapterIT extends AbstractIT {

  private final ObjectMapper mapper = new ObjectMapper();
  @Autowired
  private MancalaMongoAdapter mancalaMongoAdapter;

  @Autowired
  private MancalaMongoRepository mancalaMongoRepository;


  @AfterEach
  void tearDown() {
    this.mancalaMongoRepository.deleteAll().block();
  }


  @Test
  void create() {
    this.mancalaMongoAdapter.create(new GameOptions(6, 14))
        .log()
        .as(StepVerifier::create)
        .consumeNextWith(game -> {
          assertThat(game.id()).isNotNull();
          assertThat(game.board()).isNotNull();
          assertThat(game.isGameOver()).isFalse();
          assertThat(game.players().players()).isNotEmpty();
        })
        .verifyComplete();
  }

  @Test
  @SneakyThrows
  void retrieve() {

    final MancalaGameDocument gameDocument = this.mapper.readValue(
        ResourceUtils.getFile("src/test/resources/test-data-single.json"),
        MancalaGameDocument.class
    );

    this.mancalaMongoRepository.save(gameDocument)
        .flatMap(g -> this.mancalaMongoAdapter.retrieve("id12345"))
        .log()
        .as(StepVerifier::create)
        .consumeNextWith(retrieved -> {
          //
          assertThat(retrieved).isNotNull()
              .returns("id12345", from(Game::id));

          final List<Pit> pits = gameDocument.getPits()
              .stream()
              .map(PitDocument::toModel)
              .toList();
          //
          assertThat(retrieved.board().getPits()).isEqualTo(pits);
        })
        .verifyComplete();
  }

  @SneakyThrows
  @Test
  void play() {

    final MancalaGameDocument mancalaGameDocument = this.mapper.readValue(
        ResourceUtils.getFile("src/test/resources/new-game.json"),
        MancalaGameDocument.class
    );

    final MancalaGameDocument expectedGame = this.mapper.readValue(
        ResourceUtils.getFile("src/test/resources/first-player-first-move.json"),
        MancalaGameDocument.class
    );

    final MancalaGameDocument persistedGame = this.mancalaMongoRepository.save(mancalaGameDocument).block();

    this.mancalaMongoAdapter.play(persistedGame.getId(), 1)
        .log()
        .as(StepVerifier::create)
        .consumeNextWith(playedGame -> {
          assertThat(playedGame).isNotNull()
              .returns(expectedGame.getId(), from(Game::id))
              .returns(expectedGame.getPits().stream().map(PitDocument::toModel).toList(), from(game -> game.board().getPits()))
              .returns(expectedGame.getPlayers(), from(game -> game.players().players()));
        })
        .verifyComplete();
  }
}