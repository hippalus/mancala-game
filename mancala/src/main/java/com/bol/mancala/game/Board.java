package com.bol.mancala.game;

import java.util.List;

public interface Board {

  int size();

  Pit getPit(int position);

  boolean isBigPit(int position);

  Pit getOppositePit(int position);

  Pit getBigPit(int position);

  Pit getOppositeBigPit(int position);

  List<Pit> getPitsOnSide(int position);

  List<Pit> getPitsOnOppositeSide(int position);

  List<Pit> getPits();

  default int firstBigPitPosition() {
    return this.size() / 2 - 1;
  }

  default int secondBigPitPosition() {
    return this.size() - 1;
  }
}
