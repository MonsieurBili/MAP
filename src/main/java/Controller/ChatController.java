package Controller;

import Domain.Message;
import Domain.User;
import ObserverGui.ObserverGui;
import Service.ServiceAuth;
import Service.ServiceDuck;
import Service.ServiceMessage;
import Service.ServicePerson;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import util.EntityChangeEvent;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ChatController implements ObserverGui<EntityChangeEvent<Message>> {

    @FXML
    private ComboBox<User> comboBoxRecipient;

    @FXML
    private ListView<Message> listViewMessages;

    @FXML
    private TextField textFieldMessage;

    @FXML
    private Label lblConversation;

    @FXML
    private HBox replyBox;

    @FXML
    private Label lblReplyTo;

    private ServiceMessage serviceMessage;
    private ServicePerson servicePerson;
    private ServiceDuck serviceDuck;
    private ServiceAuth serviceAuth;

    private ObservableList<Message> messageModel = FXCollections.observableArrayList();
    private Message replyToMessage = null;

    public void setServices(ServiceMessage serviceMessage, ServicePerson servicePerson,
                           ServiceDuck serviceDuck, ServiceAuth serviceAuth) {
        this.serviceMessage = serviceMessage;
        this.servicePerson = servicePerson;
        this.serviceDuck = serviceDuck;
        this.serviceAuth = serviceAuth;

        serviceMessage.addObserver(this);

        initializeUsers();
    }

    @Override
    public void update(EntityChangeEvent<Message> event) {
        refreshUsersList();
        if (comboBoxRecipient.getValue() != null) {
            handleLoadConversation();
        }
    }

    @FXML
    public void initialize() {
        listViewMessages.setItems(messageModel);
        listViewMessages.setCellFactory(param -> new MessageCell());

        listViewMessages.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Message selected = listViewMessages.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    setReplyTo(selected);
                }
            }
        });
    }

    private void initializeUsers() {
        refreshUsersList();

        comboBoxRecipient.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(User item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getUsername());
            }
        });
        comboBoxRecipient.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(User item, boolean empty) {

                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getUsername());
            }
        });


        comboBoxRecipient.setOnAction(event -> handleLoadConversation());
    }

    private void refreshUsersList() {
        User selectedUser = comboBoxRecipient.getValue();

        List<User> allUsers = new ArrayList<>();
        servicePerson.findAll().forEach(allUsers::add);
        serviceDuck.findAll().forEach(allUsers::add);

        User currentUser = serviceAuth.getCurrentUser().orElse(null);
        if (currentUser != null) {
            allUsers.removeIf(u -> u.getId().equals(currentUser.getId()));
        }

        comboBoxRecipient.setItems(FXCollections.observableArrayList(allUsers));
        if (selectedUser != null) {
            for (User user : allUsers) {
                if (user.getId().equals(selectedUser.getId())) {
                    comboBoxRecipient.setValue(user);
                    break;
                }
            }
        }
    }

    @FXML
    public void handleLoadConversation() {
        User recipient = comboBoxRecipient.getValue();
        User currentUser = serviceAuth.getCurrentUser().orElse(null);

        if (recipient == null || currentUser == null) {
            return;
        }

        List<Message> conversation = serviceMessage.getConversation(currentUser.getId(), recipient.getId());
        messageModel.setAll(conversation);
        lblConversation.setText("Chat with " + recipient.getUsername());


        if (!conversation.isEmpty()) {
            listViewMessages.scrollTo(conversation.size() - 1);
        }
    }

    @FXML
    public void handleSendMessage() {
        User recipient = comboBoxRecipient.getValue();
        User currentUser = serviceAuth.getCurrentUser().orElse(null);
        String messageText = textFieldMessage.getText().trim();

        if (recipient == null) {
            showAlert("Select a recipient!");
            return;
        }

        if (currentUser == null) {
            showAlert("You must be logged in!");
            return;
        }

        if (messageText.isEmpty()) {
            showAlert("Enter a message!");
            return;
        }

        try {
            List<User> recipients = List.of(recipient);

            if (replyToMessage != null) {
                serviceMessage.replyToMessage(currentUser, recipients, messageText, replyToMessage);
                handleCancelReply();
            } else {
                serviceMessage.sendMessage(currentUser, recipients, messageText);
            }

            textFieldMessage.clear();
            handleLoadConversation();
        } catch (Exception e) {
            showAlert("Error sending message: " + e.getMessage());
        }
    }

    private void setReplyTo(Message message) {
        replyToMessage = message;
        lblReplyTo.setText("Replying to: \"" + truncate(message.getMessage(), 30) + "\"");
        replyBox.setVisible(true);
        replyBox.setManaged(true);
    }

    @FXML
    public void handleCancelReply() {
        replyToMessage = null;
        replyBox.setVisible(false);
        replyBox.setManaged(false);
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private String truncate(String text, int maxLength) {
        if (text.length() <= maxLength) return text;
        return text.substring(0, maxLength) + "...";
    }


    private class MessageCell extends ListCell<Message> {
        @Override
        protected void updateItem(Message message, boolean empty) {
            super.updateItem(message, empty);

            if (empty || message == null) {
                setGraphic(null);
                setText(null);
                return;
            }

            User currentUser = serviceAuth.getCurrentUser().orElse(null);
            boolean isMyMessage = currentUser != null && message.getFrom().getId().equals(currentUser.getId());

            VBox container = new VBox(3);
            container.setPadding(new Insets(5, 10, 5, 10));

            HBox header = new HBox(10);
            Label lblSender = new Label(message.getFrom().getUsername());
            lblSender.setFont(Font.font("System", FontWeight.BOLD, 12));
            lblSender.setTextFill(isMyMessage ? Color.DARKBLUE : Color.DARKGREEN);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
            Label lblTime = new Label(message.getData().format(formatter));
            lblTime.setFont(Font.font("System", 10));
            lblTime.setTextFill(Color.GRAY);

            header.getChildren().addAll(lblSender, lblTime);

            if (message.getReply() != null) {
                Label lblReply = new Label("↳ Reply to: \"" + truncate(message.getReply().getMessage(), 25) + "\"");
                lblReply.setFont(Font.font("System", FontWeight.NORMAL, 10));
                lblReply.setTextFill(Color.GRAY);
                lblReply.setStyle("-fx-font-style: italic;");
                container.getChildren().add(lblReply);
            }

            Label lblMessage = new Label(message.getMessage());
            lblMessage.setWrapText(true);
            lblMessage.setMaxWidth(350);
            lblMessage.setStyle("-fx-background-color: " + (isMyMessage ? "#DCF8C6" : "#FFFFFF") + "; " +
                    "-fx-padding: 8; -fx-background-radius: 10;");

            container.getChildren().addAll(header, lblMessage);
            container.setAlignment(isMyMessage ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);

            HBox wrapper = new HBox();
            wrapper.setAlignment(isMyMessage ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
            wrapper.getChildren().add(container);

            setGraphic(wrapper);
        }
    }
}

