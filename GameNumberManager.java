import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class GameNumberManager {
    private static final String GAME_NUMBER_FILE = "gameNumber.txt";

    public static int loadGameNumber() { // load the game num from the file 
        try {
            if (Files.exists(Paths.get(GAME_NUMBER_FILE))) {
                List<String> lines = Files.readAllLines(Paths.get(GAME_NUMBER_FILE));
                if (!lines.isEmpty()) {
                    return Integer.parseInt(lines.get(0));
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading game number: " + e.getMessage());
        }
        return 1; //default
    }

    public static void saveGameNumber(int gameNumber) { // save the game number to the file
        try {
            Files.writeString(Paths.get(GAME_NUMBER_FILE), String.valueOf(gameNumber));
        } catch (IOException e) {
            System.err.println("Error saving game number: " + e.getMessage());
        }
    }
}
