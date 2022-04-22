package com.bol.mancala.game;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class PlayersImpl implements Players {

  private final Queue<Player> players;

  public PlayersImpl(final Player... players) {
    this(new ArrayDeque<>(List.of(players)));
  }

  @Override
  public Player current() {
    return this.players.peek();
  }

  // FIFO
  @Override
  public void turn() {
    this.players.add(this.players.remove());
  }

  @Override
  public List<Player> players() {
    return this.players.stream().toList();
  }
}
