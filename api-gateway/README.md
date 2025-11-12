# API Gateway

## Overview
API Gateway is the single entry point for all microservices in the HealthFit application. It routes requests to the appropriate backend services and handles cross-cutting concerns.

## Features
- ✅ **Route Management** - Routes requests to Auth Service and User Service
- ✅ **CORS Configuration** - Allows cross-origin requests from frontend apps
- ✅ **Cookie Forwarding** - Automatically forwards JWT cookies to backend services
- ✅ **Request Logging** - Logs all incoming requests and responses
- ✅ **Health Checks** - Actuator endpoints for monitoring

## Architecture

```
Client (Browser/Mobile)
    ↓
API Gateway :8080
    ├── /api/auth/**      → Auth Service :8081
    └── /api/users/**     → User Service :8082
```

## Port Configuration
- **API Gateway**: `8080` (public)
- **Auth Service**: `8081` (internal)
- **User Service**: `8082` (internal)

Frontend apps should **only** access port 8080.

## Routes

### Auth Service Routes
| Path | Target | Description |
|------|--------|-------------|
| `/api/auth/register` | http://localhost:8081/api/auth/register | User registration |
| `/api/auth/login` | http://localhost:8081/api/auth/login | User login (sets JWT cookie) |
| `/api/auth/logout` | http://localhost:8081/api/auth/logout | User logout (clears cookie) |
| `/api/auth/me` | http://localhost:8081/api/auth/me | Get current auth user |
| `/api/auth/health` | http://localhost:8081/api/auth/health | Auth service health |

### User Service Routes
| Path | Target | Description |
|------|--------|-------------|
| `/api/users/me` | http://localhost:8082/api/users/me | Get current user profile |
| `/api/users/{id}` | http://localhost:8082/api/users/{id}` | Get user by ID |
| `/api/users/{userId}/metrics` | http://localhost:8082/api/users/{userId}/metrics` | Health metrics endpoints |
| `/api/users/{userId}/goals` | http://localhost:8082/api/users/{userId}/goals` | Goals endpoints |

## Configuration

### application.properties
```properties
# Server
server.port=8080

# Routes
spring.cloud.gateway.routes[0].id=auth-service
spring.cloud.gateway.routes[0].uri=http://localhost:8081
spring.cloud.gateway.routes[0].predicates[0]=Path=/api/auth/**

spring.cloud.gateway.routes[1].id=user-service
spring.cloud.gateway.routes[1].uri=http://localhost:8082
spring.cloud.gateway.routes[1].predicates[0]=Path=/api/users/**

# CORS
spring.cloud.gateway.globalcors.cors-configurations.[/**].allowed-origins=http://localhost:3000
spring.cloud.gateway.globalcors.cors-configurations.[/**].allow-credentials=true
```

## Running the Gateway

### Prerequisites
1. **Auth Service** running on port 8081
2. **User Service** running on port 8082
3. **PostgreSQL** databases (auth_db, user_db)

### Start Gateway
```bash
cd api-gateway
mvn spring-boot:run
```

Gateway will start on **port 8080**.

## Testing

### 1. Health Check
```bash
curl http://localhost:8080/gateway/health
```

### 2. Login via Gateway
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "test@example.com", "password": "password123"}' \
  -c cookies.txt
```

This will:
- Route to auth-service:8081
- Set JWT cookie in response
- Return user data

### 3. Access User Service via Gateway
```bash
curl http://localhost:8080/api/users/me \
  -b cookies.txt
```

This will:
- Forward cookie to user-service:8082
- Validate JWT
- Return user profile

## Logging

Gateway logs all requests with details:
```
Gateway Request: GET /api/users/me from /127.0.0.1:54321
JWT Cookie present: eyJhbGciOiJIUzI1NiIsInR...
Gateway Response: /api/users/me with status 200 OK
```

## Actuator Endpoints

| Endpoint | Description |
|----------|-------------|
| `/actuator/health` | Gateway health status |
| `/actuator/gateway/routes` | View all configured routes |
| `/actuator/info` | Gateway application info |

Access: http://localhost:8080/actuator/gateway/routes

## CORS Configuration

Allowed origins:
- `http://localhost:3000` (React)
- `http://localhost:4200` (Angular)
- `http://localhost:5173` (Vite)

Allowed methods:
- GET, POST, PUT, DELETE, PATCH, OPTIONS

Allow credentials: **true** (required for cookies)

## Security

### JWT Cookie Handling
- Gateway **forwards** JWT cookies to backend services
- Backend services validate JWT independently
- No centralized authentication at gateway level (pass-through)

### Future Enhancements
- Rate limiting per user/IP
- JWT validation at gateway level
- Request/Response transformation
- Circuit breaker for backend services
- Service discovery (Eureka/Consul)

## Troubleshooting

### Gateway won't start
```bash
# Check if port 8080 is available
netstat -ano | findstr :8080

# Kill process using port 8080
taskkill /PID <pid> /F
```

### Routes not working
1. Check backend services are running (8081, 8082)
2. Check gateway logs for routing errors
3. Verify route configuration in application.properties
4. Test backend services directly first

### Cookies not forwarding
- Ensure `PreserveHostHeader` filter is enabled
- Check CORS `allow-credentials` is true
- Verify backend services accept cookies
- Test with `-v` flag in curl to see headers

## Development

### Add new service route
1. Update `application.properties`:
```properties
spring.cloud.gateway.routes[2].id=fitness-service
spring.cloud.gateway.routes[2].uri=http://localhost:8083
spring.cloud.gateway.routes[2].predicates[0]=Path=/api/fitness/**
```

2. Or use Java config in `GatewayConfig.java`:
```java
.route("fitness-service", r -> r
    .path("/api/fitness/**")
    .filters(f -> f.preserveHostHeader())
    .uri("http://localhost:8083")
)
```

3. Restart gateway

---

**Version**: 1.0.0  
**Port**: 8080  
**Status**: ✅ Running
