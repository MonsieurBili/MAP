# Database Setup Guide

This document provides instructions for setting up and integrating PostgreSQL database with the DuckNetwork application.

## Prerequisites

- PostgreSQL 12 or higher installed on your system
- Java 17 or higher
- Gradle build tool

## Database Installation

### On Ubuntu/Debian:
```bash
sudo apt update
sudo apt install postgresql postgresql-contrib
```

### On macOS:
```bash
brew install postgresql
brew services start postgresql
```

### On Windows:
Download and install PostgreSQL from: https://www.postgresql.org/download/windows/

## Database Configuration

### 1. Create Database

Connect to PostgreSQL:
```bash
sudo -u postgres psql
```

Create the database:
```sql
CREATE DATABASE ducknetwork;
CREATE USER duckuser WITH PASSWORD 'duckpass';
GRANT ALL PRIVILEGES ON DATABASE ducknetwork TO duckuser;
\q
```

### 2. Initialize Schema

The application includes a schema initialization script. You can initialize it in two ways:

#### Option A: Using psql command line
```bash
psql -U duckuser -d ducknetwork -f src/main/resources/schema.sql
```

#### Option B: Programmatically (Recommended)
The application can initialize the schema automatically. See the "Integration" section below.

### 3. Configure Database Connection

Create a `db.properties` file in the project root directory:

```properties
db.url=jdbc:postgresql://localhost:5432/ducknetwork
db.username=duckuser
db.password=duckpass
```

**Note:** If this file doesn't exist, the application will use default values:
- URL: `jdbc:postgresql://localhost:5432/ducknetwork`
- Username: `postgres`
- Password: `postgres`

**Security:** Add `db.properties` to `.gitignore` to prevent committing sensitive credentials.

## Integration with Application

### Using Database Repositories

The application now supports two modes of operation:

1. **In-Memory Mode (Default)** - Uses the existing file-based repositories
2. **Database Mode** - Uses PostgreSQL database repositories

### Switching to Database Mode

To use the database repositories, modify the `Main.java` file:

#### Before (In-Memory):
```java
RepositoryPerson personRepository = new RepositoryPerson(personValidator, "src/main/resources/persoane.txt");
RepositoryDuck duckRepository = new RepositoryDuck(duckValidator, "src/main/resources/rate.txt");
FriendshipRepository friendshipRepository = new FriendshipRepository(friendshipValidator);
RepositoryRaceEvent repositoryRaceEvent = new RepositoryRaceEvent("src/main/resources/event.txt", eventValidator);
```

#### After (Database):
```java
// Initialize database schema (run once)
try {
    DatabaseInitializer.initializeSchema("src/main/resources/schema.sql");
} catch (SQLException e) {
    System.err.println("Failed to initialize database: " + e.getMessage());
    return;
}

// Use database repositories
PersonDatabaseRepository personRepository = new PersonDatabaseRepository(personValidator);
DuckDatabaseRepository duckRepository = new DuckDatabaseRepository(duckValidator);
FriendshipDatabaseRepository friendshipRepository = new FriendshipDatabaseRepository(friendshipValidator, personRepository);
RaceEventDatabaseRepository repositoryRaceEvent = new RaceEventDatabaseRepository(eventValidator);
```

## Database Schema

The application uses the following tables:

- **users** - Base table for all users (persons and ducks)
- **persons** - Person-specific information
- **ducks** - Duck-specific information
- **friendships** - User relationships
- **race_events** - Race event information
- **race_event_lanes** - Race lanes for events
- **race_event_participants** - Duck participants in races
- **cards** - User cards

For detailed schema information, see `src/main/resources/schema.sql`.

## Data Migration

### Migrating from Text Files to Database

If you have existing data in text files, you can migrate it to the database:

1. Start the application with database repositories
2. The data from text files can be loaded and saved to the database using the service layer
3. Alternatively, write a migration script to bulk import data

### Example Migration Code:
```java
// Load from files
RepositoryPerson fileRepo = new RepositoryPerson(personValidator, "src/main/resources/persoane.txt");

// Initialize database
DatabaseInitializer.initializeSchema("src/main/resources/schema.sql");
PersonDatabaseRepository dbRepo = new PersonDatabaseRepository(personValidator);

// Migrate data
for (Persoana person : fileRepo.findAll()) {
    dbRepo.save(person);
}
```

## Troubleshooting

### Connection Issues

**Problem:** "Connection refused" error

**Solution:** 
- Verify PostgreSQL is running: `sudo systemctl status postgresql` (Linux) or `brew services list` (macOS)
- Check that PostgreSQL is listening on port 5432
- Verify firewall settings allow local connections

### Authentication Issues

**Problem:** "authentication failed" error

**Solution:**
- Verify credentials in `db.properties` match the database user
- Check PostgreSQL `pg_hba.conf` configuration for proper authentication method

### Schema Issues

**Problem:** Tables don't exist

**Solution:**
- Run the schema initialization script manually or through the application
- Verify the database user has permissions to create tables

## Performance Considerations

- The application uses prepared statements to prevent SQL injection
- Indexes are created on frequently queried columns
- Transactions are used for data consistency
- Connection pooling can be added for better performance in production

## Security Best Practices

1. **Never commit** `db.properties` with actual credentials
2. Use **strong passwords** for database users
3. Limit database user **privileges** to only what's needed
4. Use **SSL/TLS** for database connections in production
5. Regularly **backup** your database
6. Keep PostgreSQL **updated** with security patches

## Backup and Restore

### Backup:
```bash
pg_dump -U duckuser ducknetwork > backup.sql
```

### Restore:
```bash
psql -U duckuser ducknetwork < backup.sql
```

## Additional Resources

- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [JDBC PostgreSQL Driver Documentation](https://jdbc.postgresql.org/documentation/)
- [PostgreSQL Java Tutorial](https://www.postgresqltutorial.com/postgresql-jdbc/)
