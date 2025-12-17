package Controller;

import Domain.Person.Persoana;
import Domain.Person.PersonFactory;
import Service.ServicePerson;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.time.LocalDate;

public class AddPersonController {

    @FXML
    private TextField textFieldUsername;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField textFieldEmail;

    @FXML
    private TextField textFieldLastName;

    @FXML
    private TextField textFieldFirstName;

    @FXML
    private DatePicker datePicker;

    @FXML
    private TextField textFieldOccupation;

    private ServicePerson servicePerson;
    private Stage stage;
    private PersonFactory personFactory = new PersonFactory();
    public void setService(ServicePerson service, Stage stage) {
        this.servicePerson = service;
        this.stage = stage;
    }

    @FXML
    public void handleSave() {
        try {
            String username = textFieldUsername.getText();
            String nume = textFieldLastName.getText();
            String email = textFieldEmail.getText();
            String password = passwordField.getText();
            String prenume = textFieldFirstName.getText();
            LocalDate dob = datePicker.getValue();
            String ocupatie = textFieldOccupation.getText();
            personFactory.setData(username, email, password, nume, prenume, dob, ocupatie);
            Persoana p = personFactory.createUser();
            servicePerson.save(p);
            stage.close();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    public void handleCancel() {
        stage.close();
    }



}
