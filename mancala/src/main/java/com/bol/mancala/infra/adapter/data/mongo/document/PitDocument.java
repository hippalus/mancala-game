package com.bol.mancala.infra.adapter.data.mongo.document;

import com.bol.mancala.game.Pit;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PitDocument {

  private int position;
  private int stones;

  public Pit toModel() {
    return new Pit(this.getPosition(), this.getStones());
  }
}
