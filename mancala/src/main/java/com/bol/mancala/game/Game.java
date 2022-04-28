package com.bol.mancala.game;

import java.util.Optional;

public interface Game {

  String id();

  Game play(Move move);

  void play(Move... moves);

  Board board();

  boolean isGameOver();

  Players players();

  Optional<Winner> winner();

}