package com.bol.mancala.game;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;

import com.bol.mancala.game.exception.MancalaGameException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GameImplTest {

  public static final Player JOHN = new Player("John", 6);
  public static final Player MARY = new Player("Mary", 13);
  private Game game;

  @BeforeEach
  void setUp() {
    this.game = createGame();
  }


  private GameImpl createGame() {
    final Board newBoard = new BoardImpl();
    return new GameImpl(
        UUID.randomUUID().toString(),
        newBoard,
        new PlayersImpl(JOHN, MARY)
    );
  }

  @Test
  void createNewGame() {
    //when:
    final Board board = this.game.board();

    //then:
    assertThat(this.game.id()).isNotNull();
    verifyBoard(board, 6, 6, 6, 6, 6, 6, 0, 6, 6, 6, 6, 6, 6, 0);
  }


  @Test
  void throwExBigPitMove() {
    //when and then:
    final Exception exception = catchException(() -> this.game.play(Move.of(JOHN, 6)));

    assertThat(exception)
        .isInstanceOf(MancalaGameException.class)
        .hasMessage("Can not move from this position!");
  }

  @Test
  void throwExEmptyPitMove() {
    //when and then:
    this.game.play(Move.of(JOHN, 0));// sow

    final Exception exception = catchException(() -> this.game.play(Move.of(JOHN, 0)));

    assertThat(exception)
        .isInstanceOf(MancalaGameException.class)
        .hasMessage("Can not play with empty pit!");
  }

  @Test
  void playerJohnMakesMove() {
    //when:
    this.game.play(Move.of(JOHN, 0));

    //then:
    verifyBoard(this.game.board(), 0, 7, 7, 7, 7, 7, 1, 6, 6, 6, 6, 6, 6, 0);
  }

  @Test
  void playerJohnMakesMoveFrom2() {
    //when:
    this.game.play(Move.of(JOHN, 2));

    //then:
    verifyBoard(this.game.board(), 6, 6, 0, 7, 7, 7, 1, 7, 7, 6, 6, 6, 6, 0);
  }

  @Test
  void sameTurn() {
    //when:
    this.game.play(Move.of(JOHN, 0));

    //then:
    assertThat(this.game.players().current()).isEqualTo(JOHN);
  }

  @Test
  void throwExInvalidPlayerTurn() {
    //when:
    this.game.play(Move.of(JOHN, 2));

    //then:
    final Exception exception = catchException(() -> this.game.play(Move.of(JOHN, 3)));

    assertThat(exception)
        .isInstanceOf(MancalaGameException.class)
        .hasMessage("It's not your turn!");
  }

  @Test
  void changeTurn() {
    //when:
    this.game.play(Move.of(JOHN, 5));

    //then:
    assertThat(this.game.players().current()).isEqualTo(MARY);
  }

  @Test
  void twoMovesSamePlayer() {
    //when:
    this.game.play(
        Move.of(JOHN, 0),
        Move.of(JOHN, 1)
    );

    //then:
    verifyBoard(this.game.board(), 0, 0, 8, 8, 8, 8, 2, 7, 7, 6, 6, 6, 6, 0);
  }

  @Test
  void takeAllOppositePitStones() {
    //given:
    final Game game = new GameImpl(
        UUID.randomUUID().toString(),
        new BoardImpl(
            List.of(
                new Pit(0, 0),
                new Pit(1, 5),
                new Pit(2, 1),
                new Pit(3, 0),
                new Pit(4, 11),
                new Pit(5, 11),
                new Pit(6, 4),
                new Pit(7, 10),
                new Pit(8, 9),
                new Pit(9, 2),
                new Pit(10, 0),
                new Pit(11, 11),
                new Pit(12, 0),
                new Pit(13, 8)
            )
        ),
        new PlayersImpl(JOHN, MARY)
    );

    //when:
    game.play(Move.of(JOHN, 2));

    //then:
    verifyBoard(game.board(), 0, 5, 0, 0, 11, 11, 7, 10, 9, 0, 0, 11, 0, 8);
  }

  @Test
  void twoMoves2Player() {
    //when:
    this.game.play(
        Move.of(JOHN, 0),
        Move.of(JOHN, 1),
        Move.of(MARY, 7)
    );

    //then:
    verifyBoard(this.game.board(), 0, 0, 8, 8, 8, 8, 2, 0, 8, 7, 7, 7, 0, 9);
  }


  @Test
  void gameOverConditions() {
    //given:
    final Players players = new PlayersImpl(JOHN, MARY);
    final var game = new GameImpl(
        UUID.randomUUID().toString(),
        new BoardImpl(
            List.of(
                new Pit(0, 0),
                new Pit(1, 0),
                new Pit(2, 0),
                new Pit(3, 0),
                new Pit(4, 0),
                new Pit(5, 0),
                new Pit(6, 22),
                new Pit(7, 0),
                new Pit(8, 0),
                new Pit(9, 0),
                new Pit(10, 0),
                new Pit(11, 0),
                new Pit(12, 1),
                new Pit(13, 49)
            )
        ),
        players
    );
    players.turn();
    //when:
    game.play(Move.of(MARY, 12));

    //then:
    verifyBoard(game.board(), 0, 0, 0, 0, 0, 0, 22, 0, 0, 0, 0, 0, 0, 50);
    assertThat(game.isGameOver()).isTrue();

    //and then:
    final Exception exception = catchException(() -> game.play(Move.of(JOHN, 0)));
    assertThat(exception).isInstanceOf(MancalaGameException.class)
        .hasMessage("Game Over!");
  }

  @Test
  void oppositePitStones2() {
    //given:
    final Player john = JOHN;
    final Player mary = MARY;
    final Game game = new GameImpl(
        UUID.randomUUID().toString(),
        new BoardImpl(),
        new PlayersImpl(john, mary)
    );
    //when and then:
    game.play(Move.of(john, 1));
    verifyBoard(game.board(), 6, 0, 7, 7, 7, 7, 1, 7, 6, 6, 6, 6, 6, 0);
    assertThat(game.players().current()).isEqualTo(mary);
    //when and then:
    game.play(Move.of(mary, 8));
    verifyBoard(game.board(), 7, 0, 7, 7, 7, 7, 1, 7, 0, 7, 7, 7, 7, 1);
    assertThat(game.players().current()).isEqualTo(john);
    //when and then:
    game.play(Move.of(john, 5));
    verifyBoard(game.board(), 7, 0, 7, 7, 7, 0, 2, 8, 1, 8, 8, 8, 8, 1);
    assertThat(game.players().current()).isEqualTo(mary);

    //when and then:
    game.play(Move.of(mary, 12));
    verifyBoard(game.board(), 8, 1, 8, 8, 8, 1, 2, 9, 1, 8, 8, 8, 0, 2);
    assertThat(game.players().current()).isEqualTo(john);

    //when and then:
    game.play(Move.of(john, 2));
    verifyBoard(game.board(), 8, 1, 0, 9, 9, 2, 3, 10, 2, 9, 9, 8, 0, 2);
    assertThat(game.players().current()).isEqualTo(mary);

    //when and then:
    game.play(Move.of(mary, 8));
    verifyBoard(game.board(), 8, 1, 0, 9, 9, 2, 3, 10, 0, 10, 10, 8, 0, 2);
    assertThat(game.players().current()).isEqualTo(john);

    //when and then:
    game.play(Move.of(john, 1));
    verifyBoard(game.board(), 8, 0, 0, 9, 9, 2, 14, 10, 0, 10, 0, 8, 0, 2);
    assertThat(game.players().current()).isEqualTo(mary);

    //when and then:
    game.play(Move.of(mary, 7));
    verifyBoard(game.board(), 9, 1, 1, 10, 9, 2, 14, 0, 1, 11, 1, 9, 1, 3);
    assertThat(game.players().current()).isEqualTo(john);

    //when and then:
    game.play(Move.of(john, 0));
    verifyBoard(game.board(), 0, 2, 2, 11, 10, 3, 15, 1, 2, 12, 1, 9, 1, 3);
    assertThat(game.players().current()).isEqualTo(mary);

    //when and then:
    game.play(Move.of(mary, 8));
    verifyBoard(game.board(), 0, 2, 2, 11, 10, 3, 15, 1, 0, 13, 2, 9, 1, 3);
    assertThat(game.players().current()).isEqualTo(john);

    //when and then:
    game.play(Move.of(john, 1));
    verifyBoard(game.board(), 0, 0, 3, 12, 10, 3, 15, 1, 0, 13, 2, 9, 1, 3);
    assertThat(game.players().current()).isEqualTo(mary);

    //when and then:
    game.play(Move.of(mary, 7));
    verifyBoard(game.board(), 0, 0, 3, 12, 0, 3, 15, 0, 0, 13, 2, 9, 1, 14);
    assertThat(game.players().current()).isEqualTo(john);

  }

  @Test
  void oppositePitTest() {
    //given:

    final Game game = new GameImpl(
        UUID.randomUUID().toString(),
        new BoardImpl(List.of(
            new Pit(0, 0),
            new Pit(1, 1),
            new Pit(2, 0),
            new Pit(3, 10),
            new Pit(4, 10),
            new Pit(5, 2),
            new Pit(6, 4),
            new Pit(7, 2),
            new Pit(8, 10),
            new Pit(9, 0),
            new Pit(10, 4),
            new Pit(11, 2),
            new Pit(12, 12),
            new Pit(13, 3)
        )),
        new PlayersImpl(JOHN, MARY)
    );
    //when and then:
    game.play(Move.of(JOHN, 3));
    verifyBoard(game.board(), 0, 1, 0, 0, 11, 3, 19, 3, 11, 1, 5, 3, 0, 3);
  }

  private void verifyBoard(final Board board, final Integer... pitStones) {
    assertThat(board.size()).isEqualTo(pitStones.length);
    final List<Integer> actualStones = board.getPits().stream().map(Pit::getStones).toList();
    final List<Integer> expectedStones = Arrays.stream(pitStones).toList();
    assertThat(actualStones).isEqualTo(expectedStones);
  }
}
