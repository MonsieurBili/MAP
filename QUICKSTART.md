# Quick Start Guide - PostgreSQL Integration

This guide will get you up and running with PostgreSQL database in under 5 minutes.

## Option 1: File-Based Storage (No Setup Required)

The application works out of the box with file-based storage:

```bash
./gradlew run
```

That's it! Your data is stored in text files.

---

## Option 2: PostgreSQL Database (Recommended for Production)

### Step 1: Install PostgreSQL (2 minutes)

**Ubuntu/Debian:**
```bash
sudo apt update && sudo apt install postgresql postgresql-contrib
```

**macOS:**
```bash
brew install postgresql && brew services start postgresql
```

**Windows:**
Download from: https://www.postgresql.org/download/windows/

### Step 2: Create Database (1 minute)

```bash
# Connect to PostgreSQL
sudo -u postgres psql

# Run these commands (copy-paste all at once)
CREATE DATABASE ducknetwork;
CREATE USER duckuser WITH PASSWORD 'duckpass';
GRANT ALL PRIVILEGES ON DATABASE ducknetwork TO duckuser;
\c ducknetwork
GRANT ALL ON SCHEMA public TO duckuser;
\q
```

### Step 3: Configure Application (30 seconds)

Create `db.properties` in project root:
```properties
db.url=jdbc:postgresql://localhost:5432/ducknetwork
db.username=duckuser
db.password=duckpass
```

Or just copy the example:
```bash
cp db.properties.example db.properties
```

### Step 4: Enable Database Mode (15 seconds)

Edit `src/main/java/org/example/Main.java`:

Change line 13 from:
```java
private static final boolean USE_DATABASE = false;
```

To:
```java
private static final boolean USE_DATABASE = true;
```

### Step 5: Run! (15 seconds)

```bash
./gradlew clean run
```

You should see:
```
=== Starting application with PostgreSQL Database ===
Initializing database schema...
Database connection established successfully.
Database schema initialized successfully.
Database repositories initialized successfully.
```

**Done!** Your application is now using PostgreSQL database.

---

## Switching Back to File-Based Storage

Just change `USE_DATABASE = true` back to `USE_DATABASE = false` in Main.java.

---

## Verifying Database

Connect to database and query:
```bash
psql -U duckuser -d ducknetwork
```

```sql
-- View all tables
\dt

-- Check users
SELECT * FROM users;

-- Exit
\q
```

---

## Troubleshooting

### "Connection refused"
- Is PostgreSQL running? `sudo systemctl status postgresql`
- Check db.properties credentials

### "Permission denied"
- Grant schema access: 
  ```sql
  \c ducknetwork
  GRANT ALL ON SCHEMA public TO duckuser;
  ```

### "Database does not exist"
- Create it: `sudo -u postgres createdb ducknetwork`

---

## Next Steps

1. ✅ Application is running with PostgreSQL
2. 📖 Read [INTEGRATION_GUIDE.md](INTEGRATION_GUIDE.md) for detailed testing
3. 📖 Read [DATABASE_SETUP.md](DATABASE_SETUP.md) for advanced configuration
4. 🔒 Never commit `db.properties` with real credentials!

---

## Full Documentation

- **[README.md](README.md)** - Project overview and features
- **[DATABASE_SETUP.md](DATABASE_SETUP.md)** - Detailed database setup
- **[INTEGRATION_GUIDE.md](INTEGRATION_GUIDE.md)** - Complete integration guide with testing and troubleshooting

---

**That's it!** You now have a fully functional PostgreSQL-backed social network application. 🎉
