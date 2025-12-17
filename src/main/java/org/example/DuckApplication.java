package org.example;

import Controller.DuckController;
import Controller.MainController;
import Controller.PersonController;
import Repository.Database.*;
import Repository.IdGenerator;
import Service.*;
import UI.Ui;
import Validators.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class DuckApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        PersonValidator personValidator = new PersonValidator();
        DuckValidator duckValidator = new DuckValidator();
        FriendshipValidator friendshipValidator = new FriendshipValidator();
        EventValidator eventValidator = new EventValidator();
        CardValidator cardValidator = new CardValidator();
        //RepositoryPerson personRepository = new RepositoryPerson(personValidator,"src/main/resources/persoane.txt");
        RepositoryPersonDB personRepositorydb = new RepositoryPersonDB(personValidator);
        //RepositoryDuck duckRepository = new RepositoryDuck(duckValidator,"src/main/resources/rate.txt");
        RepositoryDuckDB duckRepository = new RepositoryDuckDB(duckValidator);
        //FriendshipRepository friendshipRepository =new FriendshipRepository(friendshipValidator);
        RepositoryFriendshipDb friendshipRepository = new RepositoryFriendshipDb(friendshipValidator, personRepositorydb, duckRepository);
        //RepositoryCard cardRepository = new RepositoryCard(cardValidator);
        RepositoryCardDb cardRepository = new RepositoryCardDb(cardValidator);
        IdGenerator generatorId = IdGenerator.getInstance();
        ServicePerson personService = new ServicePerson(generatorId,personRepositorydb);
        ServiceDuck duckService = new ServiceDuck(generatorId,duckRepository);
        ServiceFriendship friendshipService = new ServiceFriendship(generatorId,friendshipRepository);
        ServiceStatistics serviceStatistics = new ServiceStatistics(duckRepository,personRepositorydb,friendshipRepository,"src/main/resources/friendship.txt");
        ServiceCard serviceCard = new ServiceCard(generatorId,cardRepository);
        //RepositoryRaceEvent repositoryRaceEvent = new RepositoryRaceEvent("src/main/resources/event.txt",eventValidator);
        RepositoryEventDb repositoryRaceEvent = new RepositoryEventDb(eventValidator,duckRepository,personRepositorydb);
        ServiceRaceEvent serviceRaceEvent = new ServiceRaceEvent(repositoryRaceEvent);
        Ui ui = new Ui(personService,duckService,friendshipService,serviceStatistics,serviceCard,serviceRaceEvent);


        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gui/mainview.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Main");
        stage.setScene(scene);

        MainController mainController = fxmlLoader.getController();
        mainController.setServices(duckService,personService,friendshipService,serviceStatistics);
        stage.show();
    }
    public static void main(String[] args) {
        launch();
    }
}
