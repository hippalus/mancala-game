package com.bol.mancala.game;

public interface Game {

  String id();

  Game play(Move move);

  void play(Move... moves);

  Board board();

  boolean isGameOver();

  Players players();

}