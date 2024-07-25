import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class Log {
    private static final String GAME_LOG_PATH = "Log.txt";
    private static final String GAME_RESULTS_PATH = "gameResults.txt";

    public static void logMove(int gameNumber, int guessNumber, int position, char letter) { //we log move based on the guess number, game number, position and the letter itself
        String content = String.format("Game %d, Guess %d, Pos %d: %c%n", gameNumber, guessNumber, position, letter); // how we format the string 
        try {
            Files.writeString(Paths.get(GAME_LOG_PATH), content, StandardOpenOption.CREATE, StandardOpenOption.APPEND);//writing to the file
        } catch (IOException e) {
            System.err.println("Error writing to log file: " + e.getMessage());
        }
    }

    public static void logResult(int gameNumber, String result) { //log results from the games this is straight forward
        String content = "Game " + gameNumber + ": " + result + System.lineSeparator();
        try {
            Files.writeString(Paths.get(GAME_RESULTS_PATH), content, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.err.println("Error writing to results file: " + e.getMessage());
        }
    }
}