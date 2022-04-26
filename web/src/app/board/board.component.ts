import {Component} from '@angular/core';
import {Game} from "../api/model/game";
import {GameClient} from "../api/game.client";
import {MatSnackBar} from "@angular/material/snack-bar";
import {catchError, throwError} from "rxjs";
import {HttpErrorResponse} from "@angular/common/http";
import {Pit} from "../api/model/pit";

@Component({
  selector: 'app-board',
  templateUrl: './board.component.html',
  styleUrls: ['./board.component.scss']
})
export class BoardComponent {
  game?: Game;

  constructor(
    private readonly gameClient: GameClient,
    private readonly snackbar: MatSnackBar,
  ) {
    this.loadBoard();
  }

  private loadBoard() {
    const gameId = sessionStorage.getItem("gameId");
    if (gameId) {
      this.gameClient.retrieve(gameId)
      .pipe(catchError(err => this.handleError(err)))
      .subscribe(game => this.game = game);
    } else {
      //TODO: route to first page
    }
  }

  get board(): Array<Pit> | undefined {
    if (!(this.game) || this.game.pits) {
      return this.game?.pits;
    }
    return new Array<Pit>();
  }

  move(index: number) {
    const gameId = sessionStorage.getItem("gameId");
    if (gameId) {
      this.gameClient.play(gameId, index)
      .pipe(catchError(err => this.handleError(err)))
      .subscribe(game => this.game = game);
    } else {
      //TODO: route to first page
    }
  }

  private handleError(error: HttpErrorResponse) {
    this.snackbar.open(error.error.message, "Ok", {duration: 2000});
    console.error(`Backend returned code ${error.status}, body was: `, error.error);
    return throwError(() => new Error('Something bad happened; please try again later.'));
  }
}
