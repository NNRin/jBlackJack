# jBlackJack

<h3><a href="https://jblackjack-production.up.railway.app/CardsMVC.html" target="_blank">Click here to play the live game!</a></h3> 
(https://jblackjack-production.up.railway.app/CardsMVC.html)  

<br>
<br>

https://github.com/user-attachments/assets/69247272-0cdd-44a9-807a-5c1bc8c4b628

A web-based Blackjack application built to apply and deepen my knowledge of the Java/Web ecosystem.  

I started this project during the semester break to take the concepts I had learned in class and build a working full-stack application from scratch. The lion's share of development time went into architecting the core Blackjack game logic as a decoupled Java library, and subsequently serving that game state via a Spring Boot backend. 

Building this was incredibly fun and achieved my primary goal: improving my hands-on skills with Java, Spring Boot, and backend architectures.

### Key Learnings:
- Understanding the uses and limitations of REST architecture for stateful games like Blackjack.
- Taking a set of requirements (Blackjack rules) and translating them into business logic.
- Breaking down a complex application into decoupled objects and defining their interactions to create a clean and maintainable architecture.

## Tech Stack

![Java](https://img.shields.io/badge/Java_21-ED8B00?style=flat&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot_4-6DB33F?style=flat&logo=springboot&logoColor=white)
![HTML](https://img.shields.io/badge/HTML%2FCSS%2FJS-E34F26?style=flat&logo=html5&logoColor=white)
![Gradle](https://img.shields.io/badge/Gradle-02303A?style=flat&logo=gradle&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=flat&logo=docker&logoColor=white)
![Railway](https://img.shields.io/badge/Railway-0B0D0E?style=flat&logo=railway&logoColor=white)

## Structure:

* `blackjack-core/` — core game/domain logic (plain Java library) + tests.
* `blackjack-web/` — Spring Boot WebMVC application that depends on `blackjack-core`.
* `frontend/` — standalone static HTML/CSS/JS UI.

## Known Issues
- The frontend is functional, but remains a work in progress.
- While the core library and backend are operational, some components require refactoring to adhere to cleaner code principles and better separation of concerns.
- The current HTTP request/response architecture isn't optimal for real-time game state synchronization.

## Expansion Ideas
- Migrate the backend communication to WebSockets to enable real-time state updates and eventually support a multiplayer Blackjack mode.
- Create an automated bot that uses Card Counting algorithms and ML to calculate probabilities and play against the dealer autonomously.

## Prerequisites
One of:
* Java 21 (local development)
* Docker installed (running app via docker)

## Run the app

The app can be run locally using either Docker or Gradle.

---
#### Option 1: Run with Docker

```
# Build the image
docker build -t jblackjack .

# Run the container
docker run -p 8080:8080 jblackjack
```
---
#### Option 2: Run with Spring Boot / Gradle
The web application is located in ``blackjack-web/``.
```
./gradlew :blackjack-web:bootRun
```  
---
To access the game, open this URL in your browser:

* http://localhost:8080/CardsMVC.html