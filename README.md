# jBlackJack

Multi-module Blackjack project:

* `blackjack-core/` — core game/domain logic (plain Java library) + tests.
* `blackjack-web/` — Spring Boot WebMVC application that depends on `blackjack-core`.
* `frontend/` — standalone static HTML/JS UI.

## Prerequisites

* Java 21


## Run the web app

The web application is a Spring Boot app located in `blackjack-web/`.

```
# Run Spring Boot (dev)
./gradlew :blackjack-web:bootRun
```

The Spring Boot app also serves the static frontend assets (copied from
`frontend/`) from its built-in static resources path.

Open in your browser:

* http://localhost:8080/CardsMVC.html

## Project structure

```
.
├── blackjack-core/         # blackjack game logic + tests
├── blackjack-web/          # Spring Boot REST backend app
└── frontend/               # static UI sources (packaged into blackjack-web)
```
