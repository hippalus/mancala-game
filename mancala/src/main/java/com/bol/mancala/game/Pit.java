package com.bol.mancala.game;

import java.util.concurrent.atomic.AtomicInteger;

public record Pit(int position, AtomicInteger stones) {

  public Pit(final int position, final int stones) {
    this(position, new AtomicInteger(stones));
  }

  public int getStones() {
    return this.stones.get();
  }

  public boolean isEmpty() {
    return this.getStones() == 0;
  }

  public void putStone() {
    this.stones.incrementAndGet();
  }

  public void takeAllStones() {
    this.stones().set(0);
  }

  public void putStoneFrom(final Pit pit) {
    final int stones = pit.getStones();
    pit.takeAllStones();
    this.stones.set(this.getStones() + stones);
  }
}
