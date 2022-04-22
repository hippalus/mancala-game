package com.bol.mancala.game;

import java.util.List;

public interface Players {

  Player current();

  void turn();

  List<Player> players();
}

