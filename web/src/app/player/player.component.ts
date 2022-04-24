import {Component} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {MatSnackBar} from "@angular/material/snack-bar";
import {Router} from "@angular/router";
import {GameClient} from "../api/game.client";
import {Player} from "../api/model/player";
import {CreateGameRequest} from "../api/model/create.game.request";
import {catchError, throwError} from "rxjs";
import {Game} from "../api/model/game";
import {HttpErrorResponse} from "@angular/common/http";

@Component({
  selector: 'app-player',
  templateUrl: './player.component.html',
  styleUrls: ['./player.component.scss']
})
export class PlayerComponent {
  playerForm: FormGroup;

  constructor(
    private readonly gameClient: GameClient,
    private readonly formBuilder: FormBuilder,
    private readonly snackbar: MatSnackBar,
    private readonly router: Router,
  ) {
    this.playerForm = formBuilder.group({
      player1: ['', Validators.required],
      player2: ['', Validators.required],
    });
  }

  createGame() {
    if (this.playerForm.valid) {
      const firstPlayer: Player = {
        name: this.playerForm.controls['player1'].value,
        bigPitPosition: 6 //TODO: calculate by pitAmount
      };
      const secondPlayer: Player = {
        name: this.playerForm.controls['player2'].value,
        bigPitPosition: 13 //TODO: calculate by pitAmount
      };
      const createGameRequest: CreateGameRequest = {
        stoneAmount: 6, //TODO: Get from user form
        pitAmount: 14,//TODO: Get from user form
        firstPlayer: firstPlayer,
        secondPlayer: secondPlayer
      };

      this.gameClient.create(createGameRequest)
      .pipe(catchError(err => this.handleError(err)))
      .subscribe((game: Game) => {
        if ("id" in game) {
          sessionStorage.setItem("gameId", <string>game.id);
        }
        this.router.navigate(['game']).then(r => r);
      });
    }

  }

  private handleError(error: HttpErrorResponse) {
    this.snackbar.open(error.error.message, "Ok", {duration: 2000});
    console.error(`Backend returned code ${error.status}, body was: `, error.error);
    return throwError(() => new Error('Something bad happened; please try again later.'));
  }
}
