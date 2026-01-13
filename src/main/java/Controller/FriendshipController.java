package Controller;

import Domain.Ducks.Duck;
import Domain.Friendship;
import ObserverGui.ObserverGui;
import Service.ServiceDuck;
import Service.ServiceFriendship;
import Service.ServicePerson;
import Service.ServiceStatistics;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import util.EntityChangeEvent;
import util.EntityChangeEventType;

import java.io.IOException;
import java.util.List;
import javafx.scene.control.Label;
public class FriendshipController implements ObserverGui<EntityChangeEvent> {
    @Override
    public void update(EntityChangeEvent event) {
        if (event.getType() == EntityChangeEventType.FILTER) {
            initModel();
        }
    }

    private ServiceFriendship serviceFriendship;
    private ServicePerson servicePerson;
    private ServiceDuck serviceDuck;
    private ServiceStatistics serviceStatistics;
    private ObservableList<Friendship> model = FXCollections.observableArrayList();

    @FXML
    private TableView<Friendship> tableViewFriendship;
    @FXML
    private TableColumn<Friendship, Long> tableIdFriendship;
    @FXML
    private TableColumn<Friendship, String> tableUsernameU1;
    @FXML
    private TableColumn<Friendship, String> tableUsernameU2;
    @FXML
    private Label labelCommunityNumber;
    @FXML
    private Label labelMostSociable;

    public void setServiceFriendship(ServiceFriendship serviceFriendship, ServiceDuck serviceDuck, ServicePerson servicePerson,ServiceStatistics serviceStatistics) {
        this.serviceFriendship = serviceFriendship;
        this.serviceDuck = serviceDuck;
        this.servicePerson = servicePerson;
        this.serviceStatistics = serviceStatistics;
        initModel();
    }

    @FXML
    public void initialize() {
    tableIdFriendship.setCellValueFactory(new PropertyValueFactory<>("id"));
    tableUsernameU1.setCellValueFactory(cellData ->
        {
            Friendship friendship = cellData.getValue();
            return new SimpleStringProperty(friendship.getUser1().getUsername());
        });
        tableUsernameU2.setCellValueFactory(cellData ->
        {
            Friendship friendship = cellData.getValue();
            return new SimpleStringProperty(friendship.getUser2().getUsername());
        });
    tableViewFriendship.setItems(model);
    }

    private void initModel() {
        List<Friendship> friendshipList = (List<Friendship>)serviceFriendship.findAll();
        model.setAll(friendshipList);
        updateStatistics();
    }

    @FXML
    public void handleAddFriendship() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/addFriendshipView.fxml"));
            Parent root = loader.load();

            Stage popupStage = new Stage();
            popupStage.setTitle("Add Friendship");
            popupStage.setScene(new Scene(root));

            AddFriendshipController controller = loader.getController();
            controller.setService(serviceFriendship, serviceDuck, servicePerson, popupStage);
            popupStage.showAndWait();
            initModel();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleDeleteFriendship() {
        Friendship selectedFriendship = tableViewFriendship.getSelectionModel().getSelectedItem();

        if (selectedFriendship != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation Delete");
            alert.setHeaderText("Delete Friendship: " + selectedFriendship.getUser1().getUsername() + " " + selectedFriendship.getUser2().getUsername());
            alert.setContentText("Are you sure you want to delete this Friendship?");

            if (alert.showAndWait().get() == ButtonType.OK) {
                serviceFriendship.delete(selectedFriendship.getId());
                initModel();
            }
        } else {
            Alert errorAlert = new Alert(Alert.AlertType.WARNING);
            errorAlert.setTitle("No Selection");
            errorAlert.setHeaderText(null);
            errorAlert.setContentText("Please select a friendship from the table.");
            errorAlert.showAndWait();
        }
    }

    private void updateStatistics() {
        if (serviceStatistics != null) {
            int num = serviceStatistics.CommunityNumber();
            labelCommunityNumber.setText(String.valueOf(num));
            String sociableComp = serviceStatistics.getMostSociableCommunityName();
            labelMostSociable.setText(sociableComp);
        }
    }
}
