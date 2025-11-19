package org.example;

import Database.DatabaseInitializer;
import Domain.Ducks.Duck;
import Domain.Person.Persoana;
import Repository.*;
import Service.*;
import UI.Ui;
import Validators.*;

import java.sql.SQLException;

public class Main {
    // Configuration flag: set to true to use PostgreSQL database, false to use file-based storage
    private static final boolean USE_DATABASE = false;
    
    public static void main(String[] args) {
        // Initialize validators
        PersonValidator personValidator = new PersonValidator();
        DuckValidator duckValidator = new DuckValidator();
        FriendshipValidator friendshipValidator = new FriendshipValidator();
        EventValidator eventValidator = new EventValidator();
        CardValidator cardValidator = new CardValidator();
        
        // Initialize repositories based on configuration
        Repository<Long, Persoana> personRepository;
        Repository<Long, Duck> duckRepository;
        Repository<Long, Domain.Friendship> friendshipRepository;
        Repository<Long, Domain.RaceEvent> repositoryRaceEvent;
        
        if (USE_DATABASE) {
            System.out.println("=== Starting application with PostgreSQL Database ===");
            
            // Initialize database schema
            try {
                System.out.println("Initializing database schema...");
                DatabaseInitializer.initializeSchema("src/main/resources/schema.sql");
                
                if (!DatabaseInitializer.testConnection()) {
                    System.err.println("Failed to establish database connection.");
                    System.err.println("Please check DATABASE_SETUP.md for setup instructions.");
                    return;
                }
            } catch (SQLException e) {
                System.err.println("Database initialization failed: " + e.getMessage());
                System.err.println("Please check DATABASE_SETUP.md for setup instructions.");
                return;
            }
            
            // Create database repositories
            personRepository = new PersonDatabaseRepository(personValidator);
            duckRepository = new DuckDatabaseRepository(duckValidator);
            friendshipRepository = new FriendshipDatabaseRepository(friendshipValidator, personRepository);
            repositoryRaceEvent = new RaceEventDatabaseRepository(eventValidator);
            
            System.out.println("Database repositories initialized successfully.");
        } else {
            System.out.println("=== Starting application with File-based Storage ===");
            
            // Create file-based repositories (original implementation)
            personRepository = new RepositoryPerson(personValidator, "src/main/resources/persoane.txt");
            duckRepository = new RepositoryDuck(duckValidator, "src/main/resources/rate.txt");
            friendshipRepository = new FriendshipRepository(friendshipValidator);
            repositoryRaceEvent = new RepositoryRaceEvent("src/main/resources/event.txt", eventValidator);
        }
        
        // Initialize card repository (always file-based for now)
        RepositoryCard cardRepository = new RepositoryCard(cardValidator);
        
        // Initialize ID generator
        IdGenerator generatorId = IdGenerator.getInstance();
        
        // Initialize services
        ServicePerson personService = new ServicePerson(generatorId, personRepository);
        ServiceDuck duckService = new ServiceDuck(generatorId, duckRepository);
        ServiceFriendship friendshipService = new ServiceFriendship(generatorId, friendshipRepository);
        ServiceStatistics serviceStatistics = new ServiceStatistics(duckRepository, personRepository, friendshipRepository, "src/main/resources/friendship.txt");
        ServiceCard serviceCard = new ServiceCard(generatorId, cardRepository);
        ServiceRaceEvent serviceRaceEvent = new ServiceRaceEvent(repositoryRaceEvent);
        
        // Start UI
        Ui ui = new Ui(personService, duckService, friendshipService, serviceStatistics, serviceCard, serviceRaceEvent);
        ui.run();
    }
}