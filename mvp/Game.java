package mvp;

import java.util.Scanner;
import java.util.Random;

public class Game {
    private int[] code = new int[] { 0, 1, 2, 3 };
    public boolean hasWon = false;
    public static final int NUM_CODEPEGS = 4, NUM_ROWS = 12, DIFFERENT_COLORS = 6, NUM_GUESSES = 12;

    public Game(int[] secretCode) {
        code = secretCode;
    }

    public Game(long seed) {
        Random random = new Random(seed);
        int[] code = new int[NUM_CODEPEGS];
        for (int i = 0; i < code.length; i++) {
            code[i] = random.nextInt(DIFFERENT_COLORS);
        }
    }

    public Game() {
        this(new Random().nextLong());
    }

    public static void main(String[] args) {
        Game game = new Game();
        Scanner scanner = new Scanner(System.in);

        game.play(scanner);

    }

    public void play(Scanner scanner) {
        for (int i = 0; i < Game.NUM_GUESSES && !hasWon; i++) {
            Game.printArray((makeGuess(getGuess(scanner))));
        }
        if (hasWon) {
            System.out.println("Codebreaker has won");
        } else {
            System.out.println("Codemaker has won");
        }
    }

    public Response[] makeGuess(int[] guess) {
        int responseIndex = 0;
        int difIndex = 0;
        int[] potentialPegs = new int[NUM_CODEPEGS];
        Response[] response = new Response[NUM_CODEPEGS];

        for (int i = 0; i < code.length; i++) {
            if (guess[i] != code[i]) {
                potentialPegs[difIndex] = code[i];
                difIndex++;
            }
        }

        hasWon = true;
        for (int i = 0; i < guess.length; i++) {
            if (code[i] == guess[i]) {
                response[responseIndex] = Response.CORRECT_SPOT;
                responseIndex += 1;
            } else {
                boolean inWordDifferentPos = false;
                for (int j = 0; j < difIndex && !inWordDifferentPos; j++) {
                    if (potentialPegs[j] == guess[i]) {
                        response[responseIndex] = Response.DIFFERENT_SPOT;
                        responseIndex++;
                        potentialPegs[j] = -1;
                        inWordDifferentPos = true;
                    }
                }
                if (!inWordDifferentPos) {
                    response[responseIndex] = Response.INCORRECT;
                    responseIndex++;
                }
                hasWon = false;
            }
        }
        return response;
    }

    public static int[] getGuess(Scanner scanner) {
        int[] guess = new int[4];
        for (int i = 0; i < Game.NUM_CODEPEGS; i++) {
            System.out.printf("Input guess for codepeg #%d: ", i);
            guess[i] = scanner.nextInt();
        }
        return guess;
    }

    public static void printArray(Response[] arr) {
        for (int i = 0; i < arr.length; i++) {
            System.out.println(arr[i]);
        }
    }
}
