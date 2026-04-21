export default class View {
    constructor() {
        // get input elements
        this.creditDisplay = document.getElementById('credit-display');
        this.statusDisplay = document.getElementById('status-display');
        this.controlsContainer = document.getElementById('controls-container');
        this.dealerValueMessage = document.getElementById('dealer-info');
        this.betInput = this.createInput('number', 'bet-input', 'Amount');
        this.btnStart = this.createButton('Start Game', 'btn-start');
        this.btnBet = this.createButton('Place Bet', 'btn-bet');
        this.btnHit = this.createButton('Hit', 'btn-hit');
        this.btnStand = this.createButton('Stand', 'btn-stand');
        this.btnDouble = this.createButton('Double Down', 'btn-double');
        this.btnSplit = this.createButton('Split', 'btn-split');
        this.btnSurrender = this.createButton('Surrender', 'btn-surrender');
        this.btnInsureYes = this.createButton('Buy Insurance', 'btn-ins-yes');
        this.btnInsureNo = this.createButton('No Insurance', 'btn-ins-no');
        this.btnNextRound = this.createButton('Next Round', 'btn-next');

        this.hideAllControls();
        this.show(this.btnStart);
    }

    renderPlayerHands(hands) {
        const storage = document.querySelector('.card.player .card-storage');
        storage.innerHTML = '';

        hands.forEach((hand, index) => {
            const handDiv = document.createElement('div');
            handDiv.className = 'hand-container';
            handDiv.style.border = hand.status === 'OnTurn' ? '2px solid #ffd700' : '2px solid transparent';

            const info = document.createElement('div');
            info.innerText = `($${hand.bet})`;
            info.className = 'hand-card';
            handDiv.appendChild(info);

            const cardContainer = document.createElement('div');
            cardContainer.className = 'cards-row';
            hand.cards.forEach(c => c.render(cardContainer));

            let phasesToNotShowHandValue = ["Preparing", "Betting", "Insuring", "WaitingForTurn", "OnTurn"];
            if (!phasesToNotShowHandValue.includes(hand.status)) { // display hand value when turn finished
                info.innerText += ` Value = ${hand.handValue}`;
            }

            handDiv.appendChild(cardContainer);
            storage.appendChild(handDiv);
        });
    }

    renderDealerCards(data) {
        const storage = document.querySelector('#dealer-cards');
        storage.innerHTML = '';
        data.cards.forEach(c => c.render(storage))

        const info = document.getElementById("dealer-info");
        let phasesToNotShowHandValue = ["RoundOver"];
        if (phasesToNotShowHandValue.includes(data.gameState)) { // display hand value when turn finished
            info.innerText = `Value = ${data.handValue}`;
        }
    }

    renderGameInfo(info) {
        this.creditDisplay.innerText = `Credit: $${info.credit}`;
        this.statusDisplay.innerText = "";
        if (info.insuranceBought) {
            this.statusDisplay.innerText = "(Insured)";
        }

        // hide buttons
        this.hideAllControls();

        // display buttons for state
        if (!info.gameActive) {
            this.show(this.btnStart);
            return;
        }

        if (info.status === 'RoundOver') {
            this.show(this.btnNextRound);
            if (info.roundWinnings > 0) {
                this.creditDisplay.innerText += `\nWinnings: + $${info.roundWinnings}`;
                this.creditDisplay.innerText += `\nNew Credit: $${info.roundWinnings + info.credit}`;
            } else if (info.roundWinnings < 0) {
                this.creditDisplay.innerText += `\nRound Lost`;
            } else {
                this.creditDisplay.innerText += `\nRound Tie`;
            }
            return;
        }

        if (info.status === 'WaitingForBet') {
            this.show(this.betInput);
            this.show(this.btnBet);
            this.dealerValueMessage.innerText ="";
            return;
        }

        if (info.status.includes('WaitingForMove')) {
            this.show(this.btnHit);
            this.show(this.btnStand);

            if (info.surrenderAvailable) {
                this.show(this.btnSurrender);
            }
            if (info.splitAvailable) {
                this.show(this.btnSplit);
            }
            if (info.doubleDownAvailable) {
                this.show(this.btnDouble);
            }
        }

        if (info.status === 'OfferingInsurance') {
            this.show(this.btnInsureYes);
            this.show(this.btnInsureNo);
        }
    }

    showMessage(msg) {
        setTimeout(() => alert(msg), 100);
    }

    createButton(text, id) {
        const btn = document.createElement('button');
        btn.id = id;
        btn.innerText = text;
        btn.className = 'action-btn';
        btn.style.display = 'none';
        this.controlsContainer.appendChild(btn);
        return btn;
    }

    createInput(type, id, placeholder) {
        const inp = document.createElement('input');
        inp.type = type;
        inp.id = id;
        inp.placeholder = placeholder;
        inp.style.display = 'none';
        inp.style.marginBottom = '5px';
        this.controlsContainer.appendChild(inp);
        return inp;
    }

    createElement(tag, id, text=null) {
        const el = document.createElement(tag);
        if(id) el.id = id;
        if(text) el.innerText = text;
        return el;
    }

    show(el) { el.style.display = 'inline-block'; }

    hideAllControls() {
        Array.from(this.controlsContainer.children)
            .forEach(c => c.style.display = 'none');
    }

    bindStart(h) { this.btnStart.onclick = h; }
    bindBet(h) { this.btnBet.onclick = () => h(this.betInput.value); }
    bindHit(h) { this.btnHit.onclick = h; }
    bindStand(h) { this.btnStand.onclick = h; }
    bindDouble(h) { this.btnDouble.onclick = h; }
    bindSplit(h) { this.btnSplit.onclick = h; }
    bindSurrender(h) { this.btnSurrender.onclick = h; }
    bindInsurance(h) {
        this.btnInsureYes.onclick = () => h(true);
        this.btnInsureNo.onclick = () => h(false);
    }
    bindNextRound(h) { this.btnNextRound.onclick = h; }
}