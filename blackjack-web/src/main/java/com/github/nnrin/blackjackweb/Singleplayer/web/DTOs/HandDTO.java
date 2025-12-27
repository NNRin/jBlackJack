package com.github.nnrin.blackjackweb.Singleplayer.web.DTOs;

import com.github.NNRIN.Cards.Card;
import com.github.NNRIN.Helper.ParticipantStates;

import java.util.List;

public record HandDTO(
        double bet,
        String status, // convert from enum
        List<CardDTO> cards
) {
}
