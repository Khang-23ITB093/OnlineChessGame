package org.example.onlinechessgame;

import org.example.onlinechessgame.model.History;
import org.example.onlinechessgame.util.ServerDatabaseHandler;

import java.sql.SQLException;
import java.util.List;

public class testServer {
    public static void main(String[] args) throws SQLException {
        ServerDatabaseHandler serverDatabaseHandler = new ServerDatabaseHandler();
        List<History> list = serverDatabaseHandler.getHistory("khang");
        for (History history : list) {
            System.out.println(history);
        }
    }
}
