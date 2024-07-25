This readme will simply cover what each class file did for the application. It will also include a approximately 3 minute long video demonstration of the app and its features being explained 

GameBoard.java: This class is where the GUI for the main game was implemented. It included functions that created the keyboard, gridpane, and buttons. It also helped link the stats board. this class was responsible for handaling what the user would see when playing the game. The color updates and buttons were all defined and given purpose in this class.

GameNumberManager.java: This class was a simple one that just helped us keep track of the game for many different reasons

Log.java: This class was made in order to track the moves made during a game and also the outcome of the game including win, loss, and did not complete

SelectWord.java: This class was made to select a random word from the list of words provided. It also checked if a word was valid

ShadowDataStructure.java: This class is where all the backend win checking and tracking of guesses happened. This class controlled the game logic and held the state of the game. The shadow board was a 2d array of chars. In the class there were functions defined to help validate the letters in the guesses and also a function that helped check win conditions.

Stats.java: This class controlled the stats board that is essential to the game. It handled updating the labels associated with the stats and it handled saving, loading, and resetting the stats.

Demo video: https://youtu.be/US7PwG80PyI
