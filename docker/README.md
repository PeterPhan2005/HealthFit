# Docker Compose Configuration

## 🐳 Database Architecture

This project follows **Database per Service** pattern - a microservices best practice where each service has its own isolated database.

### Services & Databases

| Service | Container Name | Database | Port | User | Password |
|---------|---------------|----------|------|------|----------|
| Auth Service | `healthfit-auth-db` | `auth_db` | 5433 | `auth_user` | `auth_pass` |
| User Service | `healthfit-user-db` | `user_db` | 5434 | `user_user` | `user_pass` |
| Fitness Service | `healthfit-fitness-db` | `fitness_db` | 5435 | `fitness_user` | `fitness_pass` |
| Meal Service | `healthfit-meal-db` | `meal_db` | 5436 | `meal_user` | `meal_pass` |

## 🚀 Usage

### Start all databases
```bash
cd docker
docker-compose up -d
```

### Check status
```bash
docker-compose ps
```

### View logs
```bash
docker-compose logs -f
```

### Stop all databases
```bash
docker-compose down
```

### Stop and remove volumes (⚠️ deletes all data)
```bash
docker-compose down -v
```

## 🔍 Database Access

### Connect via psql
```bash
# Auth DB
docker exec -it healthfit-auth-db psql -U auth_user -d auth_db

# User DB
docker exec -it healthfit-user-db psql -U user_user -d user_db

# Fitness DB
docker exec -it healthfit-fitness-db psql -U fitness_user -d fitness_db

# Meal DB
docker exec -it healthfit-meal-db psql -U meal_user -d meal_db
```

### Connect via DBeaver/pgAdmin
Use the ports and credentials from the table above with `localhost` as the host.

## 📊 Database Schema

### auth_db (Auth Service)
- `users` - User authentication data

### user_db (User Service)
- `user_profiles` - User profile information
- `user_metrics` - Historical health metrics
- `user_goals` - User fitness goals

### fitness_db (Fitness Service)
- `exercises` - Exercise catalog
- `workout_plans` - Workout plan templates
- `workout_plan_days` - Daily workout schedules
- `workout_plan_exercises` - Exercises in workout plans
- `workout_logs` - User workout history

### meal_db (Meal Service)
- `meals` - Meal catalog
- `meal_plans` - Meal plan templates
- `meal_plan_meals` - Meals in meal plans

## 🏗️ Architecture Benefits

✅ **Independence**: Each service owns its data
✅ **Scalability**: Scale databases independently
✅ **Fault Isolation**: One DB failure doesn't affect others
✅ **Technology Diversity**: Can use different DB types per service
✅ **Clear Boundaries**: Enforces microservices separation
