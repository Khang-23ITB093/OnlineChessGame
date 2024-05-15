module org.example.onlinechessgame {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.onlinechessgame to javafx.fxml;
    opens org.example.onlinechessgame.controllers to javafx.fxml;
    exports org.example.onlinechessgame;
}