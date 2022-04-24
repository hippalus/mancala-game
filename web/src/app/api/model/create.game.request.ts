import {Player} from './player';


export interface CreateGameRequest {
  stoneAmount?: number;
  pitAmount?: number;
  firstPlayer?: Player;
  secondPlayer?: Player;
}

