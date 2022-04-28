import {Pit} from "./pit";
import {Player} from "./player";
import {Winner} from "./winner";

export interface Game {
  id?: string;
  pits?: Array<Pit>;
  players?: Array<Player>;
  current?: Player;
  isGameOver?: boolean,
  winner?: Winner
}

