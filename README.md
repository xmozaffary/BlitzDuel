# BlitzDuel

## Innehållsförteckning

- [Om Projektet](#om-projektet)
- [Tech Stack](#tech-stack)
- [Features](#features)
- [Arkitektur](#arkitektur)
- [Installation & Setup](#installation--setup)
- [Deployment](#deployment)
- [WebSocket-kommunikation](#websocket-kommunikation)
- [Databas](#databas)
- [Projektstruktur](#projektstruktur)
- [Författare](#författare)

## Om Projektet

BlitzDuel är en webbapplikation som möjliggör real-time quiz-dueller mellan två spelare. En spelare skapar en lobby och får en 6-siffrig kod som den andra spelaren använder för att joina. När båda är redo startar quizet och båda ser samma frågor samtidigt med en synkroniserad timer.

### Projektets Fokus

Detta projekt fokuserar på att lära sig och implementera:

- **WebSocket med STOMP** - Tvåvägskommunikation i realtid
- **Real-time synkronisering** - Båda spelare ser samma timer och frågor
- **Server-driven arkitektur** - Servern är "source of truth" för att undvika synkroniseringsproblem
- **CI/CD pipeline** - Automatisk deployment från GitHub till egen server

## Tech Stack

### Backend

| Teknologi        | Version | Användning                         |
| ---------------- | ------- | ---------------------------------- |
| Java             | 21      | Programmeringsspråk                |
| Spring Boot      | 3.x     | Backend-ramverk                    |
| Spring WebSocket | -       | Real-time kommunikation            |
| STOMP            | -       | Meddelandeprotokoll över WebSocket |
| Spring Security  | -       | Autentisering & auktorisering      |
| Spring Data JPA  | -       | Databashantering                   |
| PostgreSQL       | 15      | Databas                            |
| Maven            | -       | Beroendehantering & build          |

### Frontend

| Teknologi     | Version | Användning                  |
| ------------- | ------- | --------------------------- |
| React         | 18      | UI-ramverk                  |
| TypeScript    | 5.x     | Typsäkert JavaScript        |
| Vite          | 5.x     | Build-verktyg & dev-server  |
| SCSS          | -       | Styling                     |
| SockJS Client | -       | WebSocket med fallback      |
| STOMP.js      | -       | STOMP-protokoll på klienten |
| React Router  | 6.x     | Routing                     |

### Infrastructure

| Teknologi          | Användning                     |
| ------------------ | ------------------------------ |
| Docker             | Containerisering               |
| Docker Compose     | Multi-container orchestration  |
| GitHub Actions     | CI/CD pipeline                 |
| Cloudflare Tunnel  | Säker exponering till internet |
| Raspberry Pi 4     | Self-hosted produktionsserver  |
| Ubuntu 24.04 ARM64 | Operativsystem på Pi           |

## Features

### Implementerade Features

- **Autentisering**

  - Google OAuth 2.0-inloggning
  - JWT-tokens för sessionshantering
  - Säker token-validering

- **Lobby-system**

  - Skapa lobby med unik 6-siffrig kod
  - Joina lobby via kod
  - Real-time uppdateringar när spelare joinar
  - Väntskärm tills båda är redo

- **Quiz Gameplay**

  - Synkroniserade frågor för båda spelare
  - Server-driven timer (undviker klocksync-problem)
  - Visuell feedback på svar (grön/röd)
  - Live poänguppdatering
  - Resultatskärm med vinnare

- **Tekniska Features**
  - Responsiv design (mobil & desktop)
  - Optimerade databasfrågor (JOIN FETCH)
  - Health checks för alla tjänster
  - Automatisk database seeding

### Spelflöde

```
1. Spelare loggar in via Google
2. Väljer ett quiz från listan
3. Skapar lobby → Får 6-siffrig kod
4. Delar koden med motståndare
5. Motståndare joinar med koden
6. Host startar spelet
7. Båda ser samma fråga + timer
8. Svarar inom tidsgränsen
9. Ser resultat efter varje fråga
10. Slutresultat visas efter sista frågan
```

## Arkitektur

### Systemöversikt

```
┌─────────────────────────────────────────────────────────────────┐
│                         INTERNET                                 │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                    CLOUDFLARE TUNNEL                            │
│                    (Säker HTTPS-tunnel)                         │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                     RASPBERRY PI 4                              │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │                   DOCKER NETWORK                          │  │
│  │                                                           │  │
│  │  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐   │  │
│  │  │   NGINX     │    │ SPRING BOOT │    │ POSTGRESQL  │   │  │
│  │  │  (Frontend) │───►│  (Backend)  │───►│ (Database)  │   │  │
│  │  │   :80       │    │   :8080     │    │   :5432     │   │  │
│  │  └─────────────┘    └─────────────┘    └─────────────┘   │  │
│  │        │                   │                              │  │
│  │        │    WebSocket/STOMP│                              │  │
│  │        │◄──────────────────┘                              │  │
│  │                                                           │  │
│  └──────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
```

### Backend-arkitektur

```
Controller Layer          Service Layer           Data Layer
─────────────────────────────────────────────────────────────────

┌──────────────────┐     ┌──────────────────┐    ┌──────────────┐
│ GameController   │────►│ GameService      │───►│ Repository   │
│ (WebSocket)      │     │                  │    │              │
└──────────────────┘     ├──────────────────┤    ├──────────────┤
                         │ TimerService     │    │ Quiz         │
┌──────────────────┐     │ (Scheduled tasks)│    │ Question     │
│ LobbyController  │────►├──────────────────┤    │ User         │
│ (WebSocket)      │     │ AnswerService    │    └──────────────┘
└──────────────────┘     │ (Score logic)    │
                         ├──────────────────┤
┌──────────────────┐     │ LobbyService     │
│ QuizController   │────►│ (Lobby mgmt)     │
│ (REST)           │     ├──────────────────┤
└──────────────────┘     │ QuizService      │
                         │ (Quiz data)      │
┌──────────────────┐     └──────────────────┘
│ AuthController   │
│ (REST + OAuth)   │
└──────────────────┘
```

### Kommunikationsflöde

```
┌────────┐                    ┌────────┐                    ┌────────┐
│Player 1│                    │ Server │                    │Player 2│
└───┬────┘                    └───┬────┘                    └───┬────┘
    │                             │                             │
    │──── CREATE LOBBY ──────────►│                             │
    │◄─── LOBBY_CREATED ──────────│                             │
    │     (code: ABC123)          │                             │
    │                             │                             │
    │                             │◄──── JOIN LOBBY ────────────│
    │                             │      (code: ABC123)         │
    │◄─── PLAYER_JOINED ──────────│───── PLAYER_JOINED ────────►│
    │                             │                             │
    │──── START GAME ────────────►│                             │
    │◄─── QUESTION ───────────────│───── QUESTION ─────────────►│
    │◄─── TIMER_UPDATE ───────────│───── TIMER_UPDATE ─────────►│
    │◄─── TIMER_UPDATE ───────────│───── TIMER_UPDATE ─────────►│
    │                             │                             │
    │──── ANSWER (index: 2) ─────►│                             │
    │                             │◄──── ANSWER (index: 1) ─────│
    │◄─── BOTH_ANSWERED ──────────│───── BOTH_ANSWERED ────────►│
    │     (scores, correct)       │      (scores, correct)      │
    │                             │                             │
```

## Installation & Setup

### Förutsättningar

- Node.js 20 eller senare
- Java 21 (JDK)
- Docker & Docker Compose
- Maven 3.9+
- Git

### Steg 1: Klona repot

```bash
git clone https://github.com/xmozaffary/BlitzDuel.git
```

### Steg 2: Konfigurera miljövariabler

```bash
cd blitzduel

cp .env.example .env
```

Redigera `.env` med dina värden:

```env
# Google OAuth (skapa på console.cloud.google.com)
GOOGLE_CLIENT_ID=din-client-id.apps.googleusercontent.com
GOOGLE_CLIENT_SECRET=din-client-secret

# JWT Secret (generera en säker nyckel)
JWT_SECRET=min-super-hemliga-jwt-nyckel-minst-32-tecken

# Databas (för lokal utveckling)
DB_USER=blitz
DB_PASSWORD=blitz123
DB_NAME=blitzduel

# URLs (för lokal utveckling)
FRONTEND_URL=http://localhost:5173
BACKEND_URL=http://localhost:8080
```

### Steg 3: Starta databasen

```bash
docker compose -f docker-compose.dev.yml up -d
```

Verifiera att databasen körs:

```bash
docker ps
# Ska visa blitzduel-postgres-dev
```

### Steg 4: Starta backend

```bash
cd apps/backend/blitzduel

# Linux/Mac
./mvnw spring-boot:run

# Windows
mvnw.cmd spring-boot:run
```

Backend körs nu på `http://localhost:8080`

### Steg 5: Starta frontend

Öppna en ny terminal:

```bash
cd apps/frontend/blitzduel
npm install
npm run dev
```

Frontend körs nu på `http://localhost:5173`

### Steg 6: Testa applikationen

1. Öppna `http://localhost:5173` i webbläsaren
2. Logga in med Google
3. Välj ett quiz
4. Skapa en lobby
5. Öppna en till webbläsare/inkognito och joina med koden

## Deployment

### CI/CD Pipeline

Projektet använder GitHub Actions för automatisk deployment.

```
┌─────────────┐     ┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│   Push to   │────►│   Detect    │────►│    Build    │────►│   Deploy    │
│    main     │     │   Changes   │     │   Docker    │     │   to Pi     │
└─────────────┘     └─────────────┘     └─────────────┘     └─────────────┘
                           │                   │                   │
                           ▼                   ▼                   ▼
                    ┌─────────────┐     ┌─────────────┐     ┌─────────────┐
                    │ Backend?    │     │ Push to     │     │ Pull images │
                    │ Frontend?   │     │ Docker Hub  │     │ Restart     │
                    └─────────────┘     └─────────────┘     └─────────────┘
```

### Pipeline-steg

1. **Detect Changes** - Kollar vilka mappar som ändrats
2. **Build Backend** (om ändrad)
   - Kompilerar Java med Maven
   - Bygger Docker image (ARM64)
   - Pushar till Docker Hub
3. **Build Frontend** (om ändrad)
   - Installerar dependencies
   - Bygger med Vite
   - Bygger Docker image (ARM64)
   - Pushar till Docker Hub
4. **Deploy**
   - Kör på self-hosted runner (Pi)
   - Pullar nya images
   - Startar om containers
   - Kör health checks

### GitHub Secrets

Följande secrets måste konfigureras i GitHub:

| Secret                 | Beskrivning                  |
| ---------------------- | ---------------------------- |
| `DOCKER_USERNAME`      | Docker Hub användarnamn      |
| `DOCKER_PASSWORD`      | Docker Hub lösenord/token    |
| `DB_USER`              | Databas användarnamn         |
| `DB_PASSWORD`          | Databas lösenord             |
| `GOOGLE_CLIENT_ID`     | Google OAuth Client ID       |
| `GOOGLE_CLIENT_SECRET` | Google OAuth Secret          |
| `JWT_SECRET`           | JWT signeringsnyckel         |
| `TUNNEL_TOKEN`         | Cloudflare Tunnel token      |
| `FRONTEND_URL`         | Produktions-URL för frontend |
| `BACKEND_URL`          | Produktions-URL för backend  |
| `BACKEND_WS_URL`       | WebSocket URL (wss://)       |

### Manuell Deployment

```bash
# SSH till Raspberry Pi
ssh user@raspberry-pi

# Gå till projektmappen
cd ~/blitzduel

# Uppdatera images
docker compose pull

# Starta om tjänsterna
docker compose up -d

# Verifiera status
docker compose ps
docker compose logs -f
```

## WebSocket-kommunikation

### Teknisk Implementation

BlitzDuel använder STOMP (Simple Text Oriented Messaging Protocol) över WebSocket för real-time kommunikation. SockJS används som fallback om WebSocket inte stöds.

### Konfiguration (Backend)

```java
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .setHandshakeHandler(new CustomHandshakeHandler())
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic", "/queue");
        registry.setApplicationDestinationPrefixes("/app");
        registry.setUserDestinationPrefix("/user");
    }
}
```

### Endpoints

#### Skicka meddelanden (Client → Server)

| Endpoint                  | Payload                 | Beskrivning           |
| ------------------------- | ----------------------- | --------------------- |
| `/app/lobby/create`       | `{ name, quizId }`      | Skapa ny lobby        |
| `/app/lobby/{code}/join`  | `{ name }`              | Joina befintlig lobby |
| `/app/game/{code}/start`  | -                       | Starta spelet         |
| `/app/game/{code}/answer` | `{ name, answerIndex }` | Skicka svar           |

#### Prenumerera (Server → Client)

| Topic                 | Beskrivning                               |
| --------------------- | ----------------------------------------- |
| `/topic/lobby/{code}` | Broadcast till alla i lobby               |
| `/user/queue/lobby`   | Privata meddelanden till specifik spelare |
| `/user/queue/errors`  | Felmeddelanden                            |

### Meddelanden

#### LOBBY_CREATED

```json
{
  "lobbyCode": "ABC123",
  "status": "WAITING",
  "quizId": 1,
  "hostName": "Player1",
  "guestName": null
}
```

#### PLAYER_JOINED

```json
{
  "status": "READY",
  "hostName": "Player1",
  "guestName": "Player2"
}
```

#### QUESTION

```json
{
  "type": "QUESTION",
  "currentQuestionIndex": 0,
  "questionText": "Vad är huvudstaden i Sverige?",
  "options": ["Göteborg", "Stockholm", "Malmö", "Uppsala"],
  "hostName": "Player1",
  "guestName": "Player2",
  "timeLimit": 5,
  "startTime": 1699123456789
}
```

#### TIMER_UPDATE

```json
{
  "type": "TIMER_UPDATE",
  "remainingTime": 3,
  "currentQuestionIndex": 0
}
```

#### BOTH_ANSWERED / GAME_OVER

```json
{
  "status": "BOTH_ANSWERED",
  "correctAnswerIndex": 1,
  "hostCorrect": true,
  "guestCorrect": false,
  "hostScore": 1,
  "guestScore": 0,
  "hostName": "Player1",
  "guestName": "Player2",
  "hostAnswerIndex": 1,
  "guestAnswerIndex": 2
}
```

### Frontend-anslutning

```typescript
import SockJS from "sockjs-client";
import { Client } from "@stomp/stompjs";

const client = new Client({
  webSocketFactory: () => new SockJS(`${API_URL}/ws`),
  onConnect: () => {
    // Prenumerera på lobby-uppdateringar
    client.subscribe(`/topic/lobby/${lobbyCode}`, (message) => {
      const data = JSON.parse(message.body);
      handleMessage(data);
    });
  },
});

client.activate();
```

## Databas

### ER-Diagram

```
┌─────────────────┐       ┌─────────────────┐
│      QUIZ       │       │    QUESTION     │
├─────────────────┤       ├─────────────────┤
│ id (PK)         │───┐   │ id (PK)         │
│ title           │   │   │ quiz_id (FK)    │──┐
│ description     │   └──►│ question_text   │  │
│ category        │       │ options (JSON)  │  │
│ image_url       │       │ correct_index   │  │
└─────────────────┘       │ time_limit      │  │
                          └─────────────────┘  │
                                               │
┌─────────────────┐                            │
│      USER       │                            │
├─────────────────┤                            │
│ id (PK)         │                            │
│ email           │       ┌────────────────────┘
│ name            │       │
│ picture_url     │       │ 1:N relation
│ provider        │       │ (Ett quiz har många frågor)
└─────────────────┘       │
```

### Optimeringar

Projektet använder optimerade databasfrågor för att undvika N+1-problemet:

```java
// Hämta quiz med alla frågor i EN query
@Query("SELECT q FROM Quiz q LEFT JOIN FETCH q.questions")
List<Quiz> findAllWithQuestions();

@Query("SELECT q FROM Quiz q LEFT JOIN FETCH q.questions WHERE q.id = :id")
Optional<Quiz> findByIdWithQuestions(@Param("id") Long id);
```

Detta reducerade antal queries från 67 till 2 vid listning av quiz.

## Projektstruktur

```
blitzduel/
│
├── apps/
│   ├── backend/
│   │   └── blitzduel/
│   │       ├── src/
│   │       │   └── main/
│   │       │       ├── java/se/examenarbete/blitzduel/
│   │       │       │   ├── config/
│   │       │       │   │   ├── CorsConfig.java
│   │       │       │   │   ├── CustomHandshakeHandler.java
│   │       │       │   │   ├── DataSeeder.java
│   │       │       │   │   ├── SecurityConfig.java
│   │       │       │   │   └── WebSocketConfig.java
│   │       │       │   │
│   │       │       │   ├── controller/
│   │       │       │   │   ├── AuthController.java
│   │       │       │   │   ├── GameController.java
│   │       │       │   │   ├── LobbyController.java
│   │       │       │   │   └── QuizController.java
│   │       │       │   │
│   │       │       │   ├── dto/
│   │       │       │   │   ├── GameUpdateResponse.java
│   │       │       │   │   ├── LobbyResponse.java
│   │       │       │   │   ├── QuestionDTO.java
│   │       │       │   │   └── SubmitAnswerRequest.java
│   │       │       │   │
│   │       │       │   ├── model/
│   │       │       │   │   ├── GameSession.java
│   │       │       │   │   ├── Lobby.java
│   │       │       │   │   ├── Question.java
│   │       │       │   │   ├── Quiz.java
│   │       │       │   │   └── User.java
│   │       │       │   │
│   │       │       │   ├── repository/
│   │       │       │   │   ├── QuestionRepository.java
│   │       │       │   │   ├── QuizRepository.java
│   │       │       │   │   └── UserRepository.java
│   │       │       │   │
│   │       │       │   ├── service/
│   │       │       │   │   ├── AnswerService.java
│   │       │       │   │   ├── GameService.java
│   │       │       │   │   ├── LobbyService.java
│   │       │       │   │   ├── QuizService.java
│   │       │       │   │   └── TimerService.java
│   │       │       │   │
│   │       │       │   └── util/
│   │       │       │       └── RandomCodeGenerator.java
│   │       │       │
│   │       │       └── resources/
│   │       │           └── application.properties
│   │       │
│   │       ├── Dockerfile
│   │       └── pom.xml
│   │
│   └── frontend/
│       └── blitzduel/
│           ├── src/
│           │   ├── components/
│           │   │   ├── game/
│           │   │   │   ├── AnswerButton.tsx
│           │   │   │   ├── GameScreen.tsx
│           │   │   │   ├── QuestionCard.tsx
│           │   │   │   ├── QuestionTimer.tsx
│           │   │   │   ├── ResultScreen.tsx
│           │   │   │   └── ScoreBoard.tsx
│           │   │   │
│           │   │   ├── lobby/
│           │   │   │   ├── CreateLobby.tsx
│           │   │   │   ├── JoinLobby.tsx
│           │   │   │   └── WaitingRoom.tsx
│           │   │   │
│           │   │   └── common/
│           │   │       ├── Navbar.tsx
│           │   │       └── ProtectedRoute.tsx
│           │   │
│           │   ├── pages/
│           │   │   ├── HomePage.tsx
│           │   │   ├── LoginPage.tsx
│           │   │   ├── QuizDetailsPage.tsx
│           │   │   └── QuizSelectionPage.tsx
│           │   │
│           │   ├── services/
│           │   │   ├── api.ts
│           │   │   └── websocket.ts
│           │   │
│           │   ├── styles/
│           │   │   ├── _game.scss
│           │   │   ├── _lobby.scss
│           │   │   ├── _navbar.scss
│           │   │   └── main.scss
│           │   │
│           │   ├── types/
│           │   │   ├── GameData.ts
│           │   │   └── lobby.ts
│           │   │
│           │   ├── App.tsx
│           │   └── main.tsx
│           │
│           ├── Dockerfile
│           ├── package.json
│           └── vite.config.ts
│
├── .github/
│   └── workflows/
│       └── deploy.yml
│
├── docker-compose.yml          # Produktion
├── docker-compose.dev.yml      # Utveckling
├── .env.example
├── .gitignore
└── README.md
```

## Författare

- **Ali** - Frontend & Backend utveckling
- **Johan** - Frontend & Backend utveckling

---

**Examensarbete 2024-2025**

_Real-time Multiplayer Quiz Platform med fokus på WebSocket-teknologi_
