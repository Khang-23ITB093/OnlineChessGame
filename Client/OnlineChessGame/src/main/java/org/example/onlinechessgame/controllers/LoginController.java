package org.example.onlinechessgame.controllers;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
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
    private Label wrongLengthUsernameLabel;

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

        //key event for login
        passwordLoginText.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                login();
            }
        });
        usernameLoginText.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                login();
            }
        });

        //key event for register
        rePasswordRegisterText.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                register();
            }
        });
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
            primaryStage.setOnCloseRequest(event -> System.exit(0));
            primaryStage.getIcons().add(new Image(String.valueOf(ChessApp.class.getResource("/org/example/onlinechessgame/Images/logo.png"))));
            primaryStage.setResizable(false);
            primaryStage.setScene(scene);
            primaryStage.show();
            ((HomeController) fxmlLoader.getController()).setClient(client);
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
        if (username.length() > 20) {
            wrongLengthUsernameLabel.setVisible(true);
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2.5), e -> wrongLengthUsernameLabel.setVisible(false)));
            timeline.setCycleCount(Timeline.INDEFINITE);
            timeline.play();
            return;
        }
        if (!password.equals(rePassword)) {
            wrongRetypePWLabel.setVisible(true);
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2.5), e -> wrongRetypePWLabel.setVisible(false)));
            timeline.setCycleCount(Timeline.INDEFINITE);
            timeline.play();
            return;
        }
        // Kiểm tra mật khẩu
        String passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,}$";
        if (!password.matches(passwordPattern)) {
            wrongPasswordLabel.setVisible(true);
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), e -> wrongPasswordLabel.setVisible(false)));
            timeline.setCycleCount(Timeline.INDEFINITE);
            timeline.play();
            return;
        }
        try {
            client.requestRegister(QuickLoginUtil.encryptLoginInfo(email, password, username));
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void registerSuccess() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Register success!", ButtonType.OK);
        alert.setHeaderText("Success");
        loginSuccess();
    }

    private void wrongInfo() {
        wrongInfoLabel.setVisible(true);
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), e -> wrongInfoLabel.setVisible(false)));
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
        ((Stage) usernameLoginText.getScene().getWindow()).setTitle("Login");
    }

    public void showForgetPane() {
        hideAll();
        forgetPassPane.setVisible(true);
        ((Stage) usernameLoginText.getScene().getWindow()).setTitle("Forget Password");
    }

    //Register
    public void showRegisterPane() {
        hideAll();
        registerPane.setVisible(true);
        ((Stage) usernameLoginText.getScene().getWindow()).setTitle("Register");
    }

    public Client getClient() {
        return client;
    }

    public void usernameExists() {
        wrongUsernameLabel.setVisible(true);
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), e -> wrongUsernameLabel.setVisible(false)));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    public void emailExists() {
        wrongEmailLabel.setVisible(true);
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), e -> wrongEmailLabel.setVisible(false)));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }
}
