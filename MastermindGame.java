import java.util.Random;
import java.util.Scanner;

public class MastermindGame {
    public static final int NUM_CODEPEGS = 4, NUM_ROWS = 12, DIFFERENT_COLORS = 6, NUM_GUESSES = 12;

    private Round[] rounds;
    private PlayerData player1;
    private PlayerData player2;
    private Random random;
    private int currentRound = -1;

    // public static void main(String[] args) {
    // System.out.println("The game has started! Guess the combination of pegs to
    // beat the code maker!");

    // CodePeg[] secretCode = new CodePeg[] { CodePeg.PURPLE, CodePeg.PURPLE,
    // CodePeg.PURPLE, CodePeg.BLACK };
    // MastermindGame game = new MastermindGame(secretCode);
    // Scanner scanner = new Scanner(System.in);
    // while (!game.isGameOver()) {
    // CodePeg[] guess = new CodePeg[MastermindGame.NUM_CODEPEGS];
    // for (int i = 0; i < guess.length; i++) {
    // while (guess[i] == null) {
    // try {
    // guess[i] = CodePeg.valueOf(scanner.nextLine());
    // } catch (IllegalArgumentException iae) {
    // System.out.println("wrong color");
    // }
    // }
    // }
    // System.out.println(game.makeGuess(guess));
    // }
    // game.printWinner();
    // scanner.close();
    // }

    public MastermindGame(Player player1, Player player2) {
        this.random = new Random();
    }

    public MastermindGame(Player player1, Player player2, long seed) {
        this.random = new Random(seed);

    }

    public void setUpGame(CodePeg[] secretCode, int roundsPlayed) {
        if (secretCode.length != NUM_CODEPEGS) {
            throw new IllegalArgumentException("Secret code is the wrong length");
        }
        player1.score = 0;
        player2.score = 0;
    }

    private void printWinner() {
        if (player1.score > player2.score) {
            System.out.printf("Player 1 has one the game with a score of %d\n", player1.score);
        } else if (player1.score < player2.score) {
            System.out.printf("Player 2 has one the game with a score of %d\n", player2.score);
        } else {
            System.out.println("Both players have tied! Nobody wins!");
        }
    }

    public void play() {
        if (rounds == null) {
            throw new IllegalStateException("The game needs to be set up first.");
        }
        printWinner();
    }

    private void printResult(CodePeg[] result) {
        StringBuilder message = new StringBuilder("Result: ");
        for (int i = 0; i < result.length; i++) {
            message.append(result[i].toString());
            message.append(", ");
        }
        System.out.println(message.toString());
    }

    private CodePeg getRandomColoredPeg() {
        CodePeg[] choices = new CodePeg[] {
                CodePeg.YELLOW,
                CodePeg.BLUE,
                CodePeg.ORANGE,
                CodePeg.PURPLE,
                CodePeg.GREEN,
                CodePeg.BLACK,
                CodePeg.RED
        };
        int randInt = random.nextInt(0, choices.length);

        return choices[randInt];
    }

    private <T> void shuffleArray(T[] array) {
        for (int i = 0; i < array.length; i++) {
            int randIndex = random.nextInt(i, array.length);
            if (i != randIndex) {
                T temp = array[randIndex];
                array[randIndex] = array[i];
                array[i] = temp;
            }
        }
    }

    private class PlayerData {
        int score;
        Player player;
        int roundsWon;
        int guessesRemaining;

        private PlayerData(Player player) {
            this.player = player;
            score = 0;
            roundsWon = 0;
            guessesRemaining = NUM_GUESSES;
        }
    }

    private class Round {
        private CodePeg[][] rows = new CodePeg[NUM_ROWS][NUM_CODEPEGS];
        private PlayerData guesser;
        private PlayerData codeMaker;
        private boolean isGuessedCorrectly = false;
        private boolean isOutOfGuesses = false;
        private CodePeg[] secretCode;

        private Round(PlayerData guesser, PlayerData codeMaker) {
            this.guesser = guesser;
            this.codeMaker = codeMaker;
        }

        private String getRoundWinner() {
            String winner = null;
            if (isOutOfGuesses) {
                winner = codeMaker.toString();
            } else {
                winner = guesser.toString();
            }
            return String.format("%s has won the round.", winner);
        }

        private CodePeg[] makeGuess() {
            CodePeg[] playerGuess = guesser.player.makeGuess(rows);
            rows[NUM_GUESSES - guesser.guessesRemaining] = playerGuess;
            if (guesser.guessesRemaining < 0) {
                isOutOfGuesses = true;
            }
            codeMaker.score += 1;
            return generateResponseCode(playerGuess);
        }

        private void playRound() {
            while (!(isGuessedCorrectly || isOutOfGuesses)) {
                CodePeg[] response = makeGuess();
                if (guesser.player.showBoard) {
                    printResult(response);
                }
            }
            if (isOutOfGuesses) {
                codeMaker.score += 1;
            }
        }

        private CodePeg[] generateResponseCode(CodePeg[] guess) {
            CodePeg[] response = new CodePeg[NUM_CODEPEGS];
            for (int i = 0; i < guess.length; i++) {
                boolean contains = false;
                int index = -1;
                for (int j = 0; j < secretCode.length && !contains; j++) {
                    contains = true;
                    index = j;
                }
                if (contains) {
                    if (index == i) {
                        response[i] = CodePeg.WHITE;
                    } else {
                        response[i] = getRandomColoredPeg();
                    }
                }
            }
            shuffleArray(response);
            return response;
        }
    }
}
