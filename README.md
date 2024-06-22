## Online Chess Game

<a href="https://github.com/Khang-23ITB093/OnlineChessGame/releases"><img src="https://img.shields.io/github/v/release/Khang-23ITB093/OnlineChessGame?5&style=for-the-badge" alt="Latest Version"></a>

### Introduction
> This is the first-year final project of <span style="color: red;">V</span><span style="color: yellow;">K</span><span style="color: Blue;">U</span> students in Vietnam.

This is an online chess game project written in JavaFX. It allows two players to connect to each other over a LAN network and play chess. The project utilizes a MySQL database to store user information and match history.

<p align="center">

<img src="https://github.com/Khang-23ITB093/OnlineChessGame/blob/main/Image/login.png?raw=true" alt="Login">
<img src="https://github.com/Khang-23ITB093/OnlineChessGame/blob/main/Image/home.png?raw=true" alt="home">
<img src="https://github.com/Khang-23ITB093/OnlineChessGame/blob/main/Image/matching.png?raw=true" alt="matching">
<img src="https://github.com/Khang-23ITB093/OnlineChessGame/blob/main/Image/gameUI.png?raw=true" alt="gameUI">
<img src="https://github.com/Khang-23ITB093/OnlineChessGame/blob/main/Image/castle.png?raw=true" alt="castle">
<img src="https://github.com/Khang-23ITB093/OnlineChessGame/blob/main/Image/promotion.png?raw=true" alt="promotion">
<img src="https://github.com/Khang-23ITB093/OnlineChessGame/blob/main/Image/endgame.png?raw=true" alt="endgame">
</p>

### Requirements

* Java Development Kit (JDK) 21+
* MySQL Connector
* MySQL database

### Installation

1. **Install JDK 21+**
2. **Install MySQL Connector**: Add the MySQL Connector library to your project.
3. **Create Database**:
    * Create a new MySQL database named `onlinechessgame`.
    * Import the `onlinechessgame.sql` file into the database to create the necessary tables.
4. **Configure Database Connection**: Edit the `ConnectionDB.java` file to set up the connection details to your database:
    ```java
    private static String DB_URL = "jdbc:mysql://localhost:3306/onlinechessgame";
    private static String USER = "root";
    private static String PASSWORD = "";
    ```

### Running the Program

1. **Start the MySQL database.**
2. **Run the server**: Execute `Server.java` to start the server.
3. **Run the client**: Execute `ChessApp.java` to start the game client.

### Playing the Game

1. **Login/Register**:
    * Use the login interface to log in to your account.
    * If you do not have an account, register a new one.
2. **Matchmaking**: Click the "Matching" button to search for an opponent.
3. **Play Chess**: Once an opponent is found, you will be redirected to the chessboard and can start playing.
4. **Game End**: The game ends when one player checkmates the opponent's king or when both players agree to a draw.
5. **Rematch**: After the game ends, you can request a rematch with your opponent.

### Notes

* Currently, the project only supports play over a LAN network.
* The program can be extended to support play over a WAN network.
* The user interface can be improved.

### Detailed Instructions

For more detailed instructions on using the program, please refer to the project source code.


>  <h1 style="color: yellow">Developed by Vo Quoc Khang</h1>
