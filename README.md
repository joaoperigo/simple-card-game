# Turn-Based Fighter Game

A strategic turn-based fighting game built with Spring Boot and WebSocket technology, where players engage in tactical combat using fighters with different power levels.

![Sketch of this game](https://github.com/joaoperigo/doublehexa/blob/master/_ASSETS/doublehexa.png)

## Game Rules

### Setup Phase
- Each player starts with 4 fighters
- Players distribute 16 points among their fighters
- Each fighter must have between 1 and 8 points
- Players receive power cards numbered 1 through 8

### Combat Phase
- Players take turns attacking opponents' fighters
- Each turn consists of:
    1. Selecting an attacking fighter
    2. Choosing a power card to attack with
    3. Selecting a target fighter
    4. Opponent can defend using their power cards
- Combat Resolution:
    - If attack power > defense power: target takes damage equal to the difference
    - If defense power > attack power: attacker takes damage equal to the difference
    - When a fighter reaches 0 points, they become inactive
- Used power cards cannot be reused

### Victory Conditions
- Game ends when both players run out of usable power cards
- The player with the highest total points among active fighters wins
- Players must pass their turn if they have no valid moves available

## Technologies Used

### Backend
- Java 21
- Spring Boot 3.4
- Spring Security
- Spring WebSocket
- Spring Data JPA
- H2 Database
- Lombok

### Frontend
- Thymeleaf
- JavaScript
- Tailwind CSS
- SockJS
- STOMP WebSocket

### Key Features
- Real-time game updates using WebSocket
- User authentication and session management
- Persistent game state
- Responsive UI with Tailwind CSS
- Turn-based combat system
- Strategic gameplay mechanics

## Architecture
The application follows a client-server architecture where:
- Server manages game state and rules
- WebSocket enables real-time bidirectional communication
- Frontend updates dynamically based on game state changes
- H2 database persists game data

## Development Approach
- MVC pattern for request handling
- DTO pattern for data transfer
- Repository pattern for data access
- Service layer for business logic
- WebSocket for real-time updates