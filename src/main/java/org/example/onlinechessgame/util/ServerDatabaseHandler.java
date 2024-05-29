package org.example.onlinechessgame.util;

import org.example.onlinechessgame.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ServerDatabaseHandler {
    private Connection connection;
    public ServerDatabaseHandler() {
        connection = ConnectionDB.getConnection();
        if (connection == null) {
            System.out.println("Cannot connect to the database.");
        }
    }

    public boolean authenticateUser(String username, String password) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT * FROM user WHERE username = ? AND password = ?");
        statement.setString(1, username);
        statement.setString(2, password);
        ResultSet resultSet = statement.executeQuery();
        return resultSet.next();
    }

    public void registerUser(String []data) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("INSERT INTO user (username, password, email) VALUES (?, ?, ?)");
        statement.setString(1, data[0]);
        statement.setString(2, data[1]);
        statement.setString(3, data[2]);
        statement.executeUpdate();
    }

    public User getUser(String username) throws SQLException {
        User user = new User();

        PreparedStatement statement = connection.prepareStatement("SELECT * FROM user WHERE username = ?");
        statement.setString(1, username);
        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            user.setUsername(resultSet.getString("username"));
            user.setPassword(resultSet.getString("password"));
            user.setEmail(resultSet.getString("email"));
            user.setCountry(resultSet.getString("country"));
            user.setPoint(resultSet.getInt("point"));
        }

        return user;
    }

    public void updatePlayerPoints(boolean isWin, User user) throws SQLException {
        int pointsChange = isWin ? 100 : -50; // Thắng +100, thua -50
        PreparedStatement statement;
        if (user.getPoint() < 50) {
            statement = connection.prepareStatement("UPDATE user SET point = ? WHERE email = ?");
            pointsChange = 0;
        } else if (user.getPoint() == 0) return;
        // Cập nhật điểm của người chơi trong database
        else
         statement = connection.prepareStatement("UPDATE user SET point = point + ? WHERE email = ?");

        statement.setInt(1, pointsChange);
        statement.setString(2, user.getEmail()); // currentPlayerUsername là username của người chơi hiện tại
        statement.executeUpdate();
    }
}