package org.example.onlinechessgame.controllers;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.onlinechessgame.ChessApp;
import org.example.onlinechessgame.model.User;
import org.example.onlinechessgame.model.client.Client;
import org.example.onlinechessgame.util.QuickLoginUtil;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML
    private TextField codeForgetPassText;

    @FXML
    private TextField usernameLoginText;

    @FXML
    private TextField emailRegisterText;

    @FXML
    private Pane forgetPassPane;

    @FXML
    private Pane loginPane;

    @FXML
    private Pane registerPane;

    @FXML
    private PasswordField passwordLoginText;

    @FXML
    private PasswordField passwordRegisterText;

    @FXML
    private PasswordField rePasswordRegisterText;

    @FXML
    private TextField usernameRegisterText;

    @FXML
    private Label wrongRetypePWLabel;

    @FXML
    private Label wrongEmailLabel;

    @FXML
    private Label wrongInfoLabel;

    @FXML
    private Label wrongUsernameLabel;

    @FXML
    private Label wrongPasswordLabel;

    @FXML
    private Line line;

    @FXML
    private CheckBox rememberCheckBox;

    @FXML
    private Button sendCodeForgetPassButton;

    private Client client;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        wrongRetypePWLabel.setVisible(false);
        wrongEmailLabel.setVisible(false);
        client = new Client("localhost", 12345, this);
        try {
            client.connectToServer();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public void login() {
        if (usernameLoginText.getText().trim().isEmpty() || passwordLoginText.getText().trim().isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Please fill in all fields!", ButtonType.OK).show();
            return;
        }
        String username = usernameLoginText.getText().trim().toLowerCase();
        String password = passwordLoginText.getText().trim();
        try {
            client.requestLogin(QuickLoginUtil.encryptLoginInfo(username, password));
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    //Login
    public void loginSuccess() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(ChessApp.class.getResource("/org/example/onlinechessgame/controllers/home.fxml"));
            Stage primaryStage = new Stage();
//        Scene scene = new Scene(fxmlLoader.load(), 1300, 950);
//        Scene scene = new Scene(fxmlLoader.load(), 816, 816);
            Scene scene = new Scene(fxmlLoader.load(), 600, 450);
            primaryStage.setTitle("Chess App");
            primaryStage.setOnCloseRequest(event -> {
                System.exit(0);
            });
            primaryStage.getIcons().add(new Image(String.valueOf(ChessApp.class.getResource("/org/example/onlinechessgame/Images/logo.png"))));
            primaryStage.setResizable(true);
            primaryStage.setScene(scene);
            primaryStage.show();
            ((HomeController)fxmlLoader.getController()).setClient(client);
            if (rememberCheckBox.isSelected()) {
                System.out.println("Save login info to login.xml!");
                QuickLoginUtil.saveLoginInfo(usernameLoginText.getText(), passwordLoginText.getText());
            }
            close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loginFailed() {
        Stage stage = (Stage) loginPane.getScene().getWindow();
        if (!stage.isShowing())
            stage.show();
        else
            wrongInfo();
    }

    public void register() {
        if (emailRegisterText.getText().trim().isEmpty() || passwordRegisterText.getText().trim().isEmpty() || rePasswordRegisterText.getText().trim().isEmpty() || usernameRegisterText.getText().trim().isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Please fill in all fields!", ButtonType.OK).show();
            return;
        }
        String email = emailRegisterText.getText().trim().toLowerCase();
        String password = passwordRegisterText.getText().trim();
        String rePassword = rePasswordRegisterText.getText().trim();
        String username = usernameRegisterText.getText().trim();
        if (!password.equals(rePassword)) {
            wrongRetypePWLabel.setVisible(true);
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), e -> {
                wrongRetypePWLabel.setVisible(false);
            }));
            timeline.setCycleCount(Timeline.INDEFINITE);
            timeline.play();
            return;
        }
        try {
            client.requestRegister(email, password, username);
            registerSuccess();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void registerSuccess() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Register success!", ButtonType.OK);
        alert.setHeaderText("Success");
        loginSuccess();
    }

    public void registerFailed() {
        wrongEmailLabel.setVisible(true);
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), e -> {
            wrongEmailLabel.setVisible(false);
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void wrongInfo() {
        wrongInfoLabel.setVisible(true);
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), e -> {
            wrongInfoLabel.setVisible(false);
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }


    private void close() {
        Stage stage = (Stage) usernameLoginText.getScene().getWindow();
        stage.close();
    }
    private void hideAll() {
        loginPane.setVisible(false);
        registerPane.setVisible(false);
        forgetPassPane.setVisible(false);
    }

    public void backToLogin() {
        hideAll();
        loginPane.setVisible(true);
    }

    public void showForgetPane() {
        hideAll();
        forgetPassPane.setVisible(true);
    }

    //Register
    public void showRegisterPane() {
        hideAll();
        registerPane.setVisible(true);
    }

    public Client getClient() {
        return client;
    }
}
