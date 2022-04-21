package com.bol.mancala.game;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.Test;

class PlayersImplTest {

  @Test
  void turnDoubleEndedFor2Player() {
    final var players = new PlayersImpl(
        new Player("John"),
        new Player("Mary")
    );
    assertThat(players.current()).isEqualTo(new Player("John"));
    players.turn();
    assertThat(players.current()).isEqualTo(new Player("Mary"));
    players.turn();
    assertThat(players.current()).isEqualTo(new Player("John"));
    players.turn();
    assertThat(players.current()).isEqualTo(new Player("Mary"));
  }

  @Test
  void turnDoubleEndedFor4Player() {
    final var players = new PlayersImpl(
        new Player("habip"),
        new Player("hakan"),
        new Player("John"),
        new Player("Mary")
    );
    assertThat(players.current()).isEqualTo(new Player("habip"));
    players.turn();
    players.turn();
    players.turn();
    assertThat(players.current()).isEqualTo(new Player("Mary"));
    players.turn();
    assertThat(players.current()).isEqualTo(new Player("habip"));
  }

}
