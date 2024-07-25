import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.geometry.Pos;
import java.io.IOException;
import javafx.geometry.Insets;

public class GameBoard extends Application {
    private static final int MAX_GUESSES = 6;
    private static final int WORD_LENGTH = 5;
    private TextField[][] letterFields;
    private Button[] keyboardButtons;
    private Button submitButton, resetButton, statsButton;
    private ShadowDataStructure shadowData;
    private SelectWord selectWord;
    private VBox root;
    private int currentRow = 0;
    private VBox keyboardLayout;
    private Stage primaryStage;
    private Stage statsStage; 
    private Stats statsController; 
    private int gameNumber = 1;
    private boolean gameActive = true;

    @Override
    public void start(Stage primaryStage) throws Exception { 
        this.primaryStage = primaryStage;
        root = new VBox(10);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #ADD8E6;");
        gameNumber = GameNumberManager.loadGameNumber();
        initStatsView();
        Label titleLabel = new Label("WORDLE");
        titleLabel.setFont(new Font("Arial", 36));
        titleLabel.setPadding(new Insets(20, 0, 20, 0));
        root.getChildren().add(titleLabel);

        selectWord = new SelectWord("words.txt");
        try {
            shadowData = new ShadowDataStructure(WORD_LENGTH, MAX_GUESSES, selectWord);
        } catch (IOException e) {
            showAlert("Error", "Failed to load words: " + e.getMessage());
            return;
        }

        initializeBoard();
        initializeKeyboard();
        initializeControlButtons();
        initializeStatsButton();

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("Word Guessing Game");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void initStatsView() { // initialize the stats window
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Stats.fxml"));
            Parent statsRoot = loader.load(); 
            statsController = loader.getController();
            statsController.setGameStage(primaryStage);
            statsStage = new Stage();
            statsStage.setTitle("Game Stats");
            statsStage.setScene(new Scene(statsRoot));
        } catch (IOException e) {
            showAlert("Error", "Failed to initialize stats view: " + e.getMessage());
        }
    }

    private void initializeStatsButton() { // button on game screen to view the stats board
        statsButton = new Button("View Stats");
        statsButton.setFont(new Font("Arial", 18));
        statsButton.setOnAction(event -> showStatsBoard());
        root.getChildren().add(statsButton);
    }

    private void showStatsBoard() { //show the stats board 
        if (statsStage != null) {
            statsStage.show();
            primaryStage.hide(); // hide the main game window when the stats board is opened
        } else {
            showAlert("Error", "Stats view is not initialized.");
        }
    }


    private void initializeBoard() { // initialize the game board
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(10);
        gridPane.setVgap(5);

        letterFields = new TextField[MAX_GUESSES][WORD_LENGTH];
        for (int i = 0; i < MAX_GUESSES; i++) {
            for (int j = 0; j < WORD_LENGTH; j++) {
                TextField textField = new TextField();
                textField.setFont(new Font("Arial", 20));
                textField.setPrefSize(45,45);
                textField.setMaxSize(45,45);
                textField.setAlignment(Pos.CENTER);
                textField.setEditable(false);
                gridPane.add(textField, j, i);
                letterFields[i][j] = textField;
            }
        }
        root.getChildren().add(gridPane);
    }

    private void initializeKeyboard() { // make the keyboard for our game 
        keyboardButtons = new Button[27];
        keyboardLayout = new VBox(10);
        keyboardLayout.setAlignment(Pos.CENTER);

        // first layer looks just like normal keyboard
        HBox qwertyRow = createKeyboardRow("QWERTYUIOP");
        keyboardLayout.getChildren().add(qwertyRow);

        // second layer
        HBox asdfRow = createKeyboardRow("ASDFGHJKL");
        keyboardLayout.getChildren().add(asdfRow);

        // third layer
        HBox zxcvRow = createKeyboardRow("ZXCVBNM");
        keyboardLayout.getChildren().add(zxcvRow);

        // adding a backspace to the third row
        Button backspaceButton = new Button("Backspace");
        backspaceButton.setFont(new Font("Arial", 18));
        backspaceButton.setOnAction(event -> handleBackspace());
        zxcvRow.getChildren().add(backspaceButton);

        keyboardButtons[keyboardButtons.length - 1] = backspaceButton;

        root.getChildren().add(keyboardLayout);
    }

    private HBox createKeyboardRow(String keys) { //function to create the keyboard row it takes the letters that will be in that row as a string
        HBox row = new HBox(5);
        row.setAlignment(Pos.CENTER);
        int startIndex = 0;

        for (Button btn : keyboardButtons) {
            if (btn != null) startIndex++;
        }

        for (int i = 0; i < keys.length(); i++) { //for loop going through string
            Button button = new Button(String.valueOf(keys.charAt(i)));
            button.setFont(new Font("Arial", 18));
            button.setMinWidth(30);
            final int idx = startIndex + i;
            final int adjustedIndex = idx - startIndex;

            button.setOnAction(event -> handleKeyPress(keys.charAt(adjustedIndex)));
            keyboardButtons[idx] = button;
            row.getChildren().add(button);
        }

        return row;
    }

    private void handleBackspace() { // this handles backspace functionality by clearing last non empty tf 
        if (currentRow < MAX_GUESSES) {
            TextField[] currentFields = letterFields[currentRow];
            for (int i = currentFields.length - 1; i >= 0; i--) {
                TextField tf = currentFields[i];
                if (!tf.getText().isEmpty()) {
                    tf.setText("");
                    break;
                }
            }
        }
    }

    private void initializeControlButtons() { // this is where the submit and reset buttons were created
        submitButton = new Button("Submit Guess");
        submitButton.setFont(new Font("Arial", 18));
        submitButton.setOnAction(event -> submitGuess());

        resetButton = new Button("Reset Game");
        resetButton.setFont(new Font("Arial", 18));
        resetButton.setOnAction(event -> resetGame());

        HBox controlPanel = new HBox(10, submitButton, resetButton);
        controlPanel.setAlignment(Pos.CENTER);
        root.getChildren().add(controlPanel);
    }

    private void handleKeyPress(char key) {// this function controls the key presses made. populates next tf
        if (currentRow < MAX_GUESSES) {
            TextField[] currentFields = letterFields[currentRow];
            for (int i = 0; i < currentFields.length; i++) {
                TextField tf = currentFields[i];
                if (tf.getText().isEmpty()) {
                    tf.setText(String.valueOf(key));
                    // call the logMove funciton here to write to the log file
                    Log.logMove(gameNumber, currentRow + 1, i, key);
                    break;
                }
            }
        }
    }
    

    private void submitGuess() {//very important function it allows for us to sumbit guesses
        if (currentRow >= MAX_GUESSES) { // if we have already reached max guesses show this alert
            showAlert("Error", "No more guesses allowed!");
            return;
        }
    
        String guess = "";
        for (TextField tf : letterFields[currentRow]) { // get the guess from the tf
            guess += tf.getText();
        }
    
        if (guess.length() != WORD_LENGTH) { //length checking
            showAlert("Error", "Please enter a full word!");
            return;
        }
    
        if (!shadowData.isValidWord(guess)) { //check if the word is valid
            showAlert("Invalid Word", "The word is not in the list. Please try another word.");
            return;
        }
        shadowData.updateBoard(guess);//update board based on guess
        updateBoardDisplay();//update the display
        updateKeyboard();//update the keyboard
    
        if (shadowData.isWinner()) { // very important checks for winner
            int guessrow = currentRow+1; // we needed to make this the increment guess count woulkdnt work without it
            showWinAlert(); //shows the win alertt
            disableInput();//disable inputs when we win
            Log.logResult(gameNumber, "won"); //log the win usinf the game num
            System.out.println("Winner detected at guess number: " + (guessrow));
            if (statsController != null) {
                statsController.incrementGuessCount(guessrow); // updtate the guesses in the stats board
            } else {
                System.out.println("Stats controller is null!");
            }
            gameNumber++; 
            GameNumberManager.saveGameNumber(gameNumber);// save game number after its incremented
            return;
        }
        currentRow++;
        if (currentRow == MAX_GUESSES) {
            showAlert("Game Over", "You've used all your guesses. The word was: " + shadowData.getCorrectWord());//say correct word after we are out of guesses
            disableInput();
            Log.logResult(gameNumber, "lost"); // log result
            gameNumber++; 
            GameNumberManager.saveGameNumber(gameNumber);// save game number after its incremented
        }
    }

    private void showWinAlert() { // show the win alert 
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game Over");
        alert.setHeaderText("Congratulations!");
        alert.setContentText("You guessed the correct word! Press OK and reset the game to play again.");

        // add a button type to reset the game directly from the alert.
        alert.getButtonTypes().setAll(new ButtonType("OK", ButtonBar.ButtonData.OK_DONE));
        alert.showAndWait().ifPresent(response -> {
            if (response.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                gameActive = false;
                resetGame();
                showStatsBoard();
            }
        });
    }

    private void updateBoardDisplay() {// this function was what allows for the colors to show up after validation
        char[][] board = shadowData.getShadowBoard();
        String correctWord = shadowData.getCorrectWord();
        for (int i = 0; i <= currentRow; i++) {
            for (int j = 0; j < WORD_LENGTH; j++) {
                TextField tf = letterFields[i][j];
                char c = board[i][j];
                tf.setText(String.valueOf(c).toUpperCase()); // make sure the text is upper case
                if (c == correctWord.charAt(j)) {
                    tf.setStyle("-fx-background-color: green; -fx-text-fill: white;");
                } else if (correctWord.indexOf(Character.toLowerCase(c)) >= 0) {
                    tf.setStyle("-fx-background-color: yellow; -fx-text-fill: black;");
                } else {
                    tf.setStyle("-fx-background-color: red; -fx-text-fill: white;");
                }
            }
        }
    }

    private void updateKeyboard() {
        for (int i = 0; i < MAX_GUESSES; i++) {
            for (int j = 0; j < WORD_LENGTH; j++) {
                TextField tf = letterFields[i][j];
                // if the background is red in the tf then we disable the input on the key board and make it red
                if (tf.getStyle().contains("red")) {
                    char incorrectLetter = tf.getText().toLowerCase().charAt(0);
                    for (Button button : keyboardButtons) {
                        if (button.getText().toLowerCase().charAt(0) == incorrectLetter) {
                            button.setDisable(true);
                            button.setStyle("-fx-background-color: red; -fx-text-fill: white;");
                        }
                    }
                }
            }
        }
    }

    private void resetGame() { // completely reset the game 
        if(gameActive){
            Log.logResult(gameNumber, "not finished");
            gameNumber++;
            GameNumberManager.saveGameNumber(gameNumber);// save game number after its incremented
        }
        try {
            shadowData = new ShadowDataStructure(WORD_LENGTH, MAX_GUESSES, selectWord); // new shadow board
            currentRow = 0;//curr row is 0 now
            for (TextField[] row : letterFields) { // make all text fields empty
                for (TextField field : row) {
                    field.setText("");
                    field.setEditable(false);
                    // reset styles to default
                    field.setStyle("-fx-background-color: white; -fx-text-fill: black; -fx-border-color: lightgray; -fx-border-width: 1;");
                }
            }
            resetKeyboard(); // reset keyboard
            enableInput();   // all inputs should be enabled
        } catch (IOException e) {
            showAlert("Error", "Failed to reset the game: " + e.getMessage());
        }
    }
    private void resetKeyboard() {// reset keyboard make it all default style and make the inputs enabled
        for (Button button : keyboardButtons) {
            if (button != null) {
                button.setDisable(false); // enable the button
                // reset to default style
                button.setStyle("");
            }
        }
    }


    private void disableInput() { // simple function to disable inputs
        for (Button button : keyboardButtons) {
            button.setDisable(true);
        }
        submitButton.setDisable(true);
    }

    private void enableInput() { //function to enable keyboard
        for (Button button : keyboardButtons) {
            button.setDisable(false);
        }
        submitButton.setDisable(false);
    }

    private void showAlert(String title, String content) { //function to show the alert
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}