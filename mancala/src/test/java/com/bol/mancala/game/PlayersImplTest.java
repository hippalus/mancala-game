package com.bol.mancala.game;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

class PlayersImplTest {

  public static final Player JOHN = new Player("John", 6);
  public static final Player MARY = new Player("Mary", 13);

  @Test
  void turnDoubleEndedFor2Player() {
    final Players players = new PlayersImpl(
        JOHN,
        MARY
    );
    assertThat(players.current()).isEqualTo(JOHN);
    players.turn();
    assertThat(players.current()).isEqualTo(MARY);
    players.turn();
    assertThat(players.current()).isEqualTo(JOHN);
    players.turn();
    assertThat(players.current()).isEqualTo(MARY);
  }

  @Test
  void turnDoubleEndedFor4Player() {
    final Players players = new PlayersImpl(
        new Player("habip", 6),
        new Player("hakan", 13),
        JOHN,
        MARY
    );
    assertThat(players.current()).isEqualTo(new Player("habip", 6));
    players.turn();
    players.turn();
    players.turn();
    assertThat(players.current()).isEqualTo(MARY);
    players.turn();
    assertThat(players.current()).isEqualTo(new Player("habip", 6));
  }

  @Test
  void collectToListOrdered() {
    final Players players = new PlayersImpl(
        new Player("habip", 6),
        new Player("hakan", 13),
        JOHN,
        MARY
    );
    assertThat(players.players())
        .isEqualTo(
            List.of(
                new Player("habip", 6),
                new Player("hakan", 13),
                JOHN,
                MARY)
        );

    players.turn();
    players.turn();
    players.turn();

    assertThat(players.players())
        .isEqualTo(
            List.of(
                MARY,
                new Player("habip", 6),
                new Player("hakan", 13),
                JOHN)
        );

    players.turn();

    assertThat(players.players())
        .isEqualTo(
            List.of(
                new Player("habip", 6),
                new Player("hakan", 13),
                JOHN,
                MARY)
        );
  }


}
