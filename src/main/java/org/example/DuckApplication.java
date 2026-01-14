package org.example;

import Controller.LoginController;
import Controller.MainController;
import Controller.RegisterController;
import Repository.Database.*;
import Repository.IdGenerator;
import Service.*;
import Validators.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class DuckApplication extends Application {

    private ServiceDuck duckService;
    private ServicePerson personService;
    private ServiceFriendship friendshipService;
    private ServiceStatistics serviceStatistics;
    private ServiceMessage serviceMessage;

    private RepositoryPersonDB personRepositorydb;
    private RepositoryDuckDB duckRepository;

    @Override
    public void start(Stage stage) throws IOException {
        PersonValidator personValidator = new PersonValidator();
        DuckValidator duckValidator = new DuckValidator();
        FriendshipValidator friendshipValidator = new FriendshipValidator();
        CardValidator cardValidator = new CardValidator();

        personRepositorydb = new RepositoryPersonDB(personValidator);
        duckRepository = new RepositoryDuckDB(duckValidator);
        RepositoryFriendshipDb friendshipRepository = new RepositoryFriendshipDb(friendshipValidator, personRepositorydb, duckRepository);
        RepositoryCardDb cardRepository = new RepositoryCardDb(cardValidator);
        RepositoryMessageDb messageRepository = new RepositoryMessageDb(personRepositorydb, duckRepository);

        IdGenerator generatorId = IdGenerator.getInstance();
        personService = new ServicePerson(generatorId, personRepositorydb);
        duckService = new ServiceDuck(generatorId, duckRepository);
        friendshipService = new ServiceFriendship(generatorId, friendshipRepository);
        serviceStatistics = new ServiceStatistics(duckRepository, personRepositorydb, friendshipRepository, "src/main/resources/friendship.txt");
        serviceMessage = new ServiceMessage(messageRepository);

        createLoginWindow("User 1 - Login", 100, 100);
        createLoginWindow("User 2 - Login", 550, 100);
    }

    private void createLoginWindow(String title, double x, double y) {
        try {
            Stage stage = new Stage();
            stage.setX(x);
            stage.setY(y);
            

            ServiceAuth serviceAuth = new ServiceAuth(personRepositorydb, duckRepository);
            SessionHandler sessionHandler = new SessionHandler(stage, serviceAuth);
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/loginView.fxml"));
            Scene scene = new Scene(loader.load());

            LoginController loginController = loader.getController();
            loginController.setServiceAuth(serviceAuth);
            loginController.setStage(stage);
            loginController.setMainApp(sessionHandler);

            stage.setTitle(title);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private class SessionHandler implements LoginController.MainApp {
        private final Stage stage;
        private final ServiceAuth serviceAuth;

        public SessionHandler(Stage stage, ServiceAuth serviceAuth) {
            this.stage = stage;
            this.serviceAuth = serviceAuth;
        }

        @Override
        public void showLoginView() {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/loginView.fxml"));
                Scene scene = new Scene(loader.load());

                LoginController loginController = loader.getController();
                loginController.setServiceAuth(serviceAuth);
                loginController.setStage(stage);
                loginController.setMainApp(this);

                stage.setTitle("Login - Duck Network");
                stage.setScene(scene);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void showRegisterView() {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/registerView.fxml"));
                Scene scene = new Scene(loader.load());

                RegisterController registerController = loader.getController();
                registerController.setServices(serviceAuth, personService, duckService);
                registerController.setStage(stage);
                registerController.setMainApp(this);

                stage.setTitle("Register - Duck Network");
                stage.setScene(scene);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void showMainView() {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/mainview.fxml"));
                Scene scene = new Scene(loader.load());

                MainController mainController = loader.getController();
                mainController.setServices(duckService, personService, friendshipService, serviceStatistics, serviceAuth, serviceMessage);

                String username = serviceAuth.getCurrentUser().map(u -> u.getUsername()).orElse("Unknown");
                stage.setTitle("Duck Network - " + username);
                stage.setScene(scene);
                stage.setWidth(800);
                stage.setHeight(600);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
