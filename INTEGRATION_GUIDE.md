# PostgreSQL Integration Guide

This guide provides step-by-step instructions for integrating and testing PostgreSQL database with the DuckNetwork application.

## Table of Contents
1. [Quick Start](#quick-start)
2. [Database Setup](#database-setup)
3. [Switching Between Storage Modes](#switching-between-storage-modes)
4. [Testing the Integration](#testing-the-integration)
5. [Data Migration](#data-migration)
6. [Troubleshooting](#troubleshooting)

---

## Quick Start

### Prerequisites
- Java 17 or higher
- PostgreSQL 12 or higher
- Gradle (included via wrapper)

### 3-Step Setup

1. **Install PostgreSQL** (if not already installed)
   ```bash
   # Ubuntu/Debian
   sudo apt update && sudo apt install postgresql postgresql-contrib
   
   # macOS
   brew install postgresql
   brew services start postgresql
   ```

2. **Create Database**
   ```bash
   # Connect to PostgreSQL
   sudo -u postgres psql
   
   # Execute these commands in psql
   CREATE DATABASE ducknetwork;
   CREATE USER duckuser WITH PASSWORD 'duckpass';
   GRANT ALL PRIVILEGES ON DATABASE ducknetwork TO duckuser;
   \q
   ```

3. **Configure Application**
   - Copy `db.properties.example` to `db.properties`
   - Update with your database credentials
   - Change `USE_DATABASE = true` in `Main.java`

---

## Database Setup

### Step 1: Install PostgreSQL

#### Ubuntu/Debian:
```bash
sudo apt update
sudo apt install postgresql postgresql-contrib
sudo systemctl start postgresql
sudo systemctl enable postgresql
```

#### macOS:
```bash
brew install postgresql
brew services start postgresql
```

#### Windows:
Download and install from: https://www.postgresql.org/download/windows/

### Step 2: Create Database and User

Connect to PostgreSQL as the postgres user:
```bash
sudo -u postgres psql
```

Execute the following SQL commands:
```sql
-- Create the database
CREATE DATABASE ducknetwork;

-- Create a user
CREATE USER duckuser WITH PASSWORD 'duckpass';

-- Grant privileges
GRANT ALL PRIVILEGES ON DATABASE ducknetwork TO duckuser;

-- Connect to the database
\c ducknetwork

-- Grant schema privileges (PostgreSQL 15+)
GRANT ALL ON SCHEMA public TO duckuser;

-- Exit psql
\q
```

### Step 3: Configure Connection

Create a `db.properties` file in the project root:

```properties
db.url=jdbc:postgresql://localhost:5432/ducknetwork
db.username=duckuser
db.password=duckpass
```

**Important:** Never commit `db.properties` with real credentials to version control!

### Step 4: Verify Connection

The application will automatically test the connection when starting in database mode. You can also test manually:

```bash
psql -U duckuser -d ducknetwork -h localhost
```

If successful, you should see the PostgreSQL prompt.

---

## Switching Between Storage Modes

The application supports two storage modes:

### File-Based Storage (Default)
- Data stored in text files in `src/main/resources/`
- Files: `persoane.txt`, `rate.txt`, `event.txt`, `friendship.txt`
- No database required

### Database Storage (PostgreSQL)
- Data stored in PostgreSQL database
- Persistent and queryable
- Supports concurrent access

### How to Switch

1. **Open** `src/main/java/org/example/Main.java`

2. **Locate** the configuration flag (around line 13):
   ```java
   private static final boolean USE_DATABASE = false;
   ```

3. **Change** to your desired mode:
   - `USE_DATABASE = false` - File-based storage (default)
   - `USE_DATABASE = true` - PostgreSQL database storage

4. **Save** and rebuild:
   ```bash
   ./gradlew clean build
   ```

5. **Run** the application:
   ```bash
   ./gradlew run
   ```

---

## Testing the Integration

### Automated Tests

Run all tests to ensure the integration works:
```bash
./gradlew test
```

### Manual Testing Checklist

#### 1. Test Database Connection

**Start application in database mode:**
```bash
# Set USE_DATABASE = true in Main.java
./gradlew run
```

**Expected output:**
```
=== Starting application with PostgreSQL Database ===
Initializing database schema...
Using default database configuration. Create 'db.properties' to customize.
Database connection established successfully.
Database schema initialized successfully.
Database repositories initialized successfully.
```

#### 2. Test Person CRUD Operations

Using the application UI:
1. Add a new person
2. List all persons
3. Update a person's information
4. Delete a person
5. Verify changes persist after restarting the application

#### 3. Test Duck CRUD Operations

1. Add a new duck (flying/swimming/both)
2. List all ducks
3. Update duck attributes
4. Delete a duck
5. Verify data persistence

#### 4. Test Friendship Operations

1. Create friendships between users
2. List all friendships
3. Test friendship removal
4. Check that friendship data persists

#### 5. Test Race Events

1. Create a race event
2. Add lanes to the event
3. Register ducks as participants
4. Run the race simulation
5. Verify event data is saved

#### 6. Verify Data in Database

Connect to the database and query directly:
```bash
psql -U duckuser -d ducknetwork
```

Run SQL queries:
```sql
-- Check users
SELECT * FROM users;

-- Check persons
SELECT u.username, p.first_name, p.last_name 
FROM users u 
JOIN persons p ON u.id = p.id;

-- Check ducks
SELECT u.username, d.duck_type, d.speed, d.resistance 
FROM users u 
JOIN ducks d ON u.id = d.id;

-- Check friendships
SELECT f.id, u1.username as user1, u2.username as user2 
FROM friendships f 
JOIN users u1 ON f.user1_id = u1.id 
JOIN users u2 ON f.user2_id = u2.id;

-- Check race events
SELECT * FROM race_events;
```

---

## Data Migration

### Migrating from Files to Database

If you have existing data in text files and want to migrate to PostgreSQL:

#### Option 1: Using the Application

1. **Start with file-based mode** (USE_DATABASE = false)
2. **Load all data** from text files (happens automatically)
3. **Switch to database mode** (USE_DATABASE = true)
4. **Restart the application** - schema will be initialized
5. **Manually re-enter data** through the UI

#### Option 2: Programmatic Migration

Create a migration script:

```java
package org.example;

import Database.DatabaseInitializer;
import Repository.*;
import Validators.*;
import Domain.Person.Persoana;
import Domain.Ducks.Duck;

import java.sql.SQLException;

public class DataMigration {
    public static void main(String[] args) {
        try {
            // Initialize database
            DatabaseInitializer.initializeSchema("src/main/resources/schema.sql");
            
            // Create validators
            PersonValidator personValidator = new PersonValidator();
            DuckValidator duckValidator = new DuckValidator();
            
            // Load from files
            RepositoryPerson filePersonRepo = new RepositoryPerson(
                personValidator, "src/main/resources/persoane.txt");
            RepositoryDuck fileDuckRepo = new RepositoryDuck(
                duckValidator, "src/main/resources/rate.txt");
            
            // Create database repositories
            PersonDatabaseRepository dbPersonRepo = new PersonDatabaseRepository(personValidator);
            DuckDatabaseRepository dbDuckRepo = new DuckDatabaseRepository(duckValidator);
            
            // Migrate persons
            System.out.println("Migrating persons...");
            int personCount = 0;
            for (Persoana person : filePersonRepo.findAll()) {
                dbPersonRepo.save(person);
                personCount++;
            }
            System.out.println("Migrated " + personCount + " persons.");
            
            // Migrate ducks
            System.out.println("Migrating ducks...");
            int duckCount = 0;
            for (Duck duck : fileDuckRepo.findAll()) {
                dbDuckRepo.save(duck);
                duckCount++;
            }
            System.out.println("Migrated " + duckCount + " ducks.");
            
            System.out.println("Migration completed successfully!");
            
        } catch (SQLException e) {
            System.err.println("Migration failed: " + e.getMessage());
        }
    }
}
```

Save this as `src/main/java/org/example/DataMigration.java` and run:
```bash
./gradlew run -PmainClass=org.example.DataMigration
```

### Migrating from Database to Files

Export data from database:
```bash
psql -U duckuser -d ducknetwork -c "\COPY (SELECT username, password, email, last_name, first_name, date_of_birth, occupation FROM users u JOIN persons p ON u.id = p.id) TO 'persoane_export.txt' WITH CSV"
```

---

## Troubleshooting

### Problem: "Connection refused"

**Symptoms:**
```
Database connection test failed: Connection refused
```

**Solutions:**
1. Verify PostgreSQL is running:
   ```bash
   # Linux
   sudo systemctl status postgresql
   
   # macOS
   brew services list
   ```

2. Check if PostgreSQL is listening on port 5432:
   ```bash
   sudo netstat -plnt | grep 5432
   ```

3. Verify connection settings in `db.properties`

### Problem: "Authentication failed"

**Symptoms:**
```
Database initialization failed: authentication failed for user "duckuser"
```

**Solutions:**
1. Verify credentials in `db.properties` match database user
2. Check PostgreSQL `pg_hba.conf` for authentication method
3. Reset user password:
   ```sql
   ALTER USER duckuser WITH PASSWORD 'newpassword';
   ```

### Problem: "Database does not exist"

**Symptoms:**
```
Database initialization failed: database "ducknetwork" does not exist
```

**Solutions:**
1. Create the database:
   ```bash
   sudo -u postgres createdb ducknetwork
   ```
2. Or use psql:
   ```sql
   CREATE DATABASE ducknetwork;
   ```

### Problem: "Permission denied for schema public"

**Symptoms:**
```
ERROR: permission denied for schema public
```

**Solutions:**
```sql
-- Connect to the database
\c ducknetwork

-- Grant schema privileges
GRANT ALL ON SCHEMA public TO duckuser;
GRANT ALL ON ALL TABLES IN SCHEMA public TO duckuser;
```

### Problem: Application starts in file mode despite USE_DATABASE = true

**Solutions:**
1. Verify you saved `Main.java` after changing the flag
2. Rebuild the project: `./gradlew clean build`
3. Check for compilation errors in the output

### Problem: "Table already exists" error

**Symptoms:**
```
ERROR: relation "users" already exists
```

**Solutions:**
The schema initialization is idempotent. If you see this error, you can:
1. Drop and recreate the database:
   ```sql
   DROP DATABASE ducknetwork;
   CREATE DATABASE ducknetwork;
   ```
2. Or manually drop tables:
   ```sql
   DROP SCHEMA public CASCADE;
   CREATE SCHEMA public;
   GRANT ALL ON SCHEMA public TO duckuser;
   ```

### Getting Help

If you encounter issues not covered here:
1. Check the application logs for detailed error messages
2. Verify PostgreSQL logs: `/var/log/postgresql/postgresql-*.log`
3. Refer to `DATABASE_SETUP.md` for detailed setup instructions
4. Check PostgreSQL documentation: https://www.postgresql.org/docs/

---

## Performance Tips

### For Development
- Use local PostgreSQL instance
- Keep default connection pool settings
- Enable query logging for debugging

### For Production
- Configure connection pooling
- Set up proper indexes (already included in schema.sql)
- Use read replicas for heavy read loads
- Regular database backups
- Monitor query performance

### Connection Pool Configuration (Future Enhancement)
Consider adding HikariCP for production:
```gradle
implementation 'com.zaxxer:HikariCP:5.0.1'
```

---

## Next Steps

After successful integration:
1. ✅ Test all CRUD operations
2. ✅ Verify data persistence across restarts
3. ✅ Backup your database regularly
4. ✅ Monitor application logs for errors
5. ✅ Consider implementing connection pooling for production
6. ✅ Set up automated database backups
7. ✅ Document any custom queries or procedures

---

## Additional Resources

- [PostgreSQL Official Documentation](https://www.postgresql.org/docs/)
- [JDBC PostgreSQL Driver Documentation](https://jdbc.postgresql.org/documentation/)
- [PostgreSQL Best Practices](https://wiki.postgresql.org/wiki/Don%27t_Do_This)
- [SQL Tutorials](https://www.postgresqltutorial.com/)
