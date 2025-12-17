package Controller;

import Domain.Ducks.Duck;
import Domain.Ducks.DuckFactory;
import Domain.Ducks.TipRata;
import Service.ServiceDuck;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddDuckController {
    @FXML
    private TextField textFieldUsername;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField textFieldEmail;

    @FXML
    private TextField textFieldType;

    @FXML
    private TextField textFieldSpeed;

    @FXML
    private TextField textFieldResistance;

    @FXML
    private TextField textFieldFlock;

    private ServiceDuck serviceDuck;
    private Stage stage;
    private DuckFactory duckFactory = DuckFactory.getInstance();

    public void setService(ServiceDuck serviceDuck,Stage stage) {
        this.serviceDuck = serviceDuck;
        this.stage = stage;
    }

    @FXML
    public void handleSave()
    {
        try {
            String username = textFieldUsername.getText();
            String email = textFieldEmail.getText();
            String password = passwordField.getText();
            String tipRata = textFieldType.getText();
            TipRata tipbun = null;
            if (tipRata.equals("FLYING"))
                tipbun = TipRata.FLYING;
            if (tipRata.equals("SWIMMING"))
                tipbun = TipRata.SWIMMING;
            if (tipRata.equals("FLYING AND SWIMMING"))
                tipbun = TipRata.FLYING_AND_SWIMMING;
            Double speed = Double.parseDouble(textFieldSpeed.getText());
            Double resistance = Double.parseDouble(textFieldResistance.getText());
            Long idCard = Long.parseLong(textFieldFlock.getText());
            duckFactory.setData(username, email, password, tipbun, speed, resistance, idCard);
            Duck d = duckFactory.createUser();
            serviceDuck.save(d);
        }catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
            alert.showAndWait();
        }
        stage.close();
    }

    @FXML
    public void handleCancel() {
        stage.close();
    }
}
