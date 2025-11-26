package org.example;

import Domain.Ducks.Duck;
import Domain.User;
import Repository.*;
import Repository.Database.RepositoryCardDB;
import Repository.Database.RepositoryDuckDB;
import Repository.Database.RepositoryFriendshipDb;
import Repository.Database.RepositoryPersonDB;
import Repository.Database.RepositoryRaceEventDB;
import Service.*;
import UI.Ui;
import Validators.*;

public class Main {
    public static void main(String[] args) {
        // Set to true to use database repositories, false to use in-memory repositories
        boolean useDatabase = true;

        PersonValidator personValidator = new PersonValidator();
        DuckValidator duckValidator = new DuckValidator();
        FriendshipValidator friendshipValidator = new FriendshipValidator();
        EventValidator eventValidator = new EventValidator();
        CardValidator cardValidator = new CardValidator();
        IdGenerator generatorId = IdGenerator.getInstance();

        if (useDatabase) {
            // Database repositories
            RepositoryPersonDB personRepository = new RepositoryPersonDB(personValidator);
            RepositoryDuckDB duckRepository = new RepositoryDuckDB(duckValidator);

            // Create a composite user repository for friendship lookups
            CompositeUserRepository userRepository = new CompositeUserRepository(personRepository, duckRepository);

            RepositoryFriendshipDb friendshipRepository = new RepositoryFriendshipDb(friendshipValidator, userRepository);
            RepositoryCardDB cardRepository = new RepositoryCardDB(cardValidator, duckRepository);
            RepositoryRaceEventDB raceEventRepository = new RepositoryRaceEventDB(eventValidator, duckRepository);

            ServicePerson personService = new ServicePerson(generatorId, personRepository);
            ServiceDuck duckService = new ServiceDuck(generatorId, duckRepository);
            ServiceFriendship friendshipService = new ServiceFriendship(generatorId, friendshipRepository);
            ServiceStatistics serviceStatistics = new ServiceStatistics(duckRepository, personRepository, friendshipRepository);
            ServiceCard serviceCard = new ServiceCard(generatorId, cardRepository);
            ServiceRaceEvent serviceRaceEvent = new ServiceRaceEvent(raceEventRepository);

            Ui ui = new Ui(personService, duckService, friendshipService, serviceStatistics, serviceCard, serviceRaceEvent);
            ui.run();
        } else {
            // In-memory repositories (file-based)
            RepositoryPerson personRepository = new RepositoryPerson(personValidator, "src/main/resources/persoane.txt");
            RepositoryDuck duckRepository = new RepositoryDuck(duckValidator, "src/main/resources/rate.txt");
            FriendshipRepository friendshipRepository = new FriendshipRepository(friendshipValidator);
            RepositoryCard cardRepository = new RepositoryCard(cardValidator);

            ServicePerson personService = new ServicePerson(generatorId, personRepository);
            ServiceDuck duckService = new ServiceDuck(generatorId, duckRepository);
            ServiceFriendship friendshipService = new ServiceFriendship(generatorId, friendshipRepository);
            ServiceStatistics serviceStatistics = new ServiceStatistics(duckRepository, personRepository, friendshipRepository, "src/main/resources/friendship.txt");
            ServiceCard serviceCard = new ServiceCard(generatorId, cardRepository);
            RepositoryRaceEvent repositoryRaceEvent = new RepositoryRaceEvent("src/main/resources/event.txt", eventValidator);
            ServiceRaceEvent serviceRaceEvent = new ServiceRaceEvent(repositoryRaceEvent);

            Ui ui = new Ui(personService, duckService, friendshipService, serviceStatistics, serviceCard, serviceRaceEvent);
            ui.run();
        }
    }
}