package org.example.onlinechessgame.model;

import java.io.Serial;
import java.io.Serializable;

public class Message implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private MessageType type;
    private Object data;

    public Message(MessageType type, Object data) {
        this.type = type;
        this.data = data;
    }

    public MessageType getType() {
        return type;
    }

    public Object getData() {
        return data;
    }

    public enum MessageType {
        CONNECT, DISCONNECT, MOVE, MATCH, REMATCH, ERROR, PROMOTE,
        INFORMATION_MATCH, CANCEL, WIN, CANCEL_REMATCH, LOSE, REQUEST_DRAW, DRAW, ACCEPT_DRAW,

        LOGIN, LOGIN_FAILED, QUICK_LOGIN, QUICK_LOGIN_FAILED, REGISTER, REGISTER_FAILED, FORGOT_PASSWORD, EXIT, OUT,
        GET_DATA, CHECK_USERNAME, CHECK_EMAIL, GET_HISTORY,
        MESSAGE
    }
}
