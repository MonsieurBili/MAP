package Controller;

import Domain.User;
import Service.ServiceAuth;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.Optional;

public class LoginController {

    @FXML
    private TextField textFieldUsername;

    @FXML
    private PasswordField passwordFieldPassword;

    @FXML
    private Label labelError;

    @FXML
    private Button buttonLogin;

    @FXML
    private Button buttonRegister;

    private ServiceAuth serviceAuth;
    private Stage stage;
    private MainApp mainApp;

    public void setServiceAuth(ServiceAuth serviceAuth) {
        this.serviceAuth = serviceAuth;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    @FXML
    public void initialize() {
        labelError.setVisible(false);
        passwordFieldPassword.setOnAction(event -> handleLogin());
    }

    @FXML
    public void handleLogin() {
        String username = textFieldUsername.getText().trim();
        String password = passwordFieldPassword.getText();

        if (username.isEmpty()) {
            showError("Write Username");
            return;
        }

        if (password.isEmpty()) {
            showError("Write password!");
            return;
        }

        Optional<User> user = serviceAuth.login(username, password);

        if (user.isPresent()) {
            labelError.setVisible(false);
            mainApp.showMainView();
        } else {
            showError("Username or password incorrect");
            passwordFieldPassword.clear();
        }
    }

    @FXML
    public void handleRegister() {
        mainApp.showRegisterView();
    }

    private void showError(String message) {
        labelError.setText(message);
        labelError.setVisible(true);
    }

    public interface MainApp {
        void showMainView();
        void showRegisterView();
        void showLoginView();
    }
}
