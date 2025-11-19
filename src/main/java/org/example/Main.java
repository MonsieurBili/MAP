package org.example;

import Repository.*;
import Service.*;
import UI.Ui;
import Validators.*;

/**
 * Main application entry point.
 * Initializes all validators, repositories, services, and starts the user interface.
 */
public class Main {
    
    /**
     * Application entry point.
     * Sets up the dependency injection manually and starts the UI.
     * 
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        // Initialize validators
        PersonValidator personValidator = new PersonValidator();
        DuckValidator duckValidator = new DuckValidator();
        FriendshipValidator friendshipValidator = new FriendshipValidator();
        EventValidator eventValidator = new EventValidator();
        CardValidator cardValidator = new CardValidator();
        
        // Initialize repositories with file paths from Constants
        RepositoryPerson personRepository = new RepositoryPerson(personValidator, Constants.PERSONS_FILE_PATH);
        RepositoryDuck duckRepository = new RepositoryDuck(duckValidator, Constants.DUCKS_FILE_PATH);
        FriendshipRepository friendshipRepository = new FriendshipRepository(friendshipValidator);
        RepositoryCard cardRepository = new RepositoryCard(cardValidator);
        RepositoryRaceEvent repositoryRaceEvent = new RepositoryRaceEvent(Constants.EVENTS_FILE_PATH, eventValidator);
        
        // Get singleton ID generator
        IdGenerator generatorId = IdGenerator.getInstance();
        
        // Initialize services
        ServicePerson personService = new ServicePerson(generatorId, personRepository);
        ServiceDuck duckService = new ServiceDuck(generatorId, duckRepository);
        ServiceFriendship friendshipService = new ServiceFriendship(generatorId, friendshipRepository);
        ServiceStatistics serviceStatistics = new ServiceStatistics(
            duckRepository, 
            personRepository, 
            friendshipRepository, 
            Constants.FRIENDSHIP_FILE_PATH
        );
        ServiceCard serviceCard = new ServiceCard(generatorId, cardRepository);
        ServiceRaceEvent serviceRaceEvent = new ServiceRaceEvent(repositoryRaceEvent);
        
        // Initialize and run the UI
        Ui ui = new Ui(personService, duckService, friendshipService, serviceStatistics, serviceCard, serviceRaceEvent);
        ui.run();
    }
}