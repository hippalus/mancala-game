package com.bol.mancala.infra.adapter.data.mongo.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WinnerDocument {

  private PlayerDocument winner;
  private int score;

}
