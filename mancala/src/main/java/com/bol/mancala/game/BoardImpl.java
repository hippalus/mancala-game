package com.bol.mancala.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class BoardImpl implements Board {

  private static final int DEFAULT_STONE_AMOUNT = 6;
  private static final int DEFAULT_PIT_AMOUNT = 14;

  private final List<Pit> pits;

  public BoardImpl() {
    this(DEFAULT_STONE_AMOUNT, DEFAULT_PIT_AMOUNT);
  }

  public BoardImpl(final int stoneAmount, final int pitAmount) {
    this(initPits(stoneAmount, pitAmount));
  }

  private static List<Pit> initPits(final int stoneAmount, final int pitAmount) {
    final List<Pit> pits = new ArrayList<>(pitAmount);
    final int firstBigPitPosition = pitAmount / 2 - 1;
    final int secondBigPitPosition = pitAmount - 1;
    for (int position = 0; position < pitAmount; position++) {
      if (position == firstBigPitPosition || position == secondBigPitPosition) {
        final Pit bigPit = new Pit(position, 0);
        pits.add(bigPit);
      } else {
        final Pit pit = new Pit(position, stoneAmount);
        pits.add(pit);
      }
    }
    return pits;
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
    return position == this.firstBigPitPosition() || position == this.secondBigPitPosition();
  }

  @Override
  public Pit getOppositePit(final int position) {
    return this.getPit(this.getOppositeIndex(position));
  }

  private int getOppositeIndex(final int position) {
    return (this.size() - position - 2) % this.size();
  }

  @Override
  public Pit getBigPit(final int position) {
    if (position < this.size() / 2) {
      return this.firstBigPit();
    }
    return this.secondBigPit();
  }

  @Override
  public Pit getOppositeBigPit(final int position) {
    return this.getBigPit(this.getOppositeIndex(position));
  }

  @Override
  public List<Pit> getPitsOnSide(final int position) {
    if (position < this.size() / 2) {
      return this.pits.subList(0, this.firstBigPitPosition());
    }
    return this.pits.subList(this.size() / 2, this.secondBigPitPosition());
  }

  @Override
  public List<Pit> getPitsOnOppositeSide(final int position) {
    return this.getPitsOnSide(this.getOppositeIndex(position));
  }

  @Override
  public List<Pit> getPits() {
    return Collections.unmodifiableList(this.pits);
  }
}
