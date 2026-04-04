# jBlackJack


https://github.com/user-attachments/assets/69247272-0cdd-44a9-807a-5c1bc8c4b628


A web-based Blackjack application built to apply and deepen my knowledge of the Java/Web ecosystem.  

I started this project during the semester break to take the concepts I had learned in class and build a working full-stack application from scratch. The lion's share of development time went into architecting the core Blackjack game logic as a decoupled Java library, and subsequently serving that game state via a Spring Boot backend. 

Building this was incredibly fun and achieved my primary goal: significantly improving my hands-on skills with Java, Spring Boot, and backend architectures.

## Structure:

* `blackjack-core/` — core game/domain logic (plain Java library) + tests.
* `blackjack-web/` — Spring Boot WebMVC application that depends on `blackjack-core`.
* `frontend/` — standalone static HTML/CSS/JS UI.

## Prerequisites

* Java 21


## Run the app

The web application is a Spring Boot app located in `blackjack-web/`.

```
# Run Spring Boot (dev)
./gradlew :blackjack-web:bootRun
```

The Spring Boot app also serves the static frontend assets (copied from
`frontend/`) from its built-in static resources path.

Open in your browser:

* http://localhost:8080/CardsMVC.html

## Known Issues
- Attempting to split a hand when it is not legally allowed currently crashes the frontend logic.
- The frontend is mostly functional but remains a work in progress.
- While the core library and backend are operational, some components require refactoring to adhere to cleaner code principles and better separation of concerns.
- The current HTTP request/response architecture isn't optimal for real-time game state synchronization.

## Expansion Ideas
- Migrate the backend communication to WebSockets to enable real-time state updates and eventually support a multiplayer Blackjack mode.
- Create an automated bot that uses Card Counting algorithms and ML to calculate probabilities and play against the dealer autonomously.
