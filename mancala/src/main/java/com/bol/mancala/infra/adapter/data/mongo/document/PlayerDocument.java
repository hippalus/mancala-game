package com.bol.mancala.infra.adapter.data.mongo.document;

import com.bol.mancala.game.Player;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayerDocument {

  private String name;
  private int bigPitPosition;

  public Player toModel() {
    return new Player(this.getName(), this.getBigPitPosition());
  }
}
