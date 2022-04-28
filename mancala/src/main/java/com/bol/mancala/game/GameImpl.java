package com.bol.mancala.game;

import com.bol.mancala.game.exception.MancalaGameException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class GameImpl implements Game {

  private final String id;
  private final Board board;
  private final Players players;
  private final AtomicBoolean isGameOver;

  public GameImpl(final String id, final Board board, final Players players) {
    this(id, board, players, new AtomicBoolean(false));
  }

  @Override
  public String id() {
    return this.id;
  }

  @Override
  public Game play(final Move move) {
    this.checkMoveConditions(move);
    final int position = move.position();

    final Pit selected = this.board().getPit(position);
    final int selectedStones = selected.getStones();
    selected.takeAllStones();

    final int targetPositionOfMove = this.targetPosition(position, selectedStones);
    final Pit last = this.board().getPit(targetPositionOfMove);
    final boolean lastInEmptyPit = last.isEmpty();

    for (int i = 1; i <= selectedStones; i++) {
      this.board().getPit(this.targetPosition(position, i)).putStone();
    }

    final Pit bigPit = this.board().getBigPit(position);

    if (!this.board().isBigPit(targetPositionOfMove)) {
      this.players().turn();
      if (lastInEmptyPit) {
        final Pit oppositePit = this.board().getOppositePit(targetPositionOfMove);
        if (!oppositePit.isEmpty()) {
          bigPit.putStoneFrom(last);
          bigPit.putStoneFrom(oppositePit);
        }
      }
    }

    this.checkGameOver(position);
    return this;
  }

  private void checkMoveConditions(final Move move) {
    if (this.isGameOver()) {
      throw new MancalaGameException("Game Over!");
    }

    if (this.board.isBigPit(move.position())) {
      throw new MancalaGameException("Can not move from this position!");
    }

    if (this.board.getPit(move.position()).isEmpty()) {
      throw new MancalaGameException("Can not play with empty pit!");
    }

    final Pit bigPitOfMover = this.board.getBigPit(move.position());
    final int realBigPitOfMover = move.player().bigPitPosition();

    if (!this.players.current().equals(move.player()) || bigPitOfMover.position() != realBigPitOfMover) {
      throw new MancalaGameException("It's not your turn!");
    }
  }

  private int targetPosition(final int position, final int stones) {
    final int target = (position + stones) % this.board.size();
    if (target == this.board.getOppositeBigPit(position).position()) {
      return (target + 1) % this.board.size();
    }
    return target;
  }


  private void checkGameOver(final int position) {
    if (this.getTotalStonesOnSide(position) == 0) {
      this.board.getPitsOnOppositeSide(position).forEach(this.board.getOppositeBigPit(position)::putStoneFrom);
      this.isGameOver.set(true);
    }
  }

  private int getTotalStonesOnSide(final int position) {
    final List<Pit> pitsOnSide = this.board.getPitsOnSide(position);
    return pitsOnSide.stream().map(Pit::getStones).reduce(Integer::sum).orElse(0);
  }

  @Override
  public void play(final Move... moves) {
    Arrays.stream(moves).forEach(this::play);
  }

  @Override
  public Board board() {
    return this.board;
  }

  @Override
  public boolean isGameOver() {
    return this.isGameOver.get();
  }

  @Override
  public Players players() {
    return this.players;
  }

  @Override
  public Optional<Winner> winner() {
    if (!this.isGameOver()) {
      return Optional.empty();
    }

    final Pit firstBigPit = this.board().firstBigPit();
    final Pit secondBigPit = this.board().secondBigPit();
    final Pit maxScoreBigPit = firstBigPit.getStones() > secondBigPit.getStones() ? firstBigPit : secondBigPit;

    return this.players().players().stream()
        .filter(player -> player.bigPitPosition() == maxScoreBigPit.position())
        .map(player -> new Winner(player, maxScoreBigPit.getStones()))
        .findFirst();
  }

}
