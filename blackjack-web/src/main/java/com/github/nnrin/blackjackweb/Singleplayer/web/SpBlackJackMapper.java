package com.github.nnrin.blackjackweb.Singleplayer.web;
import com.github.NNRIN.Cards.Card;
import com.github.NNRIN.Components.interfaces.IHand;
import com.github.NNRIN.Components.interfaces.ISingePlayerGameManager;
import com.github.NNRIN.Helper.Actions;
import com.github.NNRIN.Participants.interfaces.IDealer;
import com.github.NNRIN.Participants.interfaces.IPlayer;
import com.github.nnrin.blackjackweb.Singleplayer.web.DTOs.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SpBlackJackMapper {
    @Mapping(target = "status", source = "status")
    HandDTO toHandDto(IHand hand);

    @Mapping(target = "suit", source = "suit")
    @Mapping(target = "faceValue", source = "facevalue")
    CardDTO toCardDto(Card card);

    @Mapping(target="isHiddenHand", source="hiddenHand")
    DealerDTO toDealerDto(IDealer dealer);

    @Mapping(target="hands", source="hand")
    @Mapping(target = "isSurrenderAvailable", source = "surrenderAvailable")
    @Mapping(target = "isInsuranceBought", source = "insuranceBought")
    @Mapping(target = "isInsuranceWon", source = "insuranceWon")
    @Mapping(target = "isSplitAvailable", source= "splitAvailable")
    @Mapping(target = "isDoubleDownAvailable", source= "doubleDownAvailable")
    PlayerDTO toPlayerDto(IPlayer player);

    @Mapping(target = "wasStackReshuffled", expression = "java(gameManager.wasStackReshuffled())")
    @Mapping(target="gameState", source="gameState")
    SpBlackJackDTO toSpBlackJackDto(ISingePlayerGameManager gameManager);

    default Actions toActionEnum(ActionDTO actionDTO) {
        if (actionDTO == null || actionDTO.action() == null) {
            return null;
        }

        try {
            return Actions.valueOf(actionDTO.action());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid action: " + actionDTO.action());
        }
    }

}
