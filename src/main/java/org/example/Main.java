package org.example;

import Repository.*;
import Service.*;
import UI.Ui;
import Validators.*;

public class Main {
    public static void main(String[] args) {
        PersonValidator personValidator = new PersonValidator();
        DuckValidator duckValidator = new DuckValidator();
        FriendshipValidator friendshipValidator = new FriendshipValidator();
        EventValidator eventValidator = new EventValidator();
        CardValidator cardValidator = new CardValidator();
        RepositoryPerson personRepository = new RepositoryPerson(personValidator,"src/main/resources/persoane.txt");
        RepositoryDuck duckRepository = new RepositoryDuck(duckValidator,"src/main/resources/rate.txt");
        FriendshipRepository friendshipRepository =new FriendshipRepository(friendshipValidator);
        RepositoryCard cardRepository = new RepositoryCard(cardValidator);
        IdGenerator generatorId = IdGenerator.getInstance();
        ServicePerson personService = new ServicePerson(generatorId,personRepository);
        ServiceDuck duckService = new ServiceDuck(generatorId,duckRepository);
        ServiceFriendship friendshipService = new ServiceFriendship(generatorId,friendshipRepository);
        ServiceStatistics serviceStatistics = new ServiceStatistics(duckRepository,personRepository,friendshipRepository,"src/main/resources/friendship.txt");
        ServiceCard serviceCard = new ServiceCard(generatorId,cardRepository);
        RepositoryRaceEvent repositoryRaceEvent = new RepositoryRaceEvent("event.txt",eventValidator);
        ServiceRaceEvent serviceRaceEvent = new ServiceRaceEvent(repositoryRaceEvent);
        Ui ui = new Ui(personService,duckService,friendshipService,serviceStatistics,serviceCard,serviceRaceEvent);
        ui.run();

    }
}