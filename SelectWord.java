import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;
import java.io.IOException;
//created some c++ code to convert the words given to us to a list of words as seen in words.txt
public class SelectWord {
    private List<String> words;

    public SelectWord(String filePath) throws IOException { //read the lines from the file
        words = Files.readAllLines(Paths.get(filePath)); 
    }

    public String getRandomWord() { //select a random word
        if (words.isEmpty()) {
            throw new IllegalStateException("Word list is empty.");
        }
        return words.get(new Random().nextInt(words.size()));
    }

    public boolean isValidWord(String word) { // check if the word is in the file
        return words.contains(word.toLowerCase());
    }
}
