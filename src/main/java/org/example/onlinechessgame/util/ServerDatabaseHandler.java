package org.example.onlinechessgame.util;

import org.example.onlinechessgame.model.History;
import org.example.onlinechessgame.model.User;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    public void registerUser(String[] data) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("INSERT INTO user (email, password, username) VALUES (?, ?, ?)");
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
            user.setEmail(resultSet.getString("email"));
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

    public boolean checkUsername(String username) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT * FROM user WHERE username = ?");
        statement.setString(1, username);
        ResultSet resultSet = statement.executeQuery();
        return resultSet.next();
    }

    public boolean checkEmail(String email) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT * FROM user WHERE email = ?");
        statement.setString(1, email);
        ResultSet resultSet = statement.executeQuery();
        return resultSet.next();
    }

    private int getPlayerID(String username) throws SQLException {
            PreparedStatement statement = connection.prepareStatement("SELECT PlayerID FROM user WHERE username = ?");
        statement.setString(1, username);
        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            return resultSet.getInt("PlayerID");
        } else {
            throw new SQLException("No user found with username: " + username);
        }
    }

    public void addMatchHistory(String usernamePlayer1, String usernamePlayer2, LocalDateTime matchDateTime, String result) throws SQLException {
        int player1ID = getPlayerID(usernamePlayer1);
        int player2ID = getPlayerID(usernamePlayer2);
        int winnerID = getPlayerID(result);
        PreparedStatement statement = connection.prepareStatement("INSERT INTO history ( Player1ID, Player2ID, MatchDateTime, Result) VALUES ( ?, ?, ?, ?)");
        statement.setInt(1, player1ID);
        statement.setInt(2, player2ID);
        statement.setTimestamp(3, Timestamp.valueOf(matchDateTime));
        statement.setInt(4, winnerID);
        statement.executeUpdate();
    }

    public List<History> getHistory(String usernamePlayer) throws SQLException {
        int playerID = getPlayerID(usernamePlayer);
        List<History> historyList = new ArrayList<>();
        History history;
        PreparedStatement statement =
                connection.prepareStatement("SELECT u1.Username AS UsernamePlayer1, u2.Username AS UsernamePlayer2, h.MatchDateTime, u3.Username AS Winner " +
                        "FROM history h " +
                        "JOIN user u1 ON h.Player1ID = u1.PlayerID " +
                        "JOIN user u2 ON h.Player2ID = u2.PlayerID " +
                        "JOIN user u3 ON h.Result = u3.PlayerID " +
                        "WHERE h.Player1ID = ? OR h.Player2ID = ?");
        statement.setInt(1, playerID);
        statement.setInt(2, playerID);
        ResultSet resultSet = statement.executeQuery();
        while (resultSet.next()) {
            history = new History(resultSet.getString("UsernamePlayer1"), resultSet.getString("UsernamePlayer2"),
                    resultSet.getTimestamp("MatchDateTime").toLocalDateTime(), resultSet.getString("Winner"));
            historyList.add(history);
        }
        return historyList;
    }
}