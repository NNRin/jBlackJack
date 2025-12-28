package com.github.nnrin.blackjackweb.Singleplayer.web;

import com.github.NNRIN.Components.interfaces.ISingePlayerGameManager;
import com.github.nnrin.blackjackweb.Singleplayer.SingleplayerManagement;
import com.github.nnrin.blackjackweb.Singleplayer.web.DTOs.ActionDTO;
import com.github.nnrin.blackjackweb.Singleplayer.web.DTOs.BetDTO;
import com.github.nnrin.blackjackweb.Singleplayer.web.DTOs.SpBlackJackDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/sp/blackjack")
public class SingleplayerController {
    private final SingleplayerManagement singleplayerManagement;
    private final SpBlackJackMapper spBlackJackMapper;

    public SingleplayerController(SingleplayerManagement singleplayerManagement, SpBlackJackMapper spBlackJackMapper) {
        this.singleplayerManagement = singleplayerManagement;
        this.spBlackJackMapper = spBlackJackMapper;
    }

    @PostMapping
    public ResponseEntity<SpBlackJackDTO> createGame() {

        ISingePlayerGameManager game = singleplayerManagement.createGame();

        return ResponseEntity.ok(spBlackJackMapper.toSpBlackJackDto(game));
    }

    @PostMapping("/{id}/action")
    public ResponseEntity<SpBlackJackDTO> takeActionOnGame(
            @PathVariable("id") String id,
            @RequestBody ActionDTO actionDTO
    ) {
        ISingePlayerGameManager game = singleplayerManagement.takeActionOnGame(
                spBlackJackMapper.toActionEnum(actionDTO), id
        );

        return ResponseEntity.ok(spBlackJackMapper.toSpBlackJackDto(game));
    }

    @PostMapping("/{id}/bet")
    public ResponseEntity<SpBlackJackDTO> placeBet(@PathVariable("id") String id, @RequestBody BetDTO betDTO) {
        ISingePlayerGameManager game = singleplayerManagement.placeBet(betDTO.bet(), id);
        return ResponseEntity.ok(spBlackJackMapper.toSpBlackJackDto(game));
    }
}
