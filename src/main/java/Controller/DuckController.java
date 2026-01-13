package Controller;

import Domain.Ducks.Duck;
import Domain.Ducks.TipRata;
import Domain.Person.Persoana;
import ObserverGui.ObserverGui;
import Service.Service;
import javafx.beans.property.SimpleStringProperty;
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
import Observer.Observer;
import Service.ServiceDuck;
import util.EntityChangeEventType;
import util.paging.Page;
import util.paging.Pageable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DuckController implements ObserverGui<EntityChangeEvent> {
    @Override
    public void update(EntityChangeEvent event) {
        if (event.getType() == EntityChangeEventType.FILTER) {
            initModel();
        }
    }

    private ServiceDuck serviceDuck;
    private ObservableList<Duck> model =  FXCollections.observableArrayList();
    @FXML
    private TableView<Duck> tableViewDuck;
    @FXML
    private TableColumn<Duck,Long> tableIdDuck;
    @FXML
    private TableColumn<Duck,String> tableUsernameDuck;
    @FXML
    private TableColumn<Duck, String> tableDuckType;
    @FXML
    private TableColumn<Duck,Double> tableDuckSpeed;
    @FXML
    private TableColumn<Duck,Double> tableDuckResistance;
    @FXML
    private TableColumn<Duck,Long> tableCardId;
    @FXML
    private ComboBox<TipRata> comboBoxfilterByType;

    @FXML
    private Button btnNext;
    @FXML
    private Button btnPrevious;
    @FXML
    private Label lblPage;



    private int currentPage = 0;
    private int pageSize = 7;
    private int totalElements = 0;
    private String currentFilter = null;

    public void setDuckService(ServiceDuck duckService) {
        this.serviceDuck = duckService;
        initModel();
    }
    @FXML
    public void initialize() {
        tableIdDuck.setCellValueFactory(new PropertyValueFactory<>("id"));
        tableUsernameDuck.setCellValueFactory(new PropertyValueFactory<>("username"));
        tableDuckSpeed.setCellValueFactory(new PropertyValueFactory<>("viteza"));
        tableDuckResistance.setCellValueFactory(new PropertyValueFactory<>("rezistenta"));
        tableCardId.setCellValueFactory(new PropertyValueFactory<>("IdCard"));

        tableDuckType.setCellValueFactory(cellData -> {
            Duck duck = cellData.getValue();
            TipRata tipRata = duck.getTipRata();
            if (tipRata != null) {
                return new SimpleStringProperty(tipRata.toString());
            } else {
                return new SimpleStringProperty("");
            }
        });

        tableViewDuck.setItems(model);

        List<TipRata> tipuriRata = Arrays.asList(TipRata.values());
        ObservableList<TipRata> options = FXCollections.observableArrayList(tipuriRata);
        comboBoxfilterByType.setItems(options);
        options.add(0, null);

        comboBoxfilterByType.setOnAction(event-> {
            TipRata valoareNoua = comboBoxfilterByType.getValue();
            if (valoareNoua != null) {
                handleFilterByType(valoareNoua);
            }
            else {
                currentFilter = null;
                currentPage = 0;
                initModel();
            }
        });
    }

    private void initModel() {

        Pageable pageable = new Pageable(currentPage, pageSize);
        Page<Duck> page = serviceDuck.findAllOnPageFiltered(pageable, currentFilter);

        List<Duck> ducks = new ArrayList<>();
        page.getElementsOnPage().forEach(ducks::add);

        this.totalElements = page.getTotalNumberOfElements();
        model.setAll(ducks);
        updateButtonsState();
    }
    private void handleFilterByType(TipRata selectedType) {
        currentFilter = selectedType.toString();
        currentPage = 0;
        initModel();
    }
    @FXML
    public void handleAddDuck() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/addDuckView.fxml"));
            Parent root = loader.load();

            Stage popupStage = new Stage();
            popupStage.setTitle("Add Duck");
            popupStage.setScene(new Scene(root));

            AddDuckController controller = loader.getController();
            controller.setService(serviceDuck, popupStage);
            popupStage.showAndWait();
            initModel();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void handleDeleteUser() {
        Duck selectedDuck = tableViewDuck.getSelectionModel().getSelectedItem();

        if (selectedDuck != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation Delete");
            alert.setHeaderText("Delete Duck: " + selectedDuck.getUsername());
            alert.setContentText("Are you sure you want to delete this Duck?");

            if (alert.showAndWait().get() == ButtonType.OK) {
                serviceDuck.delete(selectedDuck.getId());
                initModel();
            }
        } else {
            Alert errorAlert = new Alert(Alert.AlertType.WARNING);
            errorAlert.setTitle("No Selection");
            errorAlert.setHeaderText(null);
            errorAlert.setContentText("Please select a duck from the table.");
            errorAlert.showAndWait();
        }
    }
    @FXML
    public void handleNext() {
        if ((currentPage + 1) * pageSize < totalElements) {
            currentPage++;
            initModel();
        }
    }
    public void handlePrevious() {
        if (currentPage > 0) {
            currentPage--;
            initModel();
        }
    }
    private void updateButtonsState() {
        btnPrevious.setDisable(currentPage == 0);
        btnNext.setDisable((currentPage + 1) * pageSize >= totalElements);
        lblPage.setText("Pagina " + (currentPage + 1));
    }
}
