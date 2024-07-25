import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class ShadowDataStructure {
    private final char[][] shadowBoard;
    private final int maxGuesses;
    private int currentGuess;
    private final SelectWord wordValidator;
    private final String correctWord;

    public ShadowDataStructure(int wordLength, int maxGuesses, SelectWord wordValidator) throws IOException { //set up shadowboard
        this.maxGuesses = maxGuesses;
        this.shadowBoard = new char[maxGuesses][wordLength];
        this.currentGuess = 0;
        this.wordValidator = wordValidator;
        this.correctWord = wordValidator.getRandomWord().toLowerCase(); //when this is uncommented it selects a random word 
        //this.correctWord = "apple"; //for demo purposes

        for (int i = 0; i < maxGuesses; i++) {
            java.util.Arrays.fill(shadowBoard[i], '_');
        }
    }

    public void updateBoard(String guess) { //update the board based on the guess
        if (currentGuess >= maxGuesses || guess.length() != correctWord.length()) {
            throw new IllegalStateException("No more guesses allowed or incorrect word length");
        }

        guess = guess.toLowerCase();
        Set<Integer> usedIndices = new HashSet<>();
        for (int i = 0; i < guess.length(); i++) {//checks each letter in each word and if it is in the right spot it becomes upper case in the shadow board
            if (guess.charAt(i) == correctWord.charAt(i)) {
                shadowBoard[currentGuess][i] = Character.toUpperCase(guess.charAt(i));
                usedIndices.add(i);
            }
        }

        for (int i = 0; i < guess.length(); i++) { //if the letter exists but in the wrong spot its indicated by a lowercase in the shadow board 
            if (!usedIndices.contains(i) && correctWord.indexOf(guess.charAt(i)) != -1) {
                int index = correctWord.indexOf(guess.charAt(i));
                if (!usedIndices.contains(index)) {
                    shadowBoard[currentGuess][i] = Character.toLowerCase(guess.charAt(i));
                    usedIndices.add(index);
                }
            } else {
                shadowBoard[currentGuess][i] = guess.charAt(i);
            }
        }
        currentGuess++;
    }

    public boolean isWinner() { //check for winner
        if (currentGuess == 0) { // no guesses have been made yet
            return false;
        }
        char[] lastGuess = shadowBoard[currentGuess - 1];
        for (int i = 0; i < lastGuess.length; i++) {
            if (Character.toLowerCase(lastGuess[i]) != correctWord.charAt(i)) {
                return false; // If any character does not match the correct word exactly, the guess was not correct
            }
        }
        return true; // All characters matched the correct word exactly
    }

    public char[][] getShadowBoard() {
        return shadowBoard;
    }

    public String getCorrectWord() {
        return correctWord;
    }

    public boolean isValidWord(String word) { //check if word is valid and in list
        return wordValidator.isValidWord(word);
    }
}
