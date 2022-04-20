package com.bol.mancala.game;

import java.util.ArrayList;
import java.util.List;

public final class BoardImpl implements Board {

  private static final int DEFAULT_STONE_AMOUNT = 6;
  private static final int DEFAULT_PIT_AMOUNT = 14;

  private final List<Pit> pits;

  public BoardImpl() {
    this(DEFAULT_STONE_AMOUNT, DEFAULT_PIT_AMOUNT);
  }

  public BoardImpl(final int stoneAmount, final int pitAmount) {
    this.pits = new ArrayList<>();
    final int firstBigPitPosition = pitAmount / 2 - 1;
    final int secondBigPitPosition = pitAmount - 1;
    for (int position = 0; position < pitAmount; position++) {
      if (position == firstBigPitPosition || position == secondBigPitPosition) {
        Pit bigPit = new Pit(position, 0);
        this.pits.add(bigPit);
      } else {
        Pit pit = new Pit(position, stoneAmount);
        this.pits.add(pit);
      }
    }
  }

  @Override
  public int size() {
    return this.pits.size();
  }

  @Override
  public Pit getPit(final int position) {
    return this.pits.get(position % this.size());
  }

  @Override
  public boolean isBigPit(final int position) {
    return position == firstBigPitPosition() || position == secondBigPitPosition();
  }

  @Override
  public Pit getOppositePit(int position) {
    return this.getPit(this.getOppositeIndex(position));
  }

  private int getOppositeIndex(final int position) {
    return (this.size() - position - 2) % this.size();
  }

  @Override
  public Pit getBigPit(int position) {
    if (position < this.size() / 2) {
      return this.getPit(firstBigPitPosition());
    }
    return this.getPit(secondBigPitPosition());
  }

  private int firstBigPitPosition() {
    return this.size() / 2 - 1;
  }

  private int secondBigPitPosition() {
    return this.size() - 1;
  }

  @Override
  public Pit getOppositeBigPit(int position) {
    return this.getBigPit(this.getOppositeIndex(position));
  }

  @Override
  public List<Pit> getPitsOnSide(int position) {
    if (position < this.size() / 2) {
      return this.pits.subList(0, firstBigPitPosition());
    }
    return this.pits.subList(this.size() / 2, secondBigPitPosition());

  }

  @Override
  public List<Pit> getPitsOnOppositeSide(int position) {
    return this.getPitsOnSide(this.getOppositeIndex(position));
  }
}
