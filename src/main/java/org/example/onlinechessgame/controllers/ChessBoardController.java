package org.example.onlinechessgame.controllers;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.Glow;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.example.onlinechessgame.Board;
import org.example.onlinechessgame.Tile;
import org.example.onlinechessgame.model.Move;
import org.example.onlinechessgame.model.User;
import org.example.onlinechessgame.model.client.Client;
import org.example.onlinechessgame.pieces.Piece;
import org.example.onlinechessgame.pieces.PieceType;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ChessBoardController implements Initializable {
    @FXML
    private GridPane gridPane;
    @FXML
    private Pane resultPane;
    @FXML
    private Label namePlayer1Label;
    @FXML
    private Label namePlayer2Label;
    @FXML
    private Label pointPlayer1Label;
    @FXML
    private Label pointPlayer2Label;
    @FXML
    private Label winnerLabel;
    @FXML
    private Label timerLabel;
    @FXML
    private Label player1TimerLabel;
    @FXML
    private Label player2TimerLabel;
    @FXML
    private Pane waitingPane;
    @FXML
    private TextArea chatTextArea;
    @FXML
    private TextField chatTextField;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private HBox player1Hbox;
    @FXML
    private HBox player2Hbox;
    @FXML
    private HBox blackRow1;
    @FXML
    private HBox blackRow2;
    @FXML
    private HBox whiteRow1;
    @FXML
    private HBox whiteRow2;
    @FXML
    private VBox capturedPiecesBlackVbox;
    @FXML
    private VBox capturedPiecesWhiteVbox;

    // Timer
    private int player1Time;
    private int player2Time;
    private Timeline player1Timeline;
    private Timeline player2Timeline;

    private Board board;
    private Tile selectedTile = null;
    private Piece selectedPiece = null;
    private Tile lastMoveStart = null;
    private Tile lastMoveEnd = null;
    private HomeController homeController;
    private Client client;
    private String opponentName;
    private boolean isMyTurn = false;
    private boolean playerColor = false;
    private Timeline countdownTimeline;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Khởi tạo bàn cờ
        Board board = new Board();
        setBoard(board);
        // Xử lý sự kiện click vào ô chat
        chatTextField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                sendMessage();
            }
        });
        // Xử lý sự kiện click chuột vào ô cờ
        gridPane.setOnMouseClicked(this::handleMouseClick);
        initializeTimer();
        stopAllTimeLine();
    }

    public void setBoard(Board board) {
        this.board = board;
        board.setGridPane(gridPane); // Truyền gridPane vào Board

        // Tạo các Tile và thêm vào GridPane
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Tile tile = board.getTile(row, col);
                gridPane.add(tile, col, row); // Thêm Tile (StackPane) vào GridPane
            }
        }

        // Đặt các quân cờ vào vị trí ban đầu
        board.initializePieces();
    }


    private void handleMouseClick(MouseEvent event) {
        int col = (int) (event.getX() / 100);
        int row = (int) (event.getY() / 100);

        Tile clickedTile = board.getTile(row, col);
        System.out.println(clickedTile.getPiece());
        System.out.println(clickedTile);
        // 1. Kiểm tra ô được click
        if (clickedTile == null) {
            // Click chuột ra ngoài bàn cờ, bỏ qua
            return;
        }

        // 2. Xử lý chọn/bỏ chọn quân cờ
            // Chưa có quân cờ nào được chọn
        if (selectedPiece == null) {
                // Click vào quân cờ của người chơi hiện tại, chọn quân cờ
            if (clickedTile.hasPiece() && clickedTile.getPiece().isWhite() == board.isWhiteTurn() && clickedTile.getPiece().isWhite() == playerColor) {
                selectedPiece = clickedTile.getPiece();
                selectedTile = clickedTile;
                highlightPossibleMoves(selectedPiece);
                higlightTile(clickedTile);
            }
        } else {
            // Đã có quân cờ được chọn
            if (clickedTile == selectedTile) {
                // Click vào cùng một ô, bỏ chọn quân cờ
                selectedPiece = null;
                selectedTile = null;
                if (lastMoveStart != null && lastMoveEnd != null)
                    clearHighlightsPossibleMoves();
                else
                    clearHighlights();
            } else if (clickedTile.hasPiece() && clickedTile.getPiece().isWhite() == board.isWhiteTurn() && clickedTile != selectedTile) {
                // Click vào quân cờ của người chơi hiện tại, chọn quân cờ mới
                selectedPiece = clickedTile.getPiece();
                selectedTile = clickedTile;
                if (lastMoveStart != null && lastMoveEnd != null)
                    clearHighlightsPossibleMoves();
                else
                    clearHighlights();
                highlightPossibleMoves(selectedPiece);
                higlightTile(selectedTile);
            } else {
                // Click vào ô khác
                // 3. Handle move Piece
                // Check valid move(My turn, valid move, king is safe after move)
                if (isMyTurn && isValidMove(selectedPiece, clickedTile) && board.isKingSafeAfterMove(selectedPiece, clickedTile)) {

                    // Capture King opponent
                    if (clickedTile.hasPiece() && clickedTile.getPiece().getType() == PieceType.KING && selectedTile.getPiece().isWhite() != clickedTile.getPiece().isWhite()){
                        winning();
                    }

                    // Capture Piece(En passant)
                    if (selectedTile.getPiece().getType() == PieceType.PAWN && clickedTile == board.getEnPassantTargetTile()) {
                        Piece piece;
                        if (clickedTile.getRow() == 2)
                            piece = board.getTile(clickedTile.getRow()+1, clickedTile.getCol()).getPiece();
                        else piece = board.getTile(clickedTile.getRow()-1, clickedTile.getCol()).getPiece();
                        capturePiece(piece);
                    }

                    //Pawn promotion
                    if (selectedTile.getPiece().getType() == PieceType.PAWN && (clickedTile.getRow() == 0 || clickedTile.getRow() == 7)) {
                        //Trường hợp tốt ăn quân cờ khác hoặc tiến tới ô phong cấp
                        if (isMyTurn && !clickedTile.hasPiece() || isMyTurn && clickedTile.hasPiece() && clickedTile.getPiece().getType() != PieceType.KING && clickedTile.getPiece().isWhite() != selectedTile.getPiece().isWhite()) {
                            if (clickedTile.hasPiece()) {
                                capturePiece(clickedTile.getPiece());

                            }
                            // Nước đi hợp lệ, di chuyển quân cờ
                            board.movePiece(selectedPiece, clickedTile);
                            showPromotionPopup(createMove(selectedTile, clickedTile));
                        }
                        else {  //Trường hợp tốt ăn vua ở ô phong cấp
                            capturePiece(clickedTile.getPiece());
                            board.movePiece(selectedPiece, clickedTile);
                            try {
                                client.movePiece(createMove(selectedTile, clickedTile));
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                    else {
                        // Nếu ô đích có quân cờ thêm quân cờ bị ăn vào vbox
                        if (clickedTile.hasPiece() && clickedTile.getPiece().isWhite() != playerColor) {
                            capturePiece(clickedTile.getPiece());

                        }
                        // Nước đi hợp lệ, di chuyển quân cờ
                        board.movePiece(selectedPiece, clickedTile);

                        // Gửi nước đi đến server
                        try {
                            client.movePiece(createMove(selectedTile, clickedTile));
                            startOpponentTurn(playerColor);
                            System.out.println("Sent move to server! "+createMove(selectedTile, clickedTile).toString());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }



                    // Highlight nước đi vừa thực hiện
                    highlightMoved(selectedTile, clickedTile);

                    // Đánh dấu đã di chuyển quân cờ
                    isMyTurn = false;
                }
                    // Nước đi không hợp lệ, bỏ chọn quân cờ
                clearHighlightsPossibleMoves();
                selectedPiece = null;
                selectedTile = null;
            }
        }
    }

    //Highlight
    private void highlightPossibleMoves(Piece piece) {
        List<Tile> possibleMoves = piece.getPossibleMoves(board, selectedTile);
        Glow glow = new Glow(0.75);
        for (Tile tile : possibleMoves) {
            if (tile != null) {
//                tile.getChildren().getFirst().setStyle(piece.isWhite() ? "-fx-fill: #eeeed2" : "-fx-fill: #769656;"); // Tô sáng nền của Tile
                if (tile.hasPiece() || tile == board.getEnPassantTargetTile())
                    tile.getChildren().getFirst().setStyle("-fx-fill: #FF3333;"); // Tô sáng nền của Tile màu đỏ
//                else
//                    tile.getChildren().getFirst().setStyle("-fx-fill: #fcf388;"); // Tô sáng nền của Tile màu vàng
                tile.setEffect(glow);

            }
        }
    }

    private void clearHighlights() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Tile tile = board.getTile(row, col);
                tile.getChildren().getFirst().setStyle(tile.isLight() ? "-fx-fill: #eeeed2" : "-fx-fill: #769656;");
                tile.setEffect(null);
            }
        }
    }

    private void clearHighlightsPossibleMoves(){
        clearHighlights();
        if (lastMoveStart != null)
            higlightTile(lastMoveStart);
        if (lastMoveEnd != null)
            higlightTile(lastMoveEnd);
        //If King is in check higlight King Tile
        if (board.isKingInCheck(playerColor)) {
            highlightKingInCheck(playerColor);
        } else if (board.isKingInCheck(!playerColor)) {
            highlightKingInCheck(!playerColor);
        }
    }
    private void higlightTile(Tile tile) {
        if (tile.isLight())
            tile.getChildren().getFirst().setStyle("-fx-fill: #f5f682;"); // Tô sáng nền của Tile
        else
            tile.getChildren().getFirst().setStyle("-fx-fill: #b9ca43;"); // Tô sáng nền của Tile
        tile.setEffect(null);
    }

    public void highlightMoved(Tile startTile, Tile endTile) {
        // Clear any existing highlights
        clearHighlights();

        // Highlight the starting and ending tiles
        higlightTile(startTile);
        higlightTile(endTile);

        // Store the tiles for clearing later
        lastMoveStart = startTile;
        lastMoveEnd = endTile;
    }

    public void highlightKingInCheck(boolean color) {
        if (color) {
            board.getKing(true).getChildren().getFirst().setStyle("-fx-fill: #ff0000;");
        } else {
            board.getKing(false).getChildren().getFirst().setStyle("-fx-fill: #ff0000;");
        }
    }

    private boolean isValidMove(Piece piece, Tile destinationTile) {
        List<Tile> possibleMoves = piece.getPossibleMoves(board, selectedTile);
        return possibleMoves.contains(destinationTile);
    }

    private void showPromotionPopup(Move move) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/onlinechessgame/controllers/promotePawn.fxml"));
            Stage popup = new Stage();
            Scene scene = new Scene(loader.load());
            popup.setScene(scene);
            popup.setResizable(false);
            popup.initStyle(StageStyle.TRANSPARENT);
            popup.initModality(Modality.APPLICATION_MODAL);
            ((PromotePawnController)loader.getController()).setup(board, move, client, this);


            popup.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startGame(boolean color){
        Timeline temp = new Timeline(new KeyFrame(Duration.seconds(5), event -> {
        }));
        temp.setCycleCount(Timeline.INDEFINITE);
        temp.play();
        playerColor = color;
        isMyTurn = color;
        board.setWhiteTurn(true);
        startPlayer1Turn();
    }

    public void setInformationPlayers(User player, User opponent){
        this.opponentName = opponent.getUsername();
        if (playerColor){
            //White
            namePlayer1Label.setText(player.getUsername());
            pointPlayer1Label.setText(String.valueOf(player.getPoint()));
            namePlayer2Label.setText(opponent.getUsername());
            pointPlayer2Label.setText(String.valueOf(opponent.getPoint()));
        } else {
            //Black
            namePlayer1Label.setText(opponent.getUsername());
            pointPlayer1Label.setText(String.valueOf(opponent.getPoint()));
            namePlayer2Label.setText(player.getUsername());
            pointPlayer2Label.setText(String.valueOf(player.getPoint()));
        }
    }
    private Move createMove(Tile selectedTile, Tile clickedTile) {
        return new Move(selectedTile.getRow(), selectedTile.getCol(), clickedTile.getRow(), clickedTile.getCol());
    }
    public void opponentMovePiece(Move move) {
        Platform.runLater(() -> {
            Tile startTile = board.getTile(move.getStartRow(), move.getStartCol());
            Tile endTile = board.getTile(move.getEndRow(), move.getEndCol());

            //Capture
            if (endTile.hasPiece() && endTile.getPiece().isWhite() != startTile.getPiece().isWhite()) {
                capturePiece(endTile.getPiece());
            }

            //Capture (En passant)
            if (startTile.getPiece().getType() == PieceType.PAWN && endTile == board.getEnPassantTargetTile()) {
                if (endTile.getRow() == 2)
                    capturePiece(board.getTile(endTile.getRow() + 1, endTile.getCol()).getPiece());
                else capturePiece(board.getTile(endTile.getRow() - 1, endTile.getCol()).getPiece());
            }

            board.movePiece(startTile.getPiece(), endTile);
            highlightMoved(startTile, endTile);
            if (board.isKingInCheck(!playerColor)) {
                highlightKingInCheck(!playerColor);
            } else if (board.isKingInCheck(playerColor)) {
                highlightKingInCheck(playerColor);
            }
            isMyTurn = true;
            startUserTurn(playerColor);
        });
    }

    public Board getBoard() {
        return board;
    }

    public void setClient(Client client) {
        this.client = client;
        client.setController(this);
    }

    public void winning(){
        resultPane.setVisible(true);
        winnerLabel.setText((playerColor ? namePlayer1Label.getText() : namePlayer2Label.getText()) + " Won!");
        try {
            System.out.println("Sent win to server!");
            client.winGame();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        gridPane.setDisable(true);
        stopAllTimeLine();
    }

    public void losing(){
        resultPane.setVisible(true);
        isMyTurn = false;
        winnerLabel.setText((!playerColor ? namePlayer1Label.getText() : namePlayer2Label.getText()) + " Won!");
        gridPane.setDisable(true);
        stopAllTimeLine();
    }

    public void sendLose(){
        try {
            client.loseGame();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        losing();
    }

    public void drawGame() throws IOException {
        client.drawGame();
        showDraw();
    }

    public void sendDraw() throws IOException {
        client.requestDraw();
    }

    public void draw() throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Do you want to accept the draw?", ButtonType.YES, ButtonType.NO);
        alert.setHeaderText("Draw");
        alert.showAndWait();
        if (alert.getResult() == ButtonType.YES) {
            client.requestAcceptDraw();
            stopAllTimeLine();
            showDraw();
        }
        else {
            alert.close();
        }
    }

    public void showDraw(){
        resultPane.setVisible(true);
        winnerLabel.setText("Draw!");
        gridPane.setDisable(true);
    }

    public void setHomeController(HomeController homeController) {
        this.homeController = homeController;
    }

    public void close() throws IOException {
        Stage stage = (Stage) gridPane.getScene().getWindow();
        homeController.show();
        homeController.updateAfterMatch();
        client.requestOut();
        stage.close();
    }

    public void rematch(){
        try {
            client.requestRematch();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        waitingPane.setVisible(true);
        startCountdownWaitingRematch();
    }

    public void cancelRematch(){
        try {
            client.requestCancelRematch();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        waitingPane.setVisible(false);
        stopCountdownWaitingRematch();
    }

    public void newGame(boolean isWhite) {
        resetGameState(isWhite);
    }

    private void resetGameState(boolean isWhite) {
        gridPane.setDisable(false);
        // Đặt lại màu của các quân cờ
        board.deleteAllPiece();
        board.initializePieces();


        // Xóa lịch sử nước đi
        selectedTile = null;
        selectedPiece = null;
        lastMoveStart = null;
        lastMoveEnd = null;
        clearHighlights();

        // Đặt lại timer
        stopAllTimeLine();

        // Đặt lại màu của người chơi
        // Đặt lại lượt chơi
        startGame(isWhite);

        // Đóng thông báo kết quả
        resultPane.setVisible(false);
        waitingPane.setVisible(false);

        player1TimerLabel.setText("00:10:00");
        player2TimerLabel.setText("00:10:00");
    }

    //chat
    public void displayMessage(String message) {
        chatTextArea.appendText("["+opponentName + "]: " + message + "\n");
        chatTextArea.setScrollTop(Double.MAX_VALUE);
    }

    public void sendMessage() {
        if (chatTextField.getText().trim().isEmpty() || chatTextField.getText().isBlank()) {
            return;
        }
        String message = chatTextField.getText();
        try {
            client.sendChat(message);
            chatTextArea.appendText("[You]: " + message + "\n");
            chatTextField.clear();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    //Rematch
    public void startCountdownWaitingRematch() {
        if (countdownTimeline != null) {
            countdownTimeline.stop();
        }

        countdownTimeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            String currentTimeText = timerLabel.getText().substring(6); // Lấy phần giây trong định dạng "00:00:20"
            int currentTime = Integer.parseInt(currentTimeText);
            currentTime--;
            String formattedTime = String.format("00:00:%02d", currentTime);
            timerLabel.setText(formattedTime);
            progressBar.setProgress(currentTime / 20.0);

            if (currentTime <= 0) {
                countdownTimeline.stop();
                try {
                    client.requestCancelRematch();
                    waitingPane.setVisible(false);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }));

        countdownTimeline.setCycleCount(20);
        timerLabel.setText("00:00:20");
        progressBar.setProgress(1.0);
        countdownTimeline.play();
    }

    public void stopCountdownWaitingRematch() {
        if (countdownTimeline != null) {
            countdownTimeline.stop();
        }
    }

    private void initializeTimer(){
        player1Time = 600; // 10 minutes in seconds
        player2Time = 600; // 10 minutes in seconds
        player1Timeline = null;
        player2Timeline = null;
        player1Timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            player1Time--;
            player1TimerLabel.setText(formatTime(player1Time));
            if (player1Time <= 0) {
                stopAllTimeLine();
                // Player 1 loses
                if (!playerColor)
                    winning();
            }
        }));
        player1Timeline.setCycleCount(Timeline.INDEFINITE);

        player2Timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            player2Time--;
            player2TimerLabel.setText(formatTime(player2Time));
            if (player2Time <= 0) {
                stopAllTimeLine();

                // Player 2 loses
                if (playerColor)
                    winning();
            }
        }));
        player2Timeline.setCycleCount(Timeline.INDEFINITE);
    }

    private String formatTime(int timeInSeconds) {
        int minutes = timeInSeconds / 60;
        int seconds = timeInSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    public void startUserTurn(boolean playerColor) {
        //Check if the game is over
        if (board.isCheckmate(playerColor)) {
            sendLose();
        } else if (board.isStalemate(playerColor)) {
            try {
                drawGame();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            // Start the timer for the current player
            if (playerColor) {
                startPlayer1Turn();
            } else {
                startPlayer2Turn();
            }
        }
    }

    public void startOpponentTurn(boolean opponentColor) {
        if (opponentColor) {
            startPlayer2Turn();
        } else {
            startPlayer1Turn();
        }
    }

    private void startPlayer1Turn() {
        player2Timeline.stop();
        player2Hbox.setDisable(true);
        player1Hbox.setDisable(false);
        player1Timeline.play();
    }

    private void startPlayer2Turn() {
        player1Timeline.stop();
        player1Hbox.setDisable(true);
        player2Hbox.setDisable(false);
        player2Timeline.play();
    }

    private void stopAllTimeLine(){
        player1Timeline.stop();
        player2Timeline.stop();
        player1Time = 600; // 10 minutes in seconds
        player2Time = 600; // 10 minutes in seconds
        player1Hbox.setDisable(true);
        player2Hbox.setDisable(true);
    }

    public boolean getPlayerColor() {
        return playerColor;
    }

    public void resign() throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to resign?", ButtonType.YES, ButtonType.NO);
        alert.setHeaderText("Resign");
        alert.showAndWait();
        if (alert.getResult() == ButtonType.YES) {
            client.loseGame();
        }
        else {
            alert.close();
        }
    }

    public void capturePiece(Piece piece) {
        // Add the captured piece to the appropriate VBox
        ImageView imageView = new ImageView(piece.getImage());
        imageView.setFitWidth(30);
        imageView.setFitHeight(30);

        if (!piece.isWhite()) {
            if (whiteRow1.getChildren().size() < 8) {
                whiteRow1.getChildren().add(imageView);
            } else {
                whiteRow2.getChildren().add(imageView);
            }
        } else {
            if (blackRow1.getChildren().size() < 8) {
                blackRow1.getChildren().add(imageView);
            } else {
                blackRow2.getChildren().add(imageView);
            }
        }
    }

}
