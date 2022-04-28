package com.bol.mancala.infra.adapter.rest.dto.response;

import com.bol.mancala.game.Player;

public record WinnerResponse(Player winner, int score) {

}
