import {Playcard} from "./components/Playcard.js";
import {CardMapping} from "./components/CardMapping.js";
import {CardHelper} from "./components/CardHelper.js";

export default class Model {
    state = {
        id: null,
        gameState: "WaitingForBet",
        wasStackReshuffled: false,
        player: {
            name: "You",
            credit: 0,
            isSurrenderAvailable: false,
            isInsuranceBought: false,
            hands: [],
            isSplitAvailable: false,
            isDoubleDownAvailable: false,
        },
        dealer: {
            hand: { cards: [] },
            isHiddenHand: true
        }
    };

    BASE_URL = '/sp/blackjack';
    constructor() {
        this.initialCreditAmount = 0;
    }

    bindRenderDealerCards(callback) { this.renderDealerCards = callback;}
    bindRenderPlayerHands(callback) { this.renderPlayerHands = callback; }
    bindUpdateGameInfo(callback) { this.updateGameInfo = callback; }
    bindShowMessage(callback){this.showMessage = callback; }

    async createGame() {
        this.post(this.BASE_URL);
    }

    async placeBet(amount) {
        if (!this.state.id) return;
        this.post(`${this.BASE_URL}/${this.state.id}/bet`, { bet: parseFloat(amount) });
    }

    async sendAction(actionName) {
        if (!this.state.id) return;
        this.post(`${this.BASE_URL}/${this.state.id}/action`, { action: actionName });
    }

    async post(url, body = null) {
        try {
            const options = {
                method: 'POST',
                headers: {'Content-Type': 'application/json'}
            };
            if (body) options.body = JSON.stringify(body);

            const response = await fetch(url, options);
            const data = await response.json();
            this.syncState(data);
        } catch (e) { console.error("API Error:", e); }
    }

    // handle state logic
    syncState(data) {
        this.state = data;

        if (data.wasStackReshuffled) {
            if(this.showMessage) this.showMessage("Deck was reshuffled!");
            // todo: animate
        }

        let displayDealerCards = [];
        if (data.dealer.hand && data.dealer.hand.cards) {
            displayDealerCards = data.dealer.hand.cards.map(c => new Playcard(c.suit, c.faceValue));
            if(data.dealer.isHiddenHand && data.gameState !== 'RoundOver' && displayDealerCards.length > 0) {
                displayDealerCards.push(new Playcard('back', 'back'));
            }
        }

        let dealerHandValue = CardHelper.getHandValue(data.dealer.hand);

        const dealerData = {
            cards: displayDealerCards,
            gameState: data.gameState,
            handValue: dealerHandValue,
        };

        this.renderDealerCards(dealerData);


        const displayHands = data.player.hands.map(hand => {
            return {
                cards: hand.cards.map(c => new Playcard(c.suit, c.faceValue)),
                status: hand.status,
                bet: hand.bet,
                handValue: CardHelper.getHandValue(hand),
            };
        });

        this.renderPlayerHands(displayHands);

        // Parse data from response to properly display in frontend
        if (data.gameState === 'WaitingForBet') {
            this.initialCreditAmount = data.player.credit;
        }

        let calculatedCreditToDisplay = data.player.credit;
        let roundWinningsLocal = 0;
        if (data.gameState === 'RoundOver') {
            roundWinningsLocal = data.player.credit - this.initialCreditAmount;
            calculatedCreditToDisplay = this.initialCreditAmount;
        }

        if (roundWinningsLocal <= 0) {
            calculatedCreditToDisplay = data.player.credit;
        }

        this.updateGameInfo({
            credit: calculatedCreditToDisplay,
            roundWinnings: roundWinningsLocal,
            status: data.gameState,
            surrenderAvailable: data.player.isSurrenderAvailable,
            splitAvailable: data.player.isSplitAvailable,
            doubleDownAvailable: data.player.isDoubleDownAvailable,
            insuranceBought: data.player.isInsuranceBought,
            insuranceBet: data.player.insuranceBet,
            gameActive: !!data.id
        });
    }
}