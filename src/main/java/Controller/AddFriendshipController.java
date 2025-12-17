package Controller;

import Domain.Friendship;
import Domain.User;
import Service.ServiceDuck;
import Service.ServiceFriendship;
import Service.ServicePerson;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddFriendshipController {
    @FXML
    private TextField textFieldUser1;

    @FXML
    private TextField textFieldUser2;

    private ServiceFriendship serviceFriendship;
    private ServiceDuck serviceDuck;
    private ServicePerson servicePerson;
    private Stage stage;

    public void setService(ServiceFriendship serviceFriendship,ServiceDuck serviceDuck,ServicePerson servicePerson,Stage stage) {
        this.serviceFriendship = serviceFriendship;
        this.serviceDuck = serviceDuck;
        this.servicePerson = servicePerson;
        this.stage = stage;
    }


    @FXML
    public void handleSave() {
        try {
            Long id1 = Long.parseLong(textFieldUser1.getText());
            Long id2 = Long.parseLong(textFieldUser2.getText());
            User a = null;
            User b = null;
            try {
               a = serviceDuck.findOne(id1);
            }catch (Exception ignored){
            }
            try {
                b = serviceDuck.findOne(id2);
            }
            catch (Exception ignored){};
            if (a == null) {
                a = servicePerson.findOne(id1);
            }
            if (b == null)
                b = servicePerson.findOne(id2);
            Friendship c = new Friendship(a, b);
            serviceFriendship.save(c);
        } catch (Exception e) {
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
