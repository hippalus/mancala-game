package com.bol.mancala.infra.adapter.rest;

import com.bol.mancala.game.port.MancalaGamePort;
import com.bol.mancala.infra.adapter.rest.dto.request.CreateGameRequest;
import com.bol.mancala.infra.adapter.rest.dto.response.GameResponse;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/mancala")
public class MancalaController {

  private final MancalaGamePort mancalaGamePort;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Mono<GameResponse> create(@Valid @RequestBody final CreateGameRequest request) {
    return this.mancalaGamePort.create(request.toGameOptions()).map(GameResponse::fromModel);
  }

  @PutMapping("/{gameId}/move/{position}")
  @ResponseStatus(HttpStatus.OK)
  public Mono<GameResponse> play(
      @PathVariable(name = "gameId") final String gameId,
      @PathVariable(name = "position") final int position) {
    return this.mancalaGamePort.play(gameId, position).map(GameResponse::fromModel);
  }

  @GetMapping("/{gameId}")
  @ResponseStatus(HttpStatus.OK)
  public Mono<GameResponse> retrieve(@PathVariable(name = "gameId") final String gameId) {
    return this.mancalaGamePort.retrieve(gameId).map(GameResponse::fromModel);
  }

}
