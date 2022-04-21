package com.bol.mancala.game;

public record Move(Player player, int position) {

  public static Move of(final Player player, final int position) {
    return new Move(player, position);
  }

}
