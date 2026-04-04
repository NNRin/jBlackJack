# jBlackJack


https://github.com/user-attachments/assets/69247272-0cdd-44a9-807a-5c1bc8c4b628


This project features a webapp to Play BlackJack with.   
I was inspired to create this project during the Semester break, as I was eager to use the technologies I had learned to use during the Semester. The goal of this projet was to further deepen my understanding of these technologies and improve my skills overall. Of course I also wanted to have fun whilst doing it.

The Lion share of time spent on this project was in replicating the BlackJack game Logic in a library and using Spring Boot to create a backend serving that game data.

Overall I'm quite happy how it turned out. But even more important is that I feel like I have deepened my understanding of the technologies used. 
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
- Splitting crashes the frontend if the current Hand cannot be Split
- UI is very much a work in progress
- Library / Backend are functional but code quality needs improvement
- Backend Architecture is not optimal
  - Perhaps an approach with websockets could be more beneficial

## Expansion Ideas
- Implement a multiplayer version of BlackJack with websockets
- Create an AI with Machine Learning to beat the odds 
  - Card Counting approach
