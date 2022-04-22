package com.bol.mancala.game;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public record Pit(int position, AtomicInteger stones) {

  public Pit(final int position, final int stones) {
    this(position, new AtomicInteger(stones));
  }

  public int getStones() {
    return this.stones.get();
  }

  @JsonIgnore
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

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final Pit pit = (Pit) o;
    return this.position == pit.position && Objects.equals(this.stones.get(), pit.stones.get());
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.position, this.stones.get());
  }
}
