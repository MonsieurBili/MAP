package Controller;

import Domain.Ducks.Duck;
import Domain.Ducks.DuckFactory;
import Domain.Ducks.TipRata;
import Domain.Person.Persoana;
import Domain.Person.PersonFactory;
import Service.ServiceAuth;
import Service.ServiceDuck;
import Service.ServicePerson;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;

public class RegisterController {

    @FXML
    private TextField textFieldUsername;
    @FXML
    private TextField textFieldEmail;
    @FXML
    private PasswordField passwordFieldPassword;
    @FXML
    private PasswordField passwordFieldConfirm;
    @FXML
    private ComboBox<String> comboBoxUserType;
    @FXML
    private Label labelMessage;
    @FXML
    private Button buttonRegister;
    @FXML
    private Button buttonBack;

    @FXML
    private VBox personFieldsContainer;
    @FXML
    private VBox duckFieldsContainer;

    @FXML
    private TextField textFieldNume;
    @FXML
    private TextField textFieldPrenume;
    @FXML
    private DatePicker datePickerNastere;
    @FXML
    private TextField textFieldOcupatie;

    @FXML
    private ComboBox<TipRata> comboBoxTipRata;
    @FXML
    private TextField textFieldViteza;
    @FXML
    private TextField textFieldRezistenta;

    private ServiceAuth serviceAuth;
    private ServicePerson servicePerson;
    private ServiceDuck serviceDuck;
    private Stage stage;
    private LoginController.MainApp mainApp;

    private final PersonFactory personFactory = new PersonFactory();
    private final DuckFactory duckFactory = DuckFactory.getInstance();

    public void setServices(ServiceAuth serviceAuth, ServicePerson servicePerson, ServiceDuck serviceDuck) {
        this.serviceAuth = serviceAuth;
        this.servicePerson = servicePerson;
        this.serviceDuck = serviceDuck;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setMainApp(LoginController.MainApp mainApp) {
        this.mainApp = mainApp;
    }

    @FXML
    public void initialize() {
        labelMessage.setVisible(false);

        comboBoxUserType.setItems(FXCollections.observableArrayList("Person", "Duck"));

        comboBoxTipRata.setItems(FXCollections.observableArrayList(TipRata.values()));
        comboBoxTipRata.getSelectionModel().selectFirst();

        comboBoxUserType.setOnAction(event -> {
            String selectedType = comboBoxUserType.getValue();
            if ("Person".equals(selectedType)) {
                personFieldsContainer.setVisible(true);
                personFieldsContainer.setManaged(true);
                duckFieldsContainer.setVisible(false);
                duckFieldsContainer.setManaged(false);
            } else if ("Duck".equals(selectedType)) {
                personFieldsContainer.setVisible(false);
                personFieldsContainer.setManaged(false);
                duckFieldsContainer.setVisible(true);
                duckFieldsContainer.setManaged(true);
            } else {
                personFieldsContainer.setVisible(false);
                personFieldsContainer.setManaged(false);
                duckFieldsContainer.setVisible(false);
                duckFieldsContainer.setManaged(false);
            }
        });
    }

    @FXML
    public void handleRegister() {
        String username = textFieldUsername.getText().trim();
        String email = textFieldEmail.getText().trim();
        String password = passwordFieldPassword.getText();
        String confirmPassword = passwordFieldConfirm.getText();
        String userType = comboBoxUserType.getValue();

        if (!password.equals(confirmPassword)) {
            showError("Passwords do not match!");
            return;
        }
        if (userType == null) {
            showError("Select a user type!");
            return;
        }
        if (serviceAuth.usernameExists(username)) {
            showError("Username already exists!");
            return;
        }

        try {
            if ("Person".equals(userType)) {
                String nume = textFieldNume.getText().trim();
                String prenume = textFieldPrenume.getText().trim();
                LocalDate dataNasterii = datePickerNastere.getValue();
                String ocupatie = textFieldOcupatie.getText().trim();

                if (dataNasterii == null) dataNasterii = LocalDate.now();
                personFactory.setData(username, email, password, nume, prenume, dataNasterii, ocupatie);
                Persoana persoana = personFactory.createUser();
                servicePerson.save(persoana);
            } else {
                TipRata tipRata = comboBoxTipRata.getValue();
                if (tipRata == null) tipRata = TipRata.SWIMMING;

                double viteza = parseDouble(textFieldViteza.getText().trim(), 0.0);
                double rezistenta = parseDouble(textFieldRezistenta.getText().trim(), 0.0);

                duckFactory.setData(username, email, password, tipRata, viteza, rezistenta, 0L);
                Duck duck = duckFactory.createUser();
                serviceDuck.save(duck);
            }

            showSuccess("Account created successfully!");
            mainApp.showLoginView();

        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    private double parseDouble(String text, double defaultValue) {
        try {
            if (!text.isEmpty()) {
                return Double.parseDouble(text);
            }
        } catch (NumberFormatException ignored) {}
        return defaultValue;
    }

    @FXML
    public void handleBack() {
        mainApp.showLoginView();
    }

    private void showError(String message) {
        labelMessage.setText(message);
        labelMessage.setStyle("-fx-text-fill: red; -fx-font-size: 12;");
        labelMessage.setVisible(true);
    }

    private void showSuccess(String message) {
        labelMessage.setText(message);
        labelMessage.setStyle("-fx-text-fill: green; -fx-font-size: 12;");
        labelMessage.setVisible(true);
    }
}
