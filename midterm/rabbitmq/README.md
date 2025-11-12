# RabbitMQ cho iBanking Microservices 🐰

Message broker để giao tiếp bất đồng bộ giữa các services.

## 🚀 Start RabbitMQ

```powershell
# Vào thư mục rabbitmq
cd d:\TODO-HK120252026\KienTrucHuongDV\midterm\rabbitmq

# Start RabbitMQ
docker-compose up -d

# Check logs
docker-compose logs -f
```

## ✅ Kiểm tra RabbitMQ đã chạy

### 1. Check Container Status
```powershell
docker ps | Select-String rabbitmq
```

Bạn sẽ thấy:
```
CONTAINER ID   IMAGE                       STATUS         PORTS
abc123def456   rabbitmq:3.13-management   Up 10 seconds   0.0.0.0:5672->5672/tcp, 0.0.0.0:15672->15672/tcp
```

### 2. Truy cập Management UI
- **URL**: http://localhost:15672
- **Username**: `admin`
- **Password**: `admin123`

### 3. Test Connection
```powershell
Test-NetConnection -ComputerName localhost -Port 5672
Test-NetConnection -ComputerName localhost -Port 15672
```

Nếu thấy `TcpTestSucceeded : True` → RabbitMQ đã sẵn sàng! ✅

## 📊 Management UI - Các Tab quan trọng

### Overview Tab
- Tổng quan về RabbitMQ
- Số lượng connections, channels, queues
- Message rates (messages/second)

### Queues Tab
- Xem tất cả queues
- Số lượng messages đang chờ
- Consumer đang lắng nghe

**Sau khi tạo NOTIFICATION-SERVICE, bạn sẽ thấy queue:** `payment.completed`

### Exchanges Tab
- Xem tất cả exchanges
- Type: direct, topic, fanout, headers

**Sau khi tạo services, bạn sẽ thấy exchange:** `payment.exchange`

## 🔄 Luồng hoạt động

```
┌──────────────────┐
│ PAYMENT-SERVICE  │  (Publisher)
└────────┬─────────┘
         │ Publish message
         ▼
┌─────────────────────────┐
│      RabbitMQ           │
│  ┌─────────────────┐    │
│  │ payment.exchange│    │
│  └────────┬────────┘    │
│           │             │
│           ▼             │
│  ┌─────────────────┐    │
│  │ payment.completed│   │  (Queue)
│  │ Messages: 5      │   │
│  └────────┬────────┘    │
└───────────┼─────────────┘
            │ Consume
            ▼
┌─────────────────────┐
│ NOTIFICATION-SERVICE│  (Consumer)
│ @RabbitListener     │
└─────────────────────┘
```

## 🛠️ Useful Commands

### Start RabbitMQ
```powershell
docker-compose up -d
```

### Stop RabbitMQ
```powershell
docker-compose down
```

### Restart RabbitMQ
```powershell
docker-compose restart
```

### View Logs
```powershell
docker-compose logs -f rabbitmq
```

### Remove Everything (including data)
```powershell
docker-compose down -v
```

## 📡 Connection Details

**Cho Spring Boot Applications:**
```properties
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=admin
spring.rabbitmq.password=admin123
```

## 🚨 Troubleshooting

### Port 5672 already in use
```powershell
# Check what's using port 5672
netstat -ano | Select-String "5672"

# Stop existing RabbitMQ
docker stop rabbitmq
docker rm rabbitmq
```

### Port 15672 already in use
```powershell
# Check what's using port 15672
netstat -ano | Select-String "15672"

# Kill process if needed
Stop-Process -Id <PID> -Force
```

### Cannot connect to Management UI
1. Wait 10-20 seconds after `docker-compose up`
2. Check logs: `docker-compose logs rabbitmq`
3. Restart: `docker-compose restart`

### "Authentication failed" khi login
- Username: `admin` (không phải guest!)
- Password: `admin123`
- Clear browser cache nếu cần

## 📝 Notes

- **Port 5672**: Application connection (PAYMENT-SERVICE, NOTIFICATION-SERVICE)
- **Port 15672**: Web UI (for you to monitor)
- **Data**: Được lưu trong Docker volume `rabbitmq_data`
- **Network**: `rabbitmq-network` (cho các services kết nối)

## 🎯 Next Steps

Sau khi RabbitMQ chạy thành công:
1. ✅ Tạo NOTIFICATION-SERVICE
2. ✅ Cấu hình RabbitMQ trong NOTIFICATION-SERVICE
3. ✅ Update PAYMENT-SERVICE để publish messages
4. ✅ Test flow: Payment → RabbitMQ → Send Email

## 💡 Tips

### Xem Messages trong Queue
1. Vào http://localhost:15672
2. Tab "Queues"
3. Click vào queue name (e.g., `payment.completed`)
4. Scroll xuống "Get messages"
5. Click "Get Message(s)" để xem message content

### Purge Queue (Xóa hết messages)
1. Tab "Queues"
2. Click queue name
3. Click "Purge Messages"
4. Confirm

### Delete Queue
1. Tab "Queues"
2. Click queue name
3. Scroll xuống "Delete"
4. Confirm

---

## ✅ Checklist

- [ ] `docker-compose up -d` thành công
- [ ] `docker ps` thấy container `rabbitmq` running
- [ ] Vào http://localhost:15672 thành công
- [ ] Login với admin/admin123 thành công
- [ ] Thấy Overview dashboard

→ Sẵn sàng tạo NOTIFICATION-SERVICE! 🚀
