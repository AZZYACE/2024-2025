import java.util.*;
import java.util.Arrays;

public class BookwormTest2 {

    static LinkedList<String> toGuess = new LinkedList<>();
    static Queue<char[][]> addNewBoard = new LinkedList<>();
    static Stack<LinkedList<String>> undoMove = new Stack<>();
    static Set<String> dictionary = new HashSet<>();
    static Scanner input = new Scanner(System.in);
    static char[][] board;
    static int correctGuesses = 0;

    public static void menu() {
        System.out.println("Choose a category: ");
        System.out.println("1. Mythical Creatures");
        System.out.println("2. Zodiacs");
        System.out.println("3. Ancient Civilizations");
        System.out.println("4. Philosophers");
        System.out.println("5. Quit Game");
    }

    public static Set<String> getDictionaryForChoice(int choice) {
        switch (choice) {
            case 1:
                return new HashSet<>(Arrays.asList(
                    "DRAGON", "GRIFFIN","PHOENIX","UNICORN","CENTAUR","MERMAID","MINOTAUR","HYDRA",
                    "CHIMERA","KRAKEN","BASILISK","WEREWOLF","VAMPIRE","FAIRY","ELF","TROLL",
                    "GOBLIN","NYMPH","SIREN","PEGASUS","SPHINX","GORGON","YETI","BIGFOOT",
                    "THUNDERBIRD","CHUPACABRA","KITSUNE","TIKBALANG","NAGA","AMAROK"
                ));
            case 2:
                return new HashSet<>(Arrays.asList(
                    "ARIES", "TAURUS", "GEMINI", "CANCER", "LEO", "VIRGO", "LIBRA", "SCORPIO",
                    "SAGITTARIUS", "CAPRICORN", "AQUARIUS", "PISCES", "RAT", "OX", "TIGER", "RABBIT",
                    "DRAGON", "SNAKE", "HORSE", "GOAT", "MONKEY", "ROOSTER", "DOG", "PIG",
                    "MOON", "FIRE", "EARTH", "AIR", "WATER", "PLANET"
                ));
            case 3:
                return new HashSet<>(Arrays.asList(
                    "TONDO", "EGYPT", "INDUS", "CHINA", "SUMER", "AKKAD", "BABYLON", "ASSYRIA",
                    "PERSIA", "GREECE", "ROME", "MAYA", "AZTEC", "INCA", "OLMEC", "HITTITE",
                    "PHOENICIA", "CARTHAGE", "BYZANTINE", "CELTIC", "VIKING", "KHMER", "MALI", "ETHIOPIA",
                    "MACEDON", "SPARTA", "ATHENS", "MINOAN", "MYCENAE", "SAXON"
                ));
            case 4:
                return new HashSet<>(Arrays.asList(
                    "SOCRATES", "PLATO", "ARISTOTLE", "CONFUCIUS", "LAOZI", "DESCARTES", "KANT", "NIETZSCHE",
                    "HEGEL", "LOCKE", "HUME", "RUSSELL", "WITTGENSTEIN", "SARTRE", "HEIDEGGER", "HOBBES",
                    "MACHIAVELLI", "AQUINAS", "AUGUSTINE", "SPINOZA", "BERKELEY", "ROUSSEAU", "VOLTAIRE", "MARX",
                    "ENGELS", "RAWLS", "SIMONE", "POPPER", "CAMUS", "ZENO"
                ));
            default:
                return new HashSet<>();
        }
    }

    public static char[][] createRandomBoard() {
        char[][] board = new char[6][6];
        Random rand = new Random();
        for (int i = 0; i < 6; i++)
            for (int j = 0; j < 6; j++)
                board[i][j] = (char) ('A' + rand.nextInt(26));
        return board;
    }

    public static char[][] getValidBoard(Set<String> dictionary) {
        char[][] newBoard;
        int attempts = 0;
        int maxAttempts = 100;
        do {
            newBoard = createRandomBoard();
            attempts++;
            if (attempts > maxAttempts) {
                System.out.println("Unable to generate a valid board after " + maxAttempts + " attempts. Please try a different category or restart.");
                return null;
            }
        } while (!boardContainsWordUnordered(newBoard, dictionary));
        addNewBoard.offer(newBoard); 
        return newBoard;
    }

    public static void displayBoard(char[][] board) {
        for (char[] row : board) {
            for (char c : row)
                System.out.print(c + " ");
            System.out.println();
        }
    }

    public static boolean boardContainsWordUnordered(char[][] board, Set<String> dictionary) {
        int[] boardLetterCounts = new int[26];
        for (char[] row : board)
            for (char c : row)
                if (c >= 'A' && c <= 'Z')
                    boardLetterCounts[c - 'A']++;

        for (String word : dictionary) {
            int[] wordLetterCounts = new int[26];
            for (char c : word.toUpperCase().toCharArray())
                if (c >= 'A' && c <= 'Z')
                    wordLetterCounts[c - 'A']++;
            boolean canForm = true;
            for (int i = 0; i < 26; i++)
                if (wordLetterCounts[i] > boardLetterCounts[i]) {
                    canForm = false;
                    break;
                }
            if (canForm) return true;
        }
        return false;
    }

    public static boolean isLetterInBoard(char[][] board, String letter) {
        if (letter.length() != 1) return false;
        char c = letter.charAt(0);
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j] == c) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void playCategory(int choice) {
        dictionary = getDictionaryForChoice(choice);
        board = getValidBoard(dictionary);
        if (board == null) return; 
        correctGuesses = 0;
        toGuess.clear();
        addNewBoard.clear();
        undoMove.clear();

        System.out.println();
        displayBoard(board);

        boolean playing = true;
        while (playing) {
            int menuChoice = getLetterMenuChoice();
            switch (menuChoice) {
                case 1: 
                    while (true) {
                        System.out.print("Enter a letter to add: ");
                        String letter = input.nextLine().trim().toUpperCase();
                        if (letter.length() == 1 && letter.charAt(0) >= 'A' && letter.charAt(0) <= 'Z') {
                            if (isLetterInBoard(board, letter)) {
                                toGuess.add(letter);
                                boolean replaced = false;
                                for (int i = 0; i < board.length && !replaced; i++) {
                                    for (int j = 0; j < board[i].length && !replaced; j++) {
                                        if (board[i][j] == letter.charAt(0)) {
                                            board[i][j] = '*';
                                            replaced = true;
                                        }
                                    }
                                }
                                System.out.printf("Letter %s successfully added!\n\n", letter);
                                System.out.println("Current word: " + toGuess);
                                System.out.println();
                                displayBoard(board);
                                break; 
                            } else {
                                System.out.println("That letter is not present on the board. Please choose another letter.");
                            }
                        } else {
                            System.out.println("Invalid input. Please enter a single letter (A-Z).");
                        }
                    }
                    break;
                case 2: 
                    if (!toGuess.isEmpty()) {
                        undoMove.push(new LinkedList<>(toGuess));
                        String removed = toGuess.removeLast();
                        System.out.println("Letter " + removed + " Removed from the word");
                        System.out.println();
                        System.out.println("Current word: " + toGuess);
                        System.out.println();

                        boolean restored = false;
                        for (int i = 0; i < board.length && !restored; i++) {
                            for (int j = 0; j < board[i].length && !restored; j++) {
                                if (board[i][j] == '*') {
                                    board[i][j] = removed.charAt(0);
                                    restored = true;
                                }
                            }
                        }
                        displayBoard(board);
                    } else {
                        System.out.printf("No letters left.\n\n");
                    }
                    break;
                case 3: 
                    StringBuilder sb = new StringBuilder();
                    for (String s : toGuess) sb.append(s);
                    String formedWord = sb.toString();
                    boolean isWord = dictionary.contains(formedWord);
                    if (isWord) {
                        System.out.println();
                        System.out.println("Success! " + formedWord + " is correct.");
                        System.out.println();

                        dictionary.remove(formedWord);
                        board = getValidBoard(dictionary);
                        if (board == null) {
                            playing = false;
                            break;
                        }
                        toGuess.clear();
                        correctGuesses++;

                        displayBoard(board);
                        if (correctGuesses == 5) {
                            System.out.println("Congratulations! You guessed 5 words and won!");
                            System.out.print("Do you want to play again? (Y/N): ");
                            String playAgain = input.nextLine().trim().toUpperCase();
                            if (playAgain.equals("Y")) {
                                dictionary = getDictionaryForChoice(choice);
                                board = getValidBoard(dictionary);
                                correctGuesses = 0;
                                toGuess.clear();
                                addNewBoard.clear();
                                undoMove.clear();
                                if (board != null) displayBoard(board);
                            } else {
                                playing = false;
                            }
                        }
                    } else {
                        System.out.println();
                        System.out.println("Sorry, '" + formedWord + "' is not in the dictionary.");
                        System.out.println();
                        displayBoard(board);
                    }
                    break;
                case 4: 
                    System.out.printf("You don't have enough coins to do that...\n");
                    System.out.println();
                    displayBoard(board);
                    break;
                case 5: 
                    System.out.printf("Choose a new category...\n\n");
                    playing = false;
                    break;
            }
        }
    }

    public static int getLetterMenuChoice() {
        String choice = "";
        int choice1 = 0;
        
        do {
            System.out.println();
            System.out.println("1. Add Letter, 2. Remove Letter, 3. Submit, 4. Reset Board (3 Coins), 5. Back to Category Select");
            System.out.println();
            System.out.print("Enter your choice: ");
            choice = input.nextLine();

            if (!isNumeric(choice)) {
                System.out.println("Please input numeric values only.\n");
                continue;
            }
            choice1 = Integer.parseInt(choice);
            if (choice1 < 1 || choice1 > 5) {
                System.out.println("Please choose between 1-5.\n");
                continue;
            }
            if (choice1 == 5) {
                System.out.println("Back to category select...");
                break;
            }
        } while (choice1 < 1 || choice1 > 5);
        return choice1;
    }

    public static boolean isNumeric(String choice) {
        try {
            Integer.parseInt(choice);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static void main(String[] args) {
        String choice;
        int choice1 = 0;

        System.out.printf("\n\nWELCOME TO BOOKWORM!\n");
        System.out.println("======================================================");
        System.out.printf("Here are some rules to play.\n");
        System.out.printf("\nThere are 4 categories of words to choose from.\nOnce chosen, a board of letters will be displayed.\nThen, you will have to spell out a word from that category.\nTo win, you must successfully guess 5 words\n\n");
        System.out.println("======================================================");

        do {
            menu();
            System.out.print("Enter your choice: ");
            choice = input.nextLine();
            if (!isNumeric(choice)) {
                System.out.println("Please input numeric values only.\n");
                continue;
            }
            choice1 = Integer.parseInt(choice);
            if (choice1 < 1 || choice1 > 5) {
                System.out.println("Please choose between 1-5.\n");
                continue;
            }
            if (choice1 == 5) {
                System.out.println("Game Exiting... Thank you for playing!");
                break;
            }
            playCategory(choice1);
        } while (choice1 != 5);
    }
}