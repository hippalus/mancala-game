package com.bol.mancala.game;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class BoardImplTest {

  @Test
  void testBoardSize() {
    final Board board = new BoardImpl();
    assertThat(board.size()).isEqualTo(14);
  }

  @ParameterizedTest
  @MethodSource("oppositePairParameters")
  void testOppositePit(final int position1, final int position2) {
    //given:
    final Board board = new BoardImpl();
    //when and then
    assertThat(board.getOppositePit(position1)).isEqualTo(board.getPit(position2));
    assertThat(board.getOppositePit(position2)).isEqualTo(board.getPit(position1));
  }

  private static Stream<Arguments> oppositePairParameters() {
    return Stream.of(
        Arguments.of(0, 12),
        Arguments.of(1, 11),
        Arguments.of(2, 10),
        Arguments.of(3, 9),
        Arguments.of(4, 8),
        Arguments.of(5, 7)
    );
  }


  @Test
  void testGetPitsOnFirstSideByPosition() {
    //given:
    final Board board = new BoardImpl();
    //when:
    List<Pit> pitsOnSideFirst = board.getPitsOnSide(0);
    //then:
    assertThat(pitsOnSideFirst)
        .contains(
            board.getPit(0),
            board.getPit(1),
            board.getPit(2),
            board.getPit(3),
            board.getPit(4),
            board.getPit(5)
        );
  }

  @Test
  void testGetPitsOnSecondSideByPosition() {
    //given:
    final Board board = new BoardImpl();
    //when:
    List<Pit> pitsOnSecondSide = board.getPitsOnSide(8);
    //then:
    assertThat(pitsOnSecondSide)
        .contains(
            board.getPit(7),
            board.getPit(8),
            board.getPit(9),
            board.getPit(10),
            board.getPit(11),
            board.getPit(12)
        );
  }

  @Test
  void testOwnBigPitByPosition() {
    //given:
    final var board = new BoardImpl();
    //when:
    Pit bigPitOfFirstSide = board.getBigPit(0);
    Pit bigPitOfSecond = board.getBigPit(8);
    //then:
    assertThat(bigPitOfFirstSide).isEqualTo(board.getPit(6));
    assertThat(bigPitOfSecond).isEqualTo(board.getPit(13));
  }


}
