import {Playcard} from "./components/Playcard.js";
import {CardMapping} from "./components/CardMapping.js"; // Don't forget the mapping file from step 1

export default class Model {
    // Current state of the game
    state = {
        id: null,
        gameState: "WaitingForBet",
        wasStackReshuffled: false,
        player: {
            name: "You",
            credit: 0,
            isSurrenderAvailable: false,
            isInsuranceBought: false,
            hands: []
        },
        dealer: {
            hand: { cards: [] },
            isHiddenHand: true
        }
    };

    BASE_URL = 'http://localhost:8080/sp/blackjack';

    constructor() {}

    bindRenderDealerCards(callback) { this.renderDealerCards = callback; }
    bindRenderPlayerHands(callback) { this.renderPlayerHands = callback; } // Renamed to Hands (plural)
    bindUpdateGameInfo(callback) { this.updateGameInfo = callback; }
    bindShowMessage(callback) { this.showMessage = callback; } // New: for reshuffle/errors

    // --- API Interactions (Same as before) ---
    async createGame() { /* ... implementation from previous step ... */
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

    // Helper for fetch calls to reduce redundancy
    async post(url, body = null) {
        try {
            const options = {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' }
            };
            if (body) options.body = JSON.stringify(body);

            const response = await fetch(url, options);
            const data = await response.json();
            this.syncState(data);
        } catch (e) { console.error("API Error:", e); }
    }

    // --- State Logic ---
    syncState(data) {
        this.state = data;

        // 1. Reshuffle Notification
        if (data.wasStackReshuffled) {
            if(this.showMessage) this.showMessage("Deck was reshuffled!");
            // Note: In a real app, you might want to reset this flag locally or wait for next response
        }

        // 2. Dealer Cards
        let displayDealerCards = [];
        if (data.dealer.hand && data.dealer.hand.cards) {
            displayDealerCards = data.dealer.hand.cards.map(c => new Playcard(c.suit, c.faceValue));
            if (data.dealer.isHiddenHand && data.gameState !== 'RoundOver' && displayDealerCards.length > 0) {
                displayDealerCards.push(new Playcard('back', 'back'));
            }
        }
        this.renderDealerCards(displayDealerCards);

        // 3. Player Hands (Handle Splits) [cite: 32]
        // Map over the array of hands.
        const displayHands = data.player.hands.map(hand => {
            return {
                cards: hand.cards.map(c => new Playcard(c.suit, c.faceValue)),
                status: hand.status,
                bet: hand.bet
            };
        });
        this.renderPlayerHands(displayHands);

        // 4. Update UI Info
        this.updateGameInfo({
            credit: data.player.credit,
            status: data.gameState,
            surrenderAvailable: data.player.isSurrenderAvailable, // [cite: 51]
            insuranceBought: data.player.isInsuranceBought,       // [cite: 52]
            insuranceBet: data.player.insuranceBet,
            gameActive: !!data.id
        });
    }
}