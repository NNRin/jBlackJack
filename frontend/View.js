export default class View {
    constructor() {
        // 1. CLEANUP: Remove hardcoded buttons from HTML to avoid confusion
        const oldStart = document.querySelector('#start');
        if(oldStart) oldStart.remove(); // Remove old Start button from Dealer section

        const oldPlayerBtns = document.querySelectorAll('.card.player button');
        oldPlayerBtns.forEach(b => b.remove()); // Remove old Hit/Stand from Player section

        // 2. SETUP: Create the new Control Panel in the Player section
        this.infoContainer = document.querySelector('.card.player .info');

        // Status & Credit Displays
        this.statusDisplay = this.createElement('p', 'status-display', '');
        this.creditDisplay = this.createElement('p', 'credit-display', 'Credit: $0');

        // Insert them at the top of the info box
        this.infoContainer.prepend(this.statusDisplay);
        this.infoContainer.prepend(this.creditDisplay);

        // Controls Container
        this.controlsContainer = this.createElement('div', 'controls-container');
        this.controlsContainer.style.display = 'flex';
        this.controlsContainer.style.flexDirection = 'column';
        this.controlsContainer.style.gap = '5px';
        this.controlsContainer.style.marginTop = '10px';
        this.infoContainer.appendChild(this.controlsContainer);

        // 3. CREATE INPUTS & BUTTONS
        this.betInput = this.createInput('number', 'bet-input', 'Amount');

        // Create all necessary buttons
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

        // 4. INITIAL STATE: Show only Start button
        this.hideAllControls();
        this.show(this.btnStart);
    }

    // --- Renders ---

    renderPlayerHands(hands) {
        const storage = document.querySelector('.card.player .card-storage');
        storage.innerHTML = '';

        storage.style.display = 'flex';
        storage.style.flexDirection = 'row';
        storage.style.alignItems = 'stretch';
        storage.style.width = '100%';
        storage.style.height = '100%';
        storage.style.padding = '10px';
        storage.style.gap = '10px';

        hands.forEach((hand, index) => {
            const handDiv = document.createElement('div');
            handDiv.className = 'hand-container';

            handDiv.style.flex = '1';
            handDiv.style.display = 'flex';
            handDiv.style.flexDirection = 'column';

            // --- FIX START ---
            // 'flex-start' keeps the text at the top. 'center' was pushing it up and clipping it.
            handDiv.style.justifyContent = 'flex-start';
            handDiv.style.alignItems = 'center';
            // Add padding to give the text breathing room from the top border
            handDiv.style.paddingTop = '1.5rem';
            // --- FIX END ---

            handDiv.style.borderRadius = '8px';
            handDiv.style.border = hand.status === 'OnTurn' ? '2px solid #ffd700' : '2px solid transparent';

            const info = document.createElement('div');
            info.innerText = `Hand ${index + 1} ($${hand.bet})`;
            info.style.marginBottom = '20px'; // Space between text and cards
            info.style.color = '#ddd';
            // Scale text up to match the larger cards
            info.style.fontSize = '1.8rem';
            info.style.fontWeight = 'bold';
            handDiv.appendChild(info);

            const cardContainer = document.createElement('div');
            cardContainer.className = 'cards-row';

            // Note: We removed the 'fontSize: 7rem' line here as it didn't work.
            // The scaling is now handled entirely by CSS transform.

            hand.cards.forEach(c => c.render(cardContainer));

            handDiv.appendChild(cardContainer);
            storage.appendChild(handDiv);
        });
    }

    renderDealerCards(cards) {
        const storage = document.querySelector('.card.hero .card-storage');
        storage.innerHTML = '';
        cards.forEach(c => c.render(storage));
    }

    renderGameInfo(info) {
        this.creditDisplay.innerText = `Credit: $${info.credit}`;
        //this.statusDisplay.innerText = `Status: ${info.status}`;
        if (info.insuranceBought) this.statusDisplay.innerText += " (Insured)";

        // RESET VISIBILITY
        this.hideAllControls();

        // LOGIC FOR SHOWING BUTTONS [cite: 51, 69, 81]

        // 1. No active game? Show Start.
        if (!info.gameActive) {
            this.show(this.btnStart);
            return;
        }

        // 2. Round Over? Show Next Round.
        if (info.status === 'RoundOver') {
            this.show(this.btnNextRound);
            return;
        }

        // 3. Betting Phase
        if (info.status === 'WaitingForBet') {
            this.show(this.betInput);
            this.show(this.btnBet);
            return;
        }

        // 4. Playing Phase (Hit, Stand, etc.)
        if (info.status.includes('WaitingForMove')) { // Covers 'WaitingForMoveSurrenderAvailable' too
            this.show(this.btnHit);
            this.show(this.btnStand);
            this.show(this.btnDouble);
            this.show(this.btnSplit);

            if (info.surrenderAvailable) { // [cite: 51]
                this.show(this.btnSurrender);
            }
        }

        // 5. Insurance Phase (Adjust based on exact backend string)
        if (info.status === 'WaitingForInsurance') {
            this.show(this.btnInsureYes);
            this.show(this.btnInsureNo);
        }
    }

    showMessage(msg) {
        // Simple delay to let rendering finish before alert blocks execution
        setTimeout(() => alert(msg), 100);
    }

    // --- DOM Helpers ---
    createButton(text, id) {
        const btn = document.createElement('button');
        btn.id = id;
        btn.innerText = text;
        btn.className = 'action-btn'; // Make sure to style this class in CSS
        btn.style.display = 'none';
        btn.style.margin = '4px 0';
        btn.style.padding = '8px';
        btn.style.cursor = 'pointer';
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
        Array.from(this.controlsContainer.children).forEach(c => c.style.display = 'none');
    }

    // --- Bindings ---
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