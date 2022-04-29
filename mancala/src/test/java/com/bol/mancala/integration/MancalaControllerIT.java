package com.bol.mancala.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.from;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

import com.bol.mancala.AbstractIT;
import com.bol.mancala.IT;
import com.bol.mancala.game.Game;
import com.bol.mancala.game.Player;
import com.bol.mancala.game.exception.DataNotFoundException;
import com.bol.mancala.infra.adapter.data.mongo.document.MancalaGameDocument;
import com.bol.mancala.infra.adapter.data.mongo.respository.MancalaMongoRepository;
import com.bol.mancala.infra.adapter.rest.dto.request.CreateGameRequest;
import com.bol.mancala.infra.adapter.rest.dto.response.ErrorResponse;
import com.bol.mancala.infra.adapter.rest.dto.response.GameResponse;
import com.bol.mancala.infra.adapter.rest.dto.response.WinnerResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.util.ResourceUtils;
import reactor.core.publisher.Mono;

@IT
class MancalaControllerIT extends AbstractIT {

  @MockBean
  private MancalaMongoRepository mancalaMongoRepository;
  private final ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
  private final ParameterizedTypeReference<GameResponse> gameResponseType = new ParameterizedTypeReference<>() {
  };
  private final ParameterizedTypeReference<ErrorResponse> errorResponseType = new ParameterizedTypeReference<>() {
  };


  @SneakyThrows
  @Test
  void create() {
    //given:
    final Player firstPlayer = new Player("Player 1", 6);
    final Player secondPlayer = new Player("Player 2", 13);
    final CreateGameRequest createGameRequest = new CreateGameRequest(6, 14, firstPlayer, secondPlayer);

    final MancalaGameDocument gameDocument = this.objectMapper.readValue(
        ResourceUtils.getFile("src/test/resources/new-game.json"),
        MancalaGameDocument.class
    );
    final Game expected = gameDocument.toModel();

    Mockito.when(this.mancalaMongoRepository.save(Mockito.any())).thenReturn(Mono.just(gameDocument));

    //when and then
    this.webTestClient.post()
        .uri("/api/v1/mancala")
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .body(Mono.just(createGameRequest), CreateGameRequest.class)
        .exchange()
        .expectStatus()
        .isCreated()
        .expectHeader()
        .contentType(MediaType.APPLICATION_JSON)
        .expectBody(this.gameResponseType)
        .consumeWith(response -> {
              final GameResponse body = response.getResponseBody();
              assertThat(body).isNotNull()
                  .returns(expected.players().players(), from(GameResponse::players))
                  .returns(expected.id(), from(GameResponse::id))
                  .returns(expected.board().getPits(), from(GameResponse::pits))
                  .returns(expected.players().current(), from(GameResponse::current));
            }
        );
  }

  @SneakyThrows
  @Test
  void shouldNotCreateInvalidArgs() {
    //given:
    final int INVALID_AMOUNT = -1;
    final CreateGameRequest createGameRequest = new CreateGameRequest(INVALID_AMOUNT, INVALID_AMOUNT, null, null);

    //when and then
    this.webTestClient.post()
        .uri("/api/v1/mancala")
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .body(Mono.just(createGameRequest), CreateGameRequest.class)
        .exchange()
        .expectStatus()
        .is4xxClientError()
        .expectHeader()
        .contentType(MediaType.APPLICATION_JSON)
        .expectBody(this.errorResponseType)
        .consumeWith(response -> {
              final ErrorResponse body = response.getResponseBody();
              assertThat(body).isNotNull()
                  .returns("400", from(ErrorResponse::errorCode));
              System.out.println(body.message());
            }
        );
  }

  @SneakyThrows
  @Test
  void play() {
    //given:
    final String gameId = "6261d18d701e87233773de98";

    final MancalaGameDocument mancalaGameDocument = this.objectMapper.readValue(
        ResourceUtils.getFile("src/test/resources/new-game.json"),
        MancalaGameDocument.class
    );

    final MancalaGameDocument afterMove = this.objectMapper.readValue(
        ResourceUtils.getFile("src/test/resources/first-player-first-move.json"),
        MancalaGameDocument.class
    );

    final Game expected = afterMove.toModel();

    Mockito.when(this.mancalaMongoRepository.findById(gameId)).thenReturn(Mono.just(mancalaGameDocument));
    Mockito.when(this.mancalaMongoRepository.save(Mockito.any())).thenReturn(Mono.just(afterMove));

    //when and then
    this.webTestClient.put()
        .uri("/api/v1/mancala/{gameId}/move/{position}", gameId, 0)
        .exchange()
        .expectStatus()
        .isOk()
        .expectHeader()
        .contentType(MediaType.APPLICATION_JSON)
        .expectBody(this.gameResponseType)
        .consumeWith(response -> {
              final GameResponse body = response.getResponseBody();
              assertThat(body).isNotNull()
                  .returns(expected.id(), from(GameResponse::id))
                  .returns(expected.board().getPits(), from(GameResponse::pits))
                  .returns(expected.players().players(), from(GameResponse::players))
                  .returns(expected.players().current(), from(GameResponse::current));
            }
        );

    Mockito.verify(this.mancalaMongoRepository, times(1)).findById(gameId);
    Mockito.verify(this.mancalaMongoRepository, times(1)).save(Mockito.any());
  }

  @SneakyThrows
  @Test
  void retrieve() {
    //given:
    final String gameId = "6261d18d701e87233773de98";

    final MancalaGameDocument mancalaGameDocument = this.objectMapper.readValue(
        ResourceUtils.getFile("src/test/resources/new-game.json"),
        MancalaGameDocument.class
    );
    final Game expected = mancalaGameDocument.toModel();
    Mockito.when(this.mancalaMongoRepository.findById(gameId)).thenReturn(Mono.just(mancalaGameDocument));

    //when and then
    this.webTestClient.get()
        .uri("/api/v1/mancala/{gameId}", gameId)
        .exchange()
        .expectStatus()
        .isOk()
        .expectHeader()
        .contentType(MediaType.APPLICATION_JSON)
        .expectBody(this.gameResponseType)
        .consumeWith(response -> {
              final GameResponse body = response.getResponseBody();
              assertThat(body).isNotNull()
                  .returns(expected.id(), from(GameResponse::id))
                  .returns(expected.board().getPits(), from(GameResponse::pits))
                  .returns(expected.players().players(), from(GameResponse::players))
                  .returns(expected.players().current(), from(GameResponse::current));
            }
        );

    Mockito.verify(this.mancalaMongoRepository, times(1)).findById(gameId);
  }

  @SneakyThrows
  @Test
  void dataNotFound() {
    //given:
    final String gameId = "INVALID_ID";

    Mockito.when(this.mancalaMongoRepository.findById(gameId))
        .thenThrow(new DataNotFoundException("Game not found for id: " + gameId));

    //when and then
    this.webTestClient.get()
        .uri("/api/v1/mancala/{gameId}", gameId)
        .exchange()
        .expectStatus().is4xxClientError()
        .expectHeader()
        .contentType(MediaType.APPLICATION_JSON)
        .expectBody(this.errorResponseType)
        .consumeWith(response -> {
              final ErrorResponse body = response.getResponseBody();
              assertThat(body).isNotNull()
                  .returns("Game not found for id: INVALID_ID", from(ErrorResponse::message))
                  .returns("404", from(ErrorResponse::errorCode));
            }
        );

    Mockito.verify(this.mancalaMongoRepository, times(1)).findById(gameId);
  }

  @SneakyThrows
  @Test
  void gameOverAndWinnerTest() {
    //given:
    final String gameId = "6261d18d701e87233773de98";

    final MancalaGameDocument mancalaGameDocument = this.objectMapper.readValue(
        ResourceUtils.getFile("src/test/resources/before-game-over.json"),
        MancalaGameDocument.class
    );

    final MancalaGameDocument afterMove = this.objectMapper.readValue(
        ResourceUtils.getFile("src/test/resources/after-game-over.json"),
        MancalaGameDocument.class
    );

    Mockito.when(this.mancalaMongoRepository.findById(gameId))
        .thenReturn(Mono.just(mancalaGameDocument));

    Mockito.when(this.mancalaMongoRepository.save(any()))
        .thenReturn(Mono.just(afterMove));

    final WinnerResponse expected = new WinnerResponse(new Player("Player 2", 13), 42);

    //when and then
    this.webTestClient.put()
        .uri("/api/v1/mancala/{gameId}/move/{position}", gameId, 12)
        .exchange()
        .expectStatus()
        .isOk()
        .expectHeader()
        .contentType(MediaType.APPLICATION_JSON)
        .expectBody(this.gameResponseType)
        .consumeWith(response -> {
              final GameResponse body = response.getResponseBody();
              assertThat(body).isNotNull()
                  .returns(true, from(GameResponse::isGameOver))
                  .returns(expected, from(GameResponse::winner));
            }
        );

    Mockito.verify(this.mancalaMongoRepository, times(1)).findById(gameId);
    Mockito.verify(this.mancalaMongoRepository, times(1)).save(Mockito.any());
  }

  @SneakyThrows
  @Test
  void gameOverAndWinnerTest2() {
    //given:
    final String gameId = "6261d18d701e87233773de98";

    final MancalaGameDocument mancalaGameDocument = this.objectMapper.readValue(
        ResourceUtils.getFile("src/test/resources/before-game-over-2.json"),
        MancalaGameDocument.class
    );

    final MancalaGameDocument afterMove = this.objectMapper.readValue(
        ResourceUtils.getFile("src/test/resources/after-game-over-2.json"),
        MancalaGameDocument.class
    );

    Mockito.when(this.mancalaMongoRepository.findById(gameId))
        .thenReturn(Mono.just(mancalaGameDocument));

    Mockito.when(this.mancalaMongoRepository.save(any()))
        .thenReturn(Mono.just(afterMove));

    final WinnerResponse expected = new WinnerResponse(new Player("Player 1", 6), 41);

    //when and then
    this.webTestClient.put()
        .uri("/api/v1/mancala/{gameId}/move/{position}", gameId, 2)
        .exchange()
        .expectStatus()
        .isOk()
        .expectHeader()
        .contentType(MediaType.APPLICATION_JSON)
        .expectBody(this.gameResponseType)
        .consumeWith(response -> {
              final GameResponse body = response.getResponseBody();
              assertThat(body).isNotNull()
                  .returns(true, from(GameResponse::isGameOver))
                  .returns(expected, from(GameResponse::winner));
            }
        );

    Mockito.verify(this.mancalaMongoRepository, times(1)).findById(gameId);
    Mockito.verify(this.mancalaMongoRepository, times(1)).save(Mockito.any());
  }
}