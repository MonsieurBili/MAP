package Controller;

import Service.*;
import javafx.fxml.FXML;

public class MainController {

    @FXML
    private DuckController duckViewController;

    @FXML
    private PersonController personViewController;

    @FXML
    private FriendshipController friendshipViewController;

    @FXML
    private ChatController chatViewController;

    private ServiceDuck serviceDuck;
    private ServicePerson servicePerson;
    private ServiceFriendship serviceFriendship;
    private ServiceStatistics serviceStatistics;
    private ServiceAuth serviceAuth;
    private ServiceMessage serviceMessage;

    public void setServices(ServiceDuck serviceDuck, ServicePerson servicePerson, ServiceFriendship serviceFriendship,
                           ServiceStatistics serviceStatistics, ServiceAuth serviceAuth, ServiceMessage serviceMessage) {
        this.serviceDuck = serviceDuck;
        this.servicePerson = servicePerson;
        this.serviceFriendship = serviceFriendship;
        this.serviceStatistics = serviceStatistics;
        this.serviceAuth = serviceAuth;
        this.serviceMessage = serviceMessage;

        duckViewController.setDuckService(serviceDuck);
        personViewController.setServicePerson(servicePerson);
        friendshipViewController.setServiceFriendship(serviceFriendship, serviceDuck, servicePerson, serviceStatistics);
        chatViewController.setServices(serviceMessage, servicePerson, serviceDuck, serviceAuth);

        serviceDuck.addObserver(duckViewController);
        servicePerson.addObserver(personViewController);
        serviceFriendship.addObserver(friendshipViewController);
    }
}