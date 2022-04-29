package com.bol.mancala.infra.adapter.data.mongo.document;

import com.bol.mancala.game.BoardImpl;
import com.bol.mancala.game.Game;
import com.bol.mancala.game.GameImpl;
import com.bol.mancala.game.PlayersImpl;
import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "mancala_game")
public class MancalaGameDocument {
  @Id
  private String id;
  private List<PitDocument> pits;
  private List<PlayerDocument> players;
  private boolean gameOver;
  private WinnerDocument winner;
  @CreatedDate
  private LocalDateTime createdAt;
  @LastModifiedDate
  private LocalDateTime updatedAt;

  public Game toModel() {
    return new GameImpl(
        this.getId(),
        new BoardImpl(this.getPits().stream().map(PitDocument::toModel).toList()),
        new PlayersImpl(new ArrayDeque<>(this.getPlayers().stream().map(PlayerDocument::toModel).toList())),
        new AtomicBoolean(this.isGameOver())
    );
  }
}
