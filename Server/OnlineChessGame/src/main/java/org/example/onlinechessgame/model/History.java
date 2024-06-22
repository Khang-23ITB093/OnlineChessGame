package org.example.onlinechessgame.model;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

public class History implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private String usernamePlayer1;
    private String usernamePlayer2;
    private LocalDateTime localDateTime;
    private String result;

    public History(String usernamePlayer1, String usernamePlayer2, LocalDateTime localDateTime, String result) {
        this.usernamePlayer1 = usernamePlayer1;
        this.usernamePlayer2 = usernamePlayer2;
        this.localDateTime = localDateTime;
        this.result = result;
    }

    public String getUsernamePlayer1() {
        return usernamePlayer1;
    }

    public void setUsernamePlayer1(String usernamePlayer1) {
        this.usernamePlayer1 = usernamePlayer1;
    }

    public String getUsernamePlayer2() {
        return usernamePlayer2;
    }

    public void setUsernamePlayer2(String usernamePlayer2) {
        this.usernamePlayer2 = usernamePlayer2;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

    public int getResult(String usernamePlayer) {
        if (result.equals(usernamePlayer)) {
            return 1;
        } else if (result.equals("DRAW")) {
            return 0;
        } else {
            return -1;
        }
    }

    @Override
    public String toString() {
        return "History{" +
                "usernamePlayer1='" + usernamePlayer1 + '\'' +
                ", usernamePlayer2='" + usernamePlayer2 + '\'' +
                ", localDateTime=" + localDateTime +
                ", result='" + result + '\'' +
                '}';
    }
}
