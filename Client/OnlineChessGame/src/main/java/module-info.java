module org.example.onlinechessgame {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.xml.bind;
    requires java.sql;


    opens org.example.onlinechessgame to javafx.fxml;
    opens org.example.onlinechessgame.controllers to javafx.fxml;
    exports org.example.onlinechessgame.pieces;
    exports org.example.onlinechessgame;
}