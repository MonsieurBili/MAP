# DuckNetwork - Social Network Application

A Java-based social networking application that manages users (persons and ducks), friendships, and race events. The application supports both file-based and PostgreSQL database storage.

## Features

- **User Management**: Support for two types of users - Persons and Ducks
- **Friendship System**: Create and manage friendships between users
- **Race Events**: Organize swimming races for ducks with lane management
- **Dual Storage Modes**: 
  - File-based storage (text files)
  - PostgreSQL database storage
- **Statistics**: Community analysis and network diameter calculations
- **Card System**: User card management

## Technologies

- **Language**: Java 17+
- **Build Tool**: Gradle 8.14
- **Database**: PostgreSQL 12+ (optional)
- **JDBC Driver**: PostgreSQL JDBC 42.7.1
- **Testing**: JUnit 5

## Project Structure

```
MAP/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── Database/          # Database connection and initialization
│   │   │   ├── Domain/            # Entity classes (User, Person, Duck, etc.)
│   │   │   ├── Repository/        # Data access layer (file & database)
│   │   │   ├── Service/           # Business logic layer
│   │   │   ├── UI/                # User interface
│   │   │   ├── Validators/        # Input validation
│   │   │   └── org/example/       # Main application entry point
│   │   └── resources/
│   │       ├── schema.sql         # PostgreSQL database schema
│   │       └── *.txt              # Text file storage (default mode)
├── build.gradle.kts               # Gradle build configuration
├── db.properties.example          # Database configuration template
├── DATABASE_SETUP.md              # Database setup guide
├── INTEGRATION_GUIDE.md           # Step-by-step integration guide
└── README.md                      # This file
```

## Getting Started

### Prerequisites

- Java 17 or higher
- Gradle (included via wrapper)
- PostgreSQL 12+ (optional, only for database mode)

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/MonsieurBili/MAP.git
   cd MAP
   ```

2. **Build the project**
   ```bash
   ./gradlew build
   ```

3. **Run the application** (file-based mode)
   ```bash
   ./gradlew run
   ```

## Storage Modes

### File-Based Storage (Default)

Data is stored in text files in the `src/main/resources/` directory:
- `persoane.txt` - Person data
- `rate.txt` - Duck data
- `event.txt` - Race event data
- `friendship.txt` - Friendship relationships

**No additional setup required!**

### PostgreSQL Database Storage

For persistent, queryable storage using PostgreSQL:

1. **Install PostgreSQL**
   ```bash
   # Ubuntu/Debian
   sudo apt install postgresql postgresql-contrib
   
   # macOS
   brew install postgresql
   ```

2. **Create Database**
   ```bash
   sudo -u postgres psql
   ```
   ```sql
   CREATE DATABASE ducknetwork;
   CREATE USER duckuser WITH PASSWORD 'duckpass';
   GRANT ALL PRIVILEGES ON DATABASE ducknetwork TO duckuser;
   ```

3. **Configure Connection**
   - Copy `db.properties.example` to `db.properties`
   - Update with your credentials

4. **Enable Database Mode**
   - Open `src/main/java/org/example/Main.java`
   - Change `USE_DATABASE = false` to `USE_DATABASE = true`
   - Rebuild and run: `./gradlew clean run`

**Detailed instructions**: See [INTEGRATION_GUIDE.md](INTEGRATION_GUIDE.md)

## Documentation

- **[DATABASE_SETUP.md](DATABASE_SETUP.md)** - Complete database setup and configuration guide
- **[INTEGRATION_GUIDE.md](INTEGRATION_GUIDE.md)** - Step-by-step integration and testing guide

## Usage

### Switching Storage Modes

Edit `src/main/java/org/example/Main.java`:

```java
// File-based storage (default)
private static final boolean USE_DATABASE = false;

// PostgreSQL database storage
private static final boolean USE_DATABASE = true;
```

### Running Tests

```bash
./gradlew test
```

### Building JAR

```bash
./gradlew jar
```

The JAR file will be in `build/libs/`

## Database Schema

The PostgreSQL schema includes:
- **users** - Base user table with common attributes
- **persons** - Person-specific information
- **ducks** - Duck-specific information (type, speed, resistance)
- **friendships** - User relationships
- **race_events** - Race event details
- **race_event_lanes** - Lane information for races
- **race_event_participants** - Duck registrations for races
- **cards** - User card information

See [schema.sql](src/main/resources/schema.sql) for complete schema definition.

## Key Classes

### Domain Layer
- `User` - Abstract base class for all users
- `Persoana` - Person entity with personal information
- `Duck` - Abstract duck entity with racing attributes
- `Friendship` - Relationship between users
- `RaceEvent` - Swimming race event with lanes and participants

### Repository Layer
- `Repository<ID, E>` - Generic repository interface
- `RepositoryEntity<ID, E>` - In-memory repository implementation
- `PersonDatabaseRepository` - PostgreSQL repository for persons
- `DuckDatabaseRepository` - PostgreSQL repository for ducks
- `FriendshipDatabaseRepository` - PostgreSQL repository for friendships
- `RaceEventDatabaseRepository` - PostgreSQL repository for race events

### Service Layer
- `ServicePerson` - Person business logic
- `ServiceDuck` - Duck business logic
- `ServiceFriendship` - Friendship management
- `ServiceRaceEvent` - Race event management and simulation
- `ServiceStatistics` - Community analysis and statistics

## Features in Detail

### User Management
- Create and manage person profiles (name, email, birth date, occupation)
- Create and manage duck profiles (type, speed, resistance)
- User authentication (username, password)

### Friendship System
- Add/remove friendships between any users
- Query user's friend list
- Network analysis (connected components, diameter)

### Race Events
- Create swimming races with multiple lanes
- Register swimming ducks as participants
- Intelligent lane assignment algorithm
- Race simulation with winner calculation
- Observer pattern for race notifications

### Statistics
- Calculate number of communities (connected components)
- Find community with maximum diameter
- Analyze social network structure

## Development

### Building from Source

```bash
# Clean build
./gradlew clean build

# Run tests
./gradlew test

# Generate Javadoc
./gradlew javadoc

# Check dependencies
./gradlew dependencies
```

### Code Structure

The application follows a layered architecture:
1. **Domain Layer** - Entity classes and business objects
2. **Repository Layer** - Data access abstraction
3. **Service Layer** - Business logic
4. **UI Layer** - User interaction
5. **Database Layer** - Database connectivity (PostgreSQL)

## Configuration

### Database Configuration (db.properties)

```properties
# PostgreSQL JDBC URL
db.url=jdbc:postgresql://localhost:5432/ducknetwork

# Database credentials
db.username=duckuser
db.password=duckpass
```

**Security Note**: Never commit `db.properties` with real credentials!

### Application Configuration

Edit `Main.java` to configure:
- Storage mode (file-based or database)
- File paths for text-based storage
- Repository implementations

## Troubleshooting

### Common Issues

**"Connection refused" when using database mode**
- Ensure PostgreSQL is running
- Verify port 5432 is accessible
- Check `db.properties` configuration

**"Authentication failed"**
- Verify database user credentials
- Check PostgreSQL `pg_hba.conf` settings

**Build fails**
- Ensure Java 17+ is installed
- Run `./gradlew clean build`

See [INTEGRATION_GUIDE.md](INTEGRATION_GUIDE.md#troubleshooting) for detailed troubleshooting.

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Run tests: `./gradlew test`
5. Submit a pull request

## License

This project is part of the MAP (Advanced Programming Methods) course.

## Authors

- MonsieurBili - [GitHub](https://github.com/MonsieurBili)

## Acknowledgments

- PostgreSQL team for the excellent database system
- PostgreSQL JDBC driver maintainers
- MAP course instructors

---

For detailed setup and integration instructions, see:
- [DATABASE_SETUP.md](DATABASE_SETUP.md) - Database installation and configuration
- [INTEGRATION_GUIDE.md](INTEGRATION_GUIDE.md) - Step-by-step integration guide
