import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.io.IOException;
import java.util.List;

public class Stats {
    @FXML
    private Button resetStats;

    @FXML
    private Button BackToGame;

    @FXML
    private Label oneGuessCount, twoGuessCount, threeGuessCount, fourGuessCount, fiveGuessCount, sixGuessCount;

    @FXML
    private Label winPercCounter, gamesPlayedCounter, currentStreak;

    private Stage gameStage; 
    private static final String STATS_FILE = "stats.txt";
    private static final String GAME_STATS_NUMBER_FILE = "gameStatsNumber.txt";

    @FXML
    public void initialize() {
        loadStats();
        BackToGame.setOnAction(event -> {
            gameStage.show(); // Show the main game stage
        });
        resetStats.setOnAction(event -> resetAllStats());
    }

    public void incrementGuessCount(int guesses) { // this is to increment the label
        if (guesses == 1) {
            incrementLabel(oneGuessCount);
        } else if (guesses == 2) {
            incrementLabel(twoGuessCount);
        } else if (guesses == 3) {
            incrementLabel(threeGuessCount);
        } else if (guesses == 4) {
            incrementLabel(fourGuessCount);
        } else if (guesses == 5) {
            incrementLabel(fiveGuessCount);
        } else if (guesses == 6) {
            incrementLabel(sixGuessCount);
        }
        StatsGameNumber();
        saveStats();
        calcWinPerc();
    }

    private void incrementLabel(Label label) { //parseInt and then setText
        int count = Integer.parseInt(label.getText());
        count++;
        label.setText(String.valueOf(count));
        System.out.println("Updated " + label.getId() + " to " + count); 
    }

    private void saveStats() { //save stats to a file
        String data = oneGuessCount.getText() + "," +
                      twoGuessCount.getText() + "," +
                      threeGuessCount.getText() + "," +
                      fourGuessCount.getText() + "," +
                      fiveGuessCount.getText() + "," +
                      sixGuessCount.getText();
        try {
            Files.writeString(Paths.get(STATS_FILE), data);
        } catch (IOException e) {
            System.err.println("Error saving stats: " + e.getMessage());
        }
    }

    private void loadStats() {
        try {
            if (Files.exists(Paths.get(STATS_FILE))) {
                List<String> lines = Files.readAllLines(Paths.get(STATS_FILE));
                if (!lines.isEmpty()) { //read from the file
                    String[] counts = lines.get(0).split(",");
                    oneGuessCount.setText(counts[0]);
                    twoGuessCount.setText(counts[1]);
                    threeGuessCount.setText(counts[2]);
                    fourGuessCount.setText(counts[3]);
                    fiveGuessCount.setText(counts[4]);
                    sixGuessCount.setText(counts[5]);
                }
            }
    
            // Load game stats number
            if (Files.exists(Paths.get(GAME_STATS_NUMBER_FILE))) {
                List<String> gameStatsLines = Files.readAllLines(Paths.get(GAME_STATS_NUMBER_FILE));
                if (!gameStatsLines.isEmpty()) {
                    gamesPlayedCounter.setText(gameStatsLines.get(0)); // Initialize games played counter
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading stats: " + e.getMessage());
        }
    }
    
    private void resetAllStats() { //reset stats using button
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to reset all statistics?", ButtonType.YES, ButtonType.NO);
        confirmationAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                oneGuessCount.setText("0");
                twoGuessCount.setText("0");
                threeGuessCount.setText("0");
                fourGuessCount.setText("0");
                fiveGuessCount.setText("0");
                sixGuessCount.setText("0");
                resetGameNumber();
                saveStats();  
                calcWinPerc();
            }
        });
    }
    private void StatsGameNumber() { //stats number for the game increment here
        try {
            int gamesPlayed = Integer.parseInt(gamesPlayedCounter.getText()) + 1; // Increment by one
            gamesPlayedCounter.setText(String.valueOf(gamesPlayed)); // Update the counter
            saveStatsGameNumber(gamesPlayed); // Save the new number
        } catch (NumberFormatException e) {
            System.err.println("Error updating games played: " + e.getMessage());
        }
    }
    

    private void saveStatsGameNumber(int gameNumber) {//save stats number
        try {
            Files.writeString(Paths.get(GAME_STATS_NUMBER_FILE), String.valueOf(gameNumber), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        } catch (IOException e) {
            System.err.println("Error saving game stats number: " + e.getMessage());
        }
    }

    private void resetGameNumber() {//reset stats number
        try {
            Files.writeString(Paths.get(GAME_STATS_NUMBER_FILE), "0");
            gamesPlayedCounter.setText("0");
        } catch (IOException e) {
            System.err.println("Error resetting game stats number: " + e.getMessage());
        }
    }

    private void calcWinPerc() { //win percentage calc
        try {
            int gamesPlayed = Integer.parseInt(gamesPlayedCounter.getText());
            int wins = 0;
            
            if (Files.exists(Paths.get(STATS_FILE))) {
                List<String> lines = Files.readAllLines(Paths.get(STATS_FILE));
                if (!lines.isEmpty()) {
                    wins = Integer.parseInt(lines.get(0).split(",")[0]) +
                           Integer.parseInt(lines.get(0).split(",")[1]) +
                           Integer.parseInt(lines.get(0).split(",")[2]) +
                           Integer.parseInt(lines.get(0).split(",")[3]) +
                           Integer.parseInt(lines.get(0).split(",")[4]) +
                           Integer.parseInt(lines.get(0).split(",")[5]);
                }
            }

            double winPercentage = (gamesPlayed > 0) ? ((double) wins / gamesPlayed * 100) : 0.0;
            winPercCounter.setText(String.format("%.2f%%", winPercentage));
        } catch (IOException e) {
            System.err.println("Error calculating win percentage: " + e.getMessage());
        }
    }

    
    public void setGameStage(Stage gameStage) {
        this.gameStage = gameStage;
    }
}