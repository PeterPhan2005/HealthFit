# iBanking Microservices - Complete System 🚀

Complete microservices architecture cho hệ thống iBanking với Docker Compose.

## 📊 System Architecture

```
┌─────────────────────────────────────────────────────────┐
│                    USER / CLIENT                        │
└────────────────────┬────────────────────────────────────┘
                     │
          ┌──────────┴──────────┐
          │                     │
    ┌─────▼──────┐      ┌──────▼──────┐
    │ AUTH       │      │ CUSTOMER    │
    │ SERVICE    │      │ SERVICE     │
    │ Port: 8081 │      │ Port: 8082  │
    └─────┬──────┘      └──────┬──────┘
          │                    │
          │         ┌──────────▼──────────┐
          │         │ PAYMENT SERVICE     │
          │         │ Port: 8084          │
          │         │ (OpenFeign)         │
          │         └──────────┬──────────┘
          │                    │
          │                    │ Publish
          │                    ▼
    ┌─────▼──────┐      ┌─────────────┐
    │ STUDENT    │      │  RABBITMQ   │
    │ SERVICE    │◄─────┤  Port: 5672 │
    │ Port: 8083 │      └──────┬──────┘
    └────────────┘             │ Consume
                               ▼
                    ┌─────────────────────┐
                    │ NOTIFICATION        │
                    │ SERVICE             │
                    │ Port: 8086          │
                    │ (Email)             │
                    └─────────────────────┘
```

## 🎯 Services Overview

| Service | Port | Database Port | Description |
|---------|------|---------------|-------------|
| **auth-service** | 8081 | 5433 | Authentication & JWT |
| **customer-service** | 8082 | 5434 | Customer & Balance |
| **student-service** | 8083 | 5435 | Student & Fees |
| **payment-service** | 8084 | 5436 | Payment Processing (OpenFeign) |
| **otp-service** | 8085 | 5437 | OTP Generation |
| **notification-service** | 8086 | N/A | Email Notifications (RabbitMQ) |
| **rabbitmq** | 5672, 15672 | N/A | Message Broker |

## 🚀 Quick Start

### Prerequisites
- Docker Desktop installed
- Docker Compose installed
- At least 8GB RAM
- Ports 5433-5437, 5672, 8081-8086, 15672 available

### Start All Services

```powershell
# Clone repository
cd d:\TODO-HK120252026\KienTrucHuongDV\midterm

# Start all services
docker-compose up --build

# Or run in background
docker-compose up --build -d
```

### Check Services Status

```powershell
# View all containers
docker-compose ps

# View logs
docker-compose logs -f

# View specific service logs
docker-compose logs -f payment-service
docker-compose logs -f notification-service
```

## 📡 Service URLs

### Applications
- **AUTH**: http://localhost:8081/actuator/health
- **CUSTOMER**: http://localhost:8082/actuator/health
- **STUDENT**: http://localhost:8083/actuator/health
- **PAYMENT**: http://localhost:8084/actuator/health
- **OTP**: http://localhost:8085/actuator/health
- **NOTIFICATION**: http://localhost:8086/actuator/health

### RabbitMQ Management
- **URL**: http://localhost:15672
- **Username**: admin
- **Password**: admin123

### Databases (via localhost)
- **auth-db**: localhost:5433
- **customer-db**: localhost:5434
- **student-db**: localhost:5435
- **payment-db**: localhost:5436
- **otp-db**: localhost:5437

## 🧪 Testing Full Flow

### 1. Login (AUTH-SERVICE)
```bash
POST http://localhost:8081/api/auth/login
Content-Type: application/json

{
  "username": "john",
  "password": "john123"
}

# Response: Copy JWT token
```

### 2. Check Balance (CUSTOMER-SERVICE)
```bash
GET http://localhost:8082/api/customers/balance
Authorization: Bearer {token}

# Response: { "balance": "20.000.000 ₫" }
```

### 3. Search Student (STUDENT-SERVICE)
```bash
GET http://localhost:8083/api/students/search/523H1001
Authorization: Bearer {token}

# Response: Student info with fee
```

### 4. Process Payment (PAYMENT-SERVICE)
```bash
POST http://localhost:8084/api/payments/pay
Authorization: Bearer {token}
Content-Type: application/json

{
  "studentId": "523H1001"
}

# Response: Payment successful
# → PAYMENT-SERVICE publishes message to RabbitMQ
# → NOTIFICATION-SERVICE consumes message
# → Email sent to customer
```

### 5. Check RabbitMQ
```
http://localhost:15672
→ Tab "Queues" 
→ See queue "payment.completed"
→ Messages processed: 1
```

### 6. Check Email
- Login to customer's email
- See email: "✅ Thanh toán học phí thành công - iBanking"

## 🔄 Service Dependencies

```
auth-db ────→ auth-service
customer-db ────→ customer-service
student-db ────→ student-service
payment-db ────┐
customer-service ──┼──→ payment-service ──→ rabbitmq ──→ notification-service
student-service ──┘
otp-db ────→ otp-service
```

## 🛠️ Management Commands

### Stop All Services
```powershell
docker-compose down
```

### Stop and Remove Volumes (Clean Start)
```powershell
docker-compose down -v
```

### Restart Specific Service
```powershell
docker-compose restart payment-service
```

### Rebuild Specific Service
```powershell
docker-compose up --build payment-service
```

### Scale Service (if needed)
```powershell
docker-compose up --scale notification-service=3
```

### View Resource Usage
```powershell
docker stats
```

## 📊 Monitoring

### Check Health Endpoints
```powershell
# Check all services
Invoke-RestMethod http://localhost:8081/actuator/health
Invoke-RestMethod http://localhost:8082/actuator/health
Invoke-RestMethod http://localhost:8083/actuator/health
Invoke-RestMethod http://localhost:8084/actuator/health
Invoke-RestMethod http://localhost:8085/actuator/health
Invoke-RestMethod http://localhost:8086/actuator/health
```

### RabbitMQ Monitoring
1. Open http://localhost:15672
2. **Overview Tab**: System metrics
3. **Queues Tab**: Message rates, consumers
4. **Connections Tab**: Active connections

### Database Connections
```powershell
# Connect to databases
docker exec -it auth-db psql -U postgres -d auth_db
docker exec -it customer-db psql -U postgres -d customer_db
docker exec -it student-db psql -U postgres -d student_db
docker exec -it payment-db psql -U postgres -d payment_db
docker exec -it otp-db psql -U postgres -d otp_db
```

## 🚨 Troubleshooting

### Service Won't Start
```powershell
# Check logs
docker-compose logs service-name

# Check if port is available
Test-NetConnection -ComputerName localhost -Port 8084
```

### Database Connection Failed
```powershell
# Check database is running
docker-compose ps

# Check database logs
docker-compose logs auth-db
```

### RabbitMQ Connection Failed
```powershell
# Check RabbitMQ is running
docker-compose ps rabbitmq

# Check RabbitMQ logs
docker-compose logs rabbitmq

# Restart RabbitMQ
docker-compose restart rabbitmq
```

### OpenFeign Connection Timeout
```powershell
# Check target service is running
docker-compose ps customer-service student-service

# Check network
docker network inspect midterm_ibanking-network
```

### Out of Memory
```powershell
# Check resource usage
docker stats

# Increase Docker Desktop memory:
# Docker Desktop → Settings → Resources → Memory → 8GB+
```

## 🔐 Environment Variables

### Database Credentials (Default)
```
POSTGRES_USER: postgres
POSTGRES_PASSWORD: postgres
```

### RabbitMQ Credentials
```
RABBITMQ_DEFAULT_USER: admin
RABBITMQ_DEFAULT_PASS: admin123
```

### Email Configuration
```
SPRING_MAIL_USERNAME: lananhoutlier@gmail.com
SPRING_MAIL_PASSWORD: avnszmemzhttknau
```

## 📝 Development Workflow

### 1. Make Code Changes
```powershell
# Edit code in your IDE
code ./payment-service
```

### 2. Rebuild Service
```powershell
docker-compose up --build payment-service
```

### 3. Test Changes
```powershell
# Test API endpoint
curl http://localhost:8084/api/payments/pay
```

### 4. View Logs
```powershell
docker-compose logs -f payment-service
```

## 🎓 Key Features

### ✅ Microservices Pattern
- Each service independent
- Own database per service
- Loosely coupled

### ✅ Synchronous Communication (OpenFeign)
- PAYMENT-SERVICE → CUSTOMER-SERVICE
- PAYMENT-SERVICE → STUDENT-SERVICE
- Type-safe REST calls

### ✅ Asynchronous Communication (RabbitMQ)
- PAYMENT-SERVICE → RabbitMQ → NOTIFICATION-SERVICE
- Event-driven architecture
- Reliable message delivery

### ✅ Service Discovery (via Docker Network)
- Services discover each other by container name
- No hardcoded IPs

### ✅ Health Checks
- All services expose `/actuator/health`
- Docker healthcheck for databases and RabbitMQ

## 🔄 Data Flow Example

### Payment Flow:
```
1. User → AUTH-SERVICE
   POST /api/auth/login
   ← JWT token

2. User → PAYMENT-SERVICE
   POST /api/payments/pay
   Header: Authorization: Bearer {token}
   Body: { "studentId": "523H1001" }

3. PAYMENT-SERVICE → CUSTOMER-SERVICE (OpenFeign)
   GET /api/customers/{id}
   ← Customer info + balance

4. PAYMENT-SERVICE → STUDENT-SERVICE (OpenFeign)
   GET /api/students/{studentId}
   ← Student info + tuition fee

5. PAYMENT-SERVICE → CUSTOMER-SERVICE (OpenFeign)
   PUT /api/customers/{id}/deduct
   Body: { "amount": 3500000 }
   ← Balance updated

6. PAYMENT-SERVICE → RabbitMQ
   Publish PaymentEvent to "payment.exchange"
   
7. RabbitMQ → NOTIFICATION-SERVICE
   Deliver PaymentEvent from "payment.completed" queue
   
8. NOTIFICATION-SERVICE → Gmail SMTP
   Send email confirmation to customer
   
9. User ← PAYMENT-SERVICE
   Response: { "success": true, "transactionId": 123 }
```

## 📦 Volumes

Data persistence:
- `auth_data`: Auth database data
- `customer_data`: Customer database data
- `student_data`: Student database data
- `payment_data`: Payment database data
- `otp_data`: OTP database data
- `rabbitmq_data`: RabbitMQ messages and config

## 🌐 Network

All services communicate via `ibanking-network` bridge network.

Services can reach each other by container name:
- `http://auth-service:8081`
- `http://customer-service:8082`
- `http://student-service:8083`
- etc.

## 💡 Tips

### Speed Up Builds
```powershell
# Build without cache (clean build)
docker-compose build --no-cache

# Pull latest base images
docker-compose pull
```

### Clean Up
```powershell
# Remove stopped containers
docker-compose rm

# Remove unused images
docker image prune

# Remove unused volumes
docker volume prune
```

### Debug Specific Service
```powershell
# Run service in foreground
docker-compose up payment-service

# Execute command in running container
docker-compose exec payment-service bash
```

## 🎉 Success Checklist

- [ ] `docker-compose ps` shows all services running
- [ ] All health checks return `UP`
- [ ] RabbitMQ Management UI accessible
- [ ] Can login via AUTH-SERVICE
- [ ] Can process payment via PAYMENT-SERVICE
- [ ] Message appears in RabbitMQ queue
- [ ] NOTIFICATION-SERVICE consumes message
- [ ] Email received in inbox

---

**🚀 Happy Coding with iBanking Microservices!**

For issues, check logs:
```powershell
docker-compose logs -f
```
