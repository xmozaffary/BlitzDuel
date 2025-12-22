# BlitzDuel

Real-time multiplayer quiz-plattform med WebSocket-teknologi.

## Innehållsförteckning

- [Om Projektet](#om-projektet)
- [Tech Stack](#tech-stack)
- [Features](#features)
- [Arkitektur](#arkitektur)
- [Installation & Setup](#installation--setup)
- [WebSocket API](#websocket-api)
- [Tester](#tester)
- [Författare](#författare)

## Om Projektet

BlitzDuel är en webbapplikation som möjliggör real-time quiz-dueller mellan två spelare. En spelare skapar en lobby och får en 6-siffrig kod som den andra spelaren använder för att joina. När båda är redo startar quizet och båda ser samma frågor samtidigt med en synkroniserad timer.

**Examensarbete 2024-2025** med fokus på:
- WebSocket-kommunikation med STOMP
- Real-time synkronisering mellan spelare
- Server-driven spellogik
- CI/CD med Docker och GitHub Actions

## Tech Stack

**Backend:** Java 21, Spring Boot 3.x, Spring WebSocket, STOMP, Spring Security, JPA, PostgreSQL

**Frontend:** React 19, TypeScript, Vite, SCSS, SockJS, STOMP.js, React Router

**DevOps:** Docker, GitHub Actions, Cloudflare Tunnel, Raspberry Pi 4 (ARM64)

## Features

- **Autentisering:** Google OAuth 2.0 + JWT-tokens
- **Lobby-system:** Skapa/joina med 6-siffrig kod, real-time uppdateringar
- **Quiz Gameplay:** Synkroniserade frågor, server-driven timer, live poäng, resultatskärm
- **Optimeringar:** JOIN FETCH för databas, responsiv design, automatisk seeding

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


## Installation & Setup

### Förutsättningar

- Node.js 20 eller senare
- Java 21 (JDK)
- Docker & Docker Compose
- Maven 3.9+
- Git

**1. Klona projektet**
```bash
git clone https://github.com/xmozaffary/BlitzDuel.git
cd BlitzDuel
```

**2. Konfigurera miljövariabler**
```bash
cp .env.example .env
# Redigera .env med Google OAuth credentials, JWT secret, etc.
```

**3. Starta med Docker Compose**
```bash
# Starta alla tjänster (databas, backend, frontend)
docker compose -f docker-compose.dev.yml up -d
```

**4. Eller kör lokalt:**
```bash
# Backend (i apps/backend/blitzduel)
./mvnw spring-boot:run

# Frontend (i apps/frontend/blitzduel)
npm install && npm run dev
```

Öppna `http://localhost:5173` och logga in med Google!

## WebSocket API

BlitzDuel använder STOMP över WebSocket (med SockJS fallback) för real-time kommunikation.

**Endpoints (Client → Server):**
- `/app/lobby/create` - Skapa lobby
- `/app/lobby/{code}/join` - Joina lobby
- `/app/game/{code}/start` - Starta spel
- `/app/game/{code}/answer` - Skicka svar

**Topics (Server → Client):**
- `/topic/lobby/{code}` - Broadcast till alla i lobby
- `/user/queue/lobby` - Privata meddelanden
- `/user/queue/errors` - Felmeddelanden

**Meddelandetyper:** `LOBBY_CREATED`, `PLAYER_JOINED`, `QUESTION`, `TIMER_UPDATE`, `BOTH_ANSWERED`, `GAME_OVER`

## Tester

Projektet innehåller omfattande tester för både backend och frontend.

**Backend (JUnit 5 + Mockito):**
- Service-tester: GameService, LobbyService, QuizService, UserService, TimerService, AnswerService
- Security-tester: JwtUtil, JwtAuthenticationFilter
- Controller-tester: LobbyController, GameController

**Frontend (Vitest + React Testing Library):**
- Komponent-tester: Button, QuizCard, AnswerButton, ScoreBoard, Spinner
- Hook-tester: useAuth
- Integrationstester: LoginPage

**Kör tester:**
```bash
# Backend
cd apps/backend/blitzduel
mvn test

# Frontend
cd apps/frontend/blitzduel
npm test
```

## Författare

**Ali** & **Johan** - Examensarbete 2024-2025
