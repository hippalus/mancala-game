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
}
