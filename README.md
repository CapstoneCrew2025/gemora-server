# 💎 Gemora Backend

> A secure and transparent digital marketplace backend for gemstone trading — built with **Spring Boot 3**, **MySQL**, **JWT authentication**, **WebSocket real-time chat**, **AI gem prediction**, and an **auction engine**.

---

## Table of Contents

- [Project Overview](#-project-overview)
- [Tech Stack](#-tech-stack)
- [Architecture](#-architecture)
- [Design Principles](#-design-principles)
- [Folder Structure](#-folder-structure)
- [Modules & Responsibilities](#-modules--responsibilities)
- [Database & Entities](#-database--entities)
- [Security](#-security)
- [API Endpoints](#-api-endpoints)
- [WebSocket](#-websocket-real-time-chat)
- [File Storage](#-file-storage)
- [Scheduled Tasks](#-scheduled-tasks)
- [Error Handling](#-error-handling)
- [Environment Variables](#-environment-variables)
- [Setup & Installation](#-setup--installation)
- [Running the Project](#-running-the-project)
- [Default Admin Account](#-default-admin-account)
- [Testing](#-testing)
- [Contributing](#-contributing)
- [License](#-license)

---

## 📌 Project Overview

**Gemora** is a full-stack gemstone trading platform. This repository contains the **Spring Boot backend** which provides:

- User registration with KYC identity verification (ID images + selfie)
- JWT-based authentication and role-based access (USER / ADMIN)
- Gem listing management with admin approval workflow
- Certificate upload and admin verification
- Fixed-price sale and auction-based gem listings
- Real-time bidding with an automated auction scheduler
- Buyer–Seller real-time chat via WebSocket (STOMP/SockJS)
- AI-powered gem prediction (image recognition via external model)
- AI chatbot assistant powered by Google Gemini API
- Support ticket system with admin reply
- User profile management
- Password reset via OTP email

---

## 🛠 Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.5.6 |
| Database | MySQL 8 |
| ORM | Spring Data JPA / Hibernate |
| Security | Spring Security 6 + JWT (jjwt 0.12.6) |
| Real-time | WebSocket (STOMP + SockJS) |
| Reactive Client | Spring WebFlux (WebClient) |
| Email | Spring Mail (Gmail SMTP) |
| AI Integration | Google Gemini API |
| Build Tool | Maven (+ mvnw wrapper) |
| Utilities | Lombok |
| Testing | JUnit 5, Spring Boot Test, Spring Security Test |

---

## 🏛 Architecture

Gemora backend follows a **classic layered (n-tier) architecture**:

```
┌─────────────────────────────────────────────────────┐
│                  Client (Frontend / Postman)         │
└────────────────────────┬────────────────────────────┘
                         │  HTTP / WebSocket
┌────────────────────────▼────────────────────────────┐
│            Controller Layer  (REST + WebSocket)      │
│  AuthController · GemController · BidController      │
│  ChatRestController · ChatWebSocketController        │
│  AdminGemController · AdminUserController            │
│  ProfileController · SupportTicketController         │
│  GeminiChatController · GemPredictController         │
└────────────────────────┬────────────────────────────┘
                         │
┌────────────────────────▼────────────────────────────┐
│               Service Layer  (Business Logic)        │
│  AuthService · GemService · BidService               │
│  ChatMessageService · ProfileService                 │
│  AdminUserService · SupportTicketService             │
│  GeminiChatService · GemPredictService               │
│  FileStorageService · EmailService                   │
└────────────────────────┬────────────────────────────┘
                         │
┌────────────────────────▼────────────────────────────┐
│            Repository Layer  (Spring Data JPA)       │
│  UserRepo · GemRepo · BidRepo · CertificateRepo      │
│  GemImageRepo · ChatMessageRepo · SupportTicketRepo  │
│  PasswordResetOtpRepo                                │
└────────────────────────┬────────────────────────────┘
                         │
┌────────────────────────▼────────────────────────────┐
│                MySQL Database                        │
└─────────────────────────────────────────────────────┘
```

Cross-cutting concerns are handled by:
- **`SecurityConfig`** — JWT filter chain, CORS, CSRF, session policy
- **`JwtAuthenticationFilter`** — JWT extraction & validation on every request
- **`GlobalExceptionHandler`** — Centralised error responses
- **`AuctionScheduler`** — Periodic auction expiry checks

---

## 🎯 Design Principles

| Principle | How it's applied |
|---|---|
| **Separation of Concerns** | Controllers only handle HTTP input/output; all logic lives in the Service layer |
| **Single Responsibility** | Each class has one focused job (e.g. `JwtUtil` only deals with token operations) |
| **Interface-based Programming** | All services are defined as interfaces (`GemService`, `AuthService`, etc.) with `impl/` implementations — easy to swap or mock |
| **DTO Pattern** | Entities are never returned directly; dedicated DTOs control what data leaves or enters the API |
| **Stateless Authentication** | `SessionCreationPolicy.STATELESS` — no server-side sessions; every request is authenticated via JWT |
| **Builder Pattern** | Entities and DTOs use Lombok `@Builder` for clean, readable object construction |
| **DRY / Encapsulation** | Shared JWT logic in `JwtUtil`; shared file I/O in `FileStorageService`; shared email logic in `EmailService` |
| **Fail-Fast Exception Handling** | Custom exceptions (`BusinessException`, `ResourceNotFoundException`, `UnauthorizedException`) bubble up and are caught by `GlobalExceptionHandler` |

---

## 📁 Folder Structure

```
gemora-server/
├── src/
│   ├── main/
│   │   ├── java/com/gemora_server/
│   │   │   ├── GemoraServerApplication.java       ← Entry point (@EnableScheduling)
│   │   │   │
│   │   │   ├── config/                            ← Spring configuration beans
│   │   │   │   ├── SecurityConfig.java            ← Security filter chain, CORS, JWT setup
│   │   │   │   ├── WebConfig.java                 ← Static resource handlers (/uploads/**)
│   │   │   │   ├── WebSocketConfig.java           ← STOMP/SockJS WebSocket broker
│   │   │   │   ├── WebClientConfig.java           ← WebClient bean (Gemini API)
│   │   │   │   └── DataSeeder.java                ← Seeds default admin on startup
│   │   │   │
│   │   │   ├── controller/                        ← REST + WebSocket controllers
│   │   │   │   ├── AuthController.java            ← /api/auth/**
│   │   │   │   ├── GemController.java             ← /api/gems/**
│   │   │   │   ├── AdminGemController.java        ← /api/admin/gems/**
│   │   │   │   ├── AdminUserController.java       ← /api/admin/users/**
│   │   │   │   ├── BidController.java             ← /api/bids/**
│   │   │   │   ├── ChatRestController.java        ← /api/chat/**
│   │   │   │   ├── ChatWebSocketController.java   ← WebSocket /app/chat.send
│   │   │   │   ├── ProfileController.java         ← /api/profile/**
│   │   │   │   ├── SupportTicketController.java   ← /api/tickets/**
│   │   │   │   ├── GeminiChatController.java      ← /api/ai/ask
│   │   │   │   ├── GemPredictController.java      ← /api/predict
│   │   │   │   ├── EmailTestController.java       ← /api/email/test (dev only)
│   │   │   │   └── TestController.java            ← /public/test
│   │   │   │
│   │   │   ├── service/                           ← Service interfaces
│   │   │   │   ├── impl/                          ← Service implementations
│   │   │   │   └── ...
│   │   │   │
│   │   │   ├── repo/                              ← Spring Data JPA repositories
│   │   │   ├── entity/                            ← JPA entities (database tables)
│   │   │   ├── dto/                               ← Request / Response data transfer objects
│   │   │   ├── enums/                             ← GemStatus, ListingType, TicketStatus, etc.
│   │   │   ├── exception/                         ← Custom exceptions + GlobalExceptionHandler
│   │   │   ├── filter/                            ← JwtAuthenticationFilter
│   │   │   ├── scheduler/                         ← AuctionScheduler (runs every hour)
│   │   │   └── util/                              ← JwtUtil (token generation & validation)
│   │   │
│   │   └── resources/
│   │       └── application.properties             ← All app config (DB, JWT, Mail, Gemini)
│   │
│   └── test/
│       └── java/com/gemora_server/
│           └── AuthControllerSecurityTests.java
│
├── uploads/                                       ← Runtime file storage (gitignored)
│   ├── gems/                                      ← Gem listing images
│   ├── certificates/                              ← Certificate documents
│   └── users/                                     ← KYC identity images
│
├── pom.xml                                        ← Maven build & dependencies
├── mvnw / mvnw.cmd                                ← Maven wrapper scripts
└── README.md
```

---

## 🧩 Modules & Responsibilities

### Config
| Class | Purpose |
|---|---|
| `SecurityConfig` | Defines the security filter chain: CORS, CSRF disabled, stateless sessions, public vs protected routes, BCrypt password encoder |
| `WebConfig` | Maps `/uploads/**` and `/files/**` URL patterns to `file:./uploads/` on disk |
| `WebSocketConfig` | Registers STOMP endpoint `/ws-chat` with SockJS fallback; simple broker on `/topic` and `/queue` |
| `WebClientConfig` | Provides a pre-configured `WebClient` bean targeting the Gemini API base URL |
| `DataSeeder` | `CommandLineRunner` that creates the default admin account (`admin@gemora.com`) if it doesn't exist |

### Controllers
| Controller | Base Path | Auth Required |
|---|---|---|
| `AuthController` | `/api/auth` | ❌ Public |
| `TestController` | `/public/test` | ❌ Public |
| `GemController` | `/api/gems` | ✅ (most endpoints) |
| `AdminGemController` | `/api/admin/gems` | ✅ Admin |
| `AdminUserController` | `/api/admin/users` | ✅ Admin |
| `BidController` | `/api/bids` | ✅ (place bid) |
| `ChatRestController` | `/api/chat` | ✅ |
| `ChatWebSocketController` | WebSocket `/app/chat.send` | ✅ |
| `ProfileController` | `/api/profile` | ✅ |
| `SupportTicketController` | `/api/tickets` | ✅ |
| `GeminiChatController` | `/api/ai` | ✅ |
| `GemPredictController` | `/api/predict` | ✅ |

### Services
| Service | Key Responsibility |
|---|---|
| `AuthService` | Register (with KYC file saves), login, forgot-password OTP, reset-password |
| `GemService` | Create/update/delete gems, image & certificate upload, approve/reject flow, SOLD status |
| `BidService` | Place bid (validates auction state, updates highest bid), fetch bids, auction countdown |
| `ChatMessageService` | Save messages, chat history, inbox, mark-as-read, soft-delete per user |
| `ProfileService` | Get/update user profile (name, contact, selfie), mark gem as sold |
| `AdminUserService` | List all users, get/update/delete by admin |
| `SupportTicketService` | Create ticket, list own tickets, list all (admin), admin reply & status update |
| `FileStorageService` | Persist uploaded files to disk under `uploads/`, return relative URL |
| `EmailService` | Send OTP email via Gmail SMTP |
| `GeminiChatService` | Forward user message to Google Gemini REST API, return AI response |
| `GemPredictService` | Send image to external ML prediction endpoint, return gem classification |

---

## 🗄 Database & Entities

### Entity Map

```
users ──┐
        ├──< gems >──< gem_images
        │       └──< certificates
        │       └──< bids ──> users (bidder)
        │       └──< orders ──> users (buyer)
        │
        ├──< support_tickets (userId FK)
        └──< chat_messages (senderId / receiverId)
```

### Entity Summary

| Entity | Table | Key Fields |
|---|---|---|
| `User` | `users` | id, name, email, password, role (`USER`/`ADMIN`), contactNumber, idFrontImageUrl, idBackImageUrl, selfieImageUrl, createdAt |
| `Gem` | `gems` | id, name, description, category, carat, origin, price, status (`PENDING/APPROVED/REJECTED/SOLD`), listingType (`SALE/AUCTION/SOLD`), auctionEndTime, currentHighestBid, seller → User |
| `GemImage` | `gem_images` | id, url, fileName, gem → Gem |
| `Certificate` | `certificates` | id, certificateNumber, issuingAuthority, issueDate, fileUrl, verified, verifiedBy, gem → Gem |
| `Bid` | `bids` | id, amount, placedAt, gem → Gem, bidder → User |
| `Order` | `orders` | id, amount, createdAt, status, buyer → User, gem → Gem |
| `SupportTicket` | `support_tickets` | id, title, description, userId, status (`OPEN/IN_PROGRESS/CLOSED`), priority (`LOW/MEDIUM/HIGH`), adminReply, createdAt, updatedAt |
| `ChatMessage` | `chat_messages` | id, senderId, receiverId, content, sentAt, status, roomId, gemId, deletedBySender, deletedByReceiver |
| `PasswordResetOtp` | `password_reset_otps` | id, email, otp, expiresAt |

### Enums

| Enum | Values |
|---|---|
| `GemStatus` | `PENDING`, `APPROVED`, `REJECTED`, `SOLD` |
| `ListingType` | `SALE`, `AUCTION`, `SOLD` |
| `TicketStatus` | `OPEN`, `IN_PROGRESS`, `CLOSED` |
| `TicketPriority` | `LOW`, `MEDIUM`, `HIGH` |
| `ChatMessageStatus` | `SENT`, `READ` |

---

## 🔐 Security

### Authentication Flow

```
Client                       Server
  │                             │
  │── POST /api/auth/login ────>│
  │                             │  Validate credentials
  │                             │  Generate JWT (userId + email claims, HS256)
  │<── { token: "eyJ..." } ────│
  │                             │
  │── GET /api/gems/mine ──────>│ Authorization: Bearer eyJ...
  │                    JwtAuthenticationFilter extracts token
  │                    Validates signature + expiry
  │                    Sets SecurityContext with email principal
  │<── 200 [gems] ────────────-│
```

### Public vs Protected Routes

| Pattern | Access |
|---|---|
| `OPTIONS /**` | Public (CORS preflight) |
| `/api/auth/**` | Public |
| `/public/**` | Public |
| `/uploads/**` | Public (served static files) |
| `/files/**` | Public (backward-compat alias) |
| `/error`, `/`, `/index.html`, `/favicon.ico`, `/css/**`, `/js/**`, `/images/**` | Public |
| Everything else | **Requires valid JWT** |

### JWT Token Claims

| Claim | Value |
|---|---|
| `sub` | User email |
| `userId` | User database ID (Long) |
| `email` | User email |
| Algorithm | HS256 (HMAC-SHA256) |
| Expiry | Configurable via `jwt.expiration-ms` |

### Password Encoding

All passwords are hashed with **BCrypt** before storage.

---

## 🌐 API Endpoints

### 🔑 Auth  —  `/api/auth`  (No authentication required)

| Method | Endpoint | Content-Type | Description |
|---|---|---|---|
| POST | `/api/auth/register` | `multipart/form-data` | Register new user with KYC files |
| POST | `/api/auth/login` | `application/json` | Login and receive JWT token |
| POST | `/api/auth/forgot-password` | `application/json` | Send OTP to registered email |
| POST | `/api/auth/reset-password` | `application/json` | Verify OTP and set new password |

**Register — form-data fields:**

| Field | Type | Required |
|---|---|---|
| name | text | ✅ |
| email | text | ✅ |
| password | text | ✅ |
| contactNumber | text | ✅ |
| idFrontImage | file | ❌ |
| idBackImage | file | ❌ |
| selfieImage | file | ❌ |

> ⚠️ **Important:** `/api/auth/register` uses `multipart/form-data`. Sending `application/json` will result in a `400 Bad Request`.

**Login — JSON body:**
```json
{
  "email": "binoj@gemora.com",
  "password": "12345"
}
```

**Login — Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "message": "Login successful"
}
```

---

### 💎 Gems  —  `/api/gems`  (Authentication required)

| Method | Endpoint | Content-Type | Description |
|---|---|---|---|
| POST | `/api/gems` | `multipart/form-data` | Create a new gem listing |
| GET | `/api/gems/approved` | — | Get all approved gems (marketplace) |
| GET | `/api/gems/mine` | — | Get logged-in user's gems |
| GET | `/api/gems/{id}` | — | Get single gem by ID |
| PUT | `/api/gems/{gemId}` | `multipart/form-data` | Update gem listing |
| DELETE | `/api/gems/{id}` | — | Delete own gem |
| POST | `/api/gems/{gemId}/certificate` | `multipart/form-data` | Upload certificate for gem |

**Create Gem — form-data fields:**

| Field | Type | Notes |
|---|---|---|
| name | text | Gem name |
| description | text | Detailed description |
| category | text | e.g. Ruby, Sapphire |
| carat | number | Weight in carats |
| origin | text | Country of origin |
| price | number | Listing price |
| listingType | text | `SALE` or `AUCTION` |
| auctionEndTime | text | ISO datetime (auctions only) |
| images | files | Multiple gem images |
| certificateFile | file | Optional certificate |

---

### 🛡 Admin — Gems  —  `/api/admin/gems`  (Authentication required)

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/admin/gems/pending` | List all PENDING gems |
| GET | `/api/admin/gems/approved` | List all APPROVED gems |
| PUT | `/api/admin/gems/{id}/approve` | Approve a gem listing |
| PUT | `/api/admin/gems/{id}/reject?reason=...` | Reject a gem listing |
| PUT | `/api/admin/gems/certificate/{id}/verify?verified=true` | Verify/unverify a certificate |
| DELETE | `/api/admin/gems/{id}` | Force-delete any gem |

---

### 👥 Admin — Users  —  `/api/admin/users`  (Authentication required)

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/admin/users` | List all users |
| GET | `/api/admin/users/{userId}` | Get user by ID |
| PUT | `/api/admin/users/{userId}` | Update user details |
| DELETE | `/api/admin/users/{userId}` | Delete user |

---

### 🔨 Bids  —  `/api/bids`

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| POST | `/api/bids/place` | ✅ | Place a bid on an auction gem |
| GET | `/api/bids/gem/{gemId}` | ❌ | Get all bids for a gem |
| GET | `/api/bids/remaining-time/{gemId}` | ❌ | Get auction remaining time |

**Place Bid — JSON body:**
```json
{
  "gemId": 5,
  "amount": 1500.00
}
```

---

### 💬 Chat  —  `/api/chat`  (Authentication required)

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/chat/send` | Send a chat message |
| POST | `/api/chat/history` | Get chat history between two users for a gem |
| GET | `/api/chat/inbox` | Get all inbox conversations |
| POST | `/api/chat/mark-as-read?otherUserId=&gemId=` | Mark messages as read |
| DELETE | `/api/chat/delete?otherUserId=&gemId=` | Delete a conversation |

---

### 👤 Profile  —  `/api/profile`  (Authentication required)

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/profile` | Get logged-in user's profile |
| PUT | `/api/profile` | Update name, contactNumber, selfieImage |
| PUT | `/api/profile/mark-sold/{gemId}` | Mark a gem as sold |

---

### 🎫 Support Tickets  —  `/api/tickets`

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| POST | `/api/tickets` | ✅ | Create a support ticket |
| GET | `/api/tickets/my` | ✅ | Get current user's tickets |
| GET | `/api/tickets/admin` | ✅ Admin | Get all tickets |
| PUT | `/api/tickets/admin/{ticketId}/reply` | ✅ Admin | Reply to a ticket |

**Create Ticket — JSON body:**
```json
{
  "title": "Payment issue",
  "description": "I was charged twice for my order.",
  "priority": "HIGH"
}
```

---

### 🤖 AI Features

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| POST | `/api/ai/ask` | ✅ | Ask the Gemora AI assistant (Gemini) |
| POST | `/api/predict` | ✅ | Upload gem image for AI classification |

**Ask AI — JSON body:**
```json
{
  "message": "What is the best way to store a ruby?"
}
```

---

### 🧪 Public Test

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| GET | `/public/test` | ❌ | Health check — returns `"public ok"` |

---

## 📡 WebSocket Real-Time Chat

**Endpoint:** `ws://localhost:8080/ws-chat` (SockJS fallback supported)

| Direction | Destination |
|---|---|
| Client → Server | `/app/chat.send` |
| Server → Client | `/topic/chat/{roomId}` or `/queue/...` |

**Message Broker:** In-memory simple broker (topics: `/topic`, `/queue`)

**Connect example (JavaScript SockJS + STOMP):**
```javascript
const socket = new SockJS('http://localhost:8080/ws-chat');
const stompClient = Stomp.over(socket);
stompClient.connect({}, () => {
  stompClient.subscribe('/topic/chat/room123', (message) => {
    console.log(JSON.parse(message.body));
  });
});
```

---

## 📂 File Storage

Files are stored on disk in the `uploads/` directory at the project root:

| Directory | Purpose | Served At |
|---|---|---|
| `uploads/gems/` | Gem listing images | `/uploads/gems/{filename}` |
| `uploads/certificates/` | Certificate documents | `/uploads/certificates/{filename}` |
| `uploads/users/` | User KYC identity images | `/uploads/users/{filename}` |

Both `/uploads/**` and `/files/**` (backward-compat) URL patterns are publicly accessible and map to this directory.

> **Note:** The `uploads/` folder is excluded from version control via `.gitignore`.

---

## ⏱ Scheduled Tasks

| Scheduler | Schedule | Description |
|---|---|---|
| `AuctionScheduler` | Every 1 hour (`fixedRate = 3600000ms`) | Scans for expired APPROVED auction gems (where `auctionEndTime` is in the past) and resets them to `PENDING` status, clearing the bid data |

---

## ⚠️ Error Handling

All unhandled exceptions are caught by `GlobalExceptionHandler` (`@RestControllerAdvice`) and returned as a structured JSON error response:

```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Gem not found with id: 99",
  "timestamp": "2025-10-28T10:13:25"
}
```

Custom exception types:
- `GemoraException` — base exception with HTTP status
- `ResourceNotFoundException` — 404 for missing entities
- `BusinessException` — 400 for business rule violations
- `UnauthorizedException` — 401 for auth failures

---

## ⚙️ Environment Variables

Create `src/main/resources/application.properties` (copy from the template below — **do not commit real credentials to Git**):

```properties
# Application
spring.application.name=gemora-server
server.port=8080

# Database
spring.datasource.url=jdbc:mysql://localhost:3306/gemora_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
spring.datasource.username=YOUR_DB_USERNAME
spring.datasource.password=YOUR_DB_PASSWORD
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false

# JWT
jwt.secret=YourSuperSecretKeyAtLeast32CharactersLong
jwt.expiration-ms=86400000

# File Upload
file.upload-dir=uploads
spring.servlet.multipart.max-file-size=20MB
spring.servlet.multipart.max-request-size=25MB

# Email (Gmail SMTP)
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=YOUR_GMAIL_ADDRESS
spring.mail.password=YOUR_APP_PASSWORD
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

# Gemini AI
gemini.api.key=YOUR_GEMINI_API_KEY
gemini.api.url=https://generativelanguage.googleapis.com/v1/models/gemini-2.5-flash:generateContent
gemini.system.instruction=You are Gemora AI Assistant...
```

---

## 🚀 Setup & Installation

### Prerequisites

- Java 17+
- MySQL 8+
- Maven 3.8+ (or use the included `mvnw` wrapper)
- Git

### Steps

```bash
# 1. Clone the repository
git clone https://github.com/yourusername/gemora-backend.git
cd gemora-backend

# 2. Create the database (MySQL)
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS gemora_db;"

# 3. Configure application.properties
# Copy the template from the Environment Variables section above
# and fill in your credentials

# 4. Build the project
./mvnw clean package -DskipTests

# Windows
.\mvnw.cmd clean package -DskipTests
```

---

## ▶️ Running the Project

```bash
# Run with Maven wrapper
./mvnw spring-boot:run

# Windows
.\mvnw.cmd spring-boot:run

# Run the JAR directly
java -jar target/gemora-server-0.0.1-SNAPSHOT.jar
```

The server starts at: **`http://localhost:8080`**

> If port 8080 is already in use, change `server.port` in `application.properties` or kill the existing process.

---

## 🔑 Default Admin Account

On first startup, `DataSeeder` automatically creates a default admin account:

| Field | Value |
|---|---|
| Email | `admin@gemora.com` |
| Password | `admin123` |
| Role | `ADMIN` |

> ⚠️ Change this password immediately in a production environment.

---

## 🧪 Testing

```bash
# Run all tests
./mvnw test

# Windows
.\mvnw.cmd test
```

Test reports are generated under `target/surefire-reports/`.

Current test class: `AuthControllerSecurityTests` — covers security layer for auth endpoints.

---

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch: `git checkout -b feature/your-feature`
3. Commit your changes: `git commit -m 'Add your feature'`
4. Push to the branch: `git push origin feature/your-feature`
5. Open a Pull Request

**Coding conventions:**
- Follow the layered architecture: Controller → Service → Repository
- Always use DTOs — never expose entities directly from controllers
- Add service interface before implementation
- Run `./mvnw test` before submitting a PR

---

## 📄 License

This project is licensed under the MIT License — see the [LICENSE](LICENSE) file for details.

---

<div align="center">
  Built with ❤️ for the Gemora Capstone Project
</div>

