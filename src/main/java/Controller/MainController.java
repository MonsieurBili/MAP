package Controller;

import Service.ServiceDuck;
import Service.ServiceFriendship;
import Service.ServicePerson;
import Service.ServiceStatistics;
import javafx.fxml.FXML;

public class MainController {

    @FXML
    private DuckController duckViewController;

    @FXML
    private PersonController personViewController;

    @FXML
    private FriendshipController friendshipViewController;

    private ServiceDuck serviceDuck;
    private ServicePerson servicePerson;
    private ServiceFriendship serviceFriendship;
    private ServiceStatistics serviceStatistics;

    public void setServices(ServiceDuck serviceDuck, ServicePerson servicePerson,ServiceFriendship serviceFriendship, ServiceStatistics serviceStatistics) {
        this.serviceDuck = serviceDuck;
        this.servicePerson = servicePerson;
        this.serviceFriendship = serviceFriendship;
        this.serviceStatistics = serviceStatistics;
        duckViewController.setDuckService(serviceDuck);
        personViewController.setServicePerson(servicePerson);
        friendshipViewController.setServiceFriendship(serviceFriendship,serviceDuck,servicePerson,serviceStatistics);


        serviceDuck.addObserver(duckViewController);
        servicePerson.addObserver(personViewController);
        serviceFriendship.addObserver(friendshipViewController);
    }
}