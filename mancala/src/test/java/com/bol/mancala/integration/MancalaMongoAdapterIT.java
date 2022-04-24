package com.bol.mancala.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.from;

import com.bol.mancala.AbstractIT;
import com.bol.mancala.IT;
import com.bol.mancala.game.Game;
import com.bol.mancala.game.GameOptions;
import com.bol.mancala.game.Pit;
import com.bol.mancala.game.Player;
import com.bol.mancala.game.exception.DataNotFoundException;
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
    //given:
    final Player firstPlayer = new Player("Player 1", 6);
    final Player secondPlayer = new Player("Player 2", 13);
    final GameOptions gameOptions = new GameOptions(6, 14, firstPlayer, secondPlayer);

    //when and then:
    this.mancalaMongoAdapter.create(gameOptions)
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

  @Test
  void throwNotFoundExWhenFindByMethodReturnEmpty() {

    final String invalidId = "INVALID_ID";

    this.mancalaMongoAdapter.retrieve(invalidId)
        .log()
        .as(StepVerifier::create)
        .expectError(DataNotFoundException.class)
        .verify();
  }

  @SneakyThrows
  @Test
  void play() {

    final MancalaGameDocument mancalaGameDocument = this.mapper.readValue(
        ResourceUtils.getFile("src/test/resources/new-game.json"),
        MancalaGameDocument.class
    );
    this.mancalaMongoRepository.save(mancalaGameDocument).block();

    final MancalaGameDocument expectedGameDoc = this.mapper.readValue(
        ResourceUtils.getFile("src/test/resources/first-player-first-move.json"),
        MancalaGameDocument.class
    );
    final Game expected = expectedGameDoc.toModel();

    this.mancalaMongoAdapter.play(mancalaGameDocument.getId(), 1)
        .log()
        .as(StepVerifier::create)
        .consumeNextWith(playedGame -> {
          assertThat(playedGame).isNotNull()
              .returns(expected.id(), from(Game::id))
              .returns(expected.board().getPits(), from(game -> game.board().getPits()))
              .returns(expected.players().players(), from(game -> game.players().players()))
              .returns(expected.players().current(), from(game -> game.players().current()));
        })
        .verifyComplete();
  }
}