import {Pit} from "./pit";
import {Player} from "./player";

export interface Game {
  id?: string;
  pits?: Array<Pit>;
  players?: Array<Player>;
  current?: Player;
}

