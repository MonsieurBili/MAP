package Controller;

import Domain.Ducks.Duck;
import Domain.Ducks.TipRata;
import Domain.Person.Persoana;
import ObserverGui.ObserverGui;
import Service.ServicePerson;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import util.EntityChangeEvent;
import util.EntityChangeEventType;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PersonController implements ObserverGui<EntityChangeEvent> {
    @Override
    public void update(EntityChangeEvent event) {
        if (event.getType() == EntityChangeEventType.FILTER) {
            initModel();
        }
    }

    private ServicePerson servicePerson;
    private ObservableList<Persoana> model =  FXCollections.observableArrayList();
    @FXML
    private TableView<Persoana> tableViewPerson;
    @FXML
    private TableColumn<Persoana,Long> tableIdPerson;
    @FXML
    private TableColumn<Persoana,String> tableUsernamePerson;
    @FXML
    private TableColumn<Persoana,String> tableLastName;
    @FXML
    private TableColumn<Persoana, String> tableFirstName;
    @FXML
    private TableColumn<Persoana, LocalDate> tableDOB;
    @FXML
    private TableColumn<Persoana,String> tableOcupation;

    public void setServicePerson(ServicePerson servicePerson) {
        this.servicePerson = servicePerson;
        initModel();
    }

    @FXML
    public void initialize() {
        tableIdPerson.setCellValueFactory(new PropertyValueFactory<>("id"));
        tableUsernamePerson.setCellValueFactory(new PropertyValueFactory<>("username"));
        tableLastName.setCellValueFactory(new PropertyValueFactory<>("nume"));
        tableFirstName.setCellValueFactory(new PropertyValueFactory<>("prenume"));
        tableDOB.setCellValueFactory(new PropertyValueFactory<>("dataNasterii"));
        tableOcupation.setCellValueFactory(new PropertyValueFactory<>("ocupatie"));
        tableViewPerson.setItems(model);
    }

    private void initModel() {
        List<Persoana> persons = new ArrayList<>();
        persons = (List<Persoana>) servicePerson.findAll();
        model.setAll(persons);
    }


    @FXML
    public void handleAddUser() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/addPersonView.fxml"));
            Parent root = loader.load();

            Stage popupStage = new Stage();
            popupStage.setTitle("Add Person");
            popupStage.setScene(new Scene(root));

            AddPersonController controller = loader.getController();
            controller.setService(servicePerson, popupStage);
            popupStage.showAndWait();
            initModel();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleDeleteUser() {
        Persoana selectedPerson = tableViewPerson.getSelectionModel().getSelectedItem();

        if (selectedPerson != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation Delete");
            alert.setHeaderText("Delete person: " + selectedPerson.getNume());
            alert.setContentText("Are you sure you want to delete this person?");

            if (alert.showAndWait().get() == ButtonType.OK) {
                servicePerson.delete(selectedPerson.getId());
                initModel();
            }
        } else {
            Alert errorAlert = new Alert(Alert.AlertType.WARNING);
            errorAlert.setTitle("No Selection");
            errorAlert.setHeaderText(null);
            errorAlert.setContentText("Please select a person from the table.");
            errorAlert.showAndWait();
        }
    }

}
