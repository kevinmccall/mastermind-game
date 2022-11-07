package mvp;

import java.util.Scanner;

public class Game {
    private int[] code = new int[] { 0, 1, 2, 3 };
    public boolean hasWon = false;
    public static final int NUM_CODEPEGS = 4, NUM_ROWS = 12, DIFFERENT_COLORS = 6, NUM_GUESSES = 12;

    public static void main(String[] args) {
        Game game = new Game();
        Scanner scanner = new Scanner(System.in);
        for (int i = 0; i < Game.NUM_GUESSES && !game.hasWon; i++) {
            Game.printArray((game.makeGuess(getGuess(scanner))));
        }
        if (game.hasWon) {
            System.out.println("Codebreaker has won");
        } else {
            System.out.println("Codemaker has won");
        }
    }

    public int[] makeGuess(int[] guess) {
        int[] codeCopy = new int[NUM_CODEPEGS];
        int rIndex = 0;
        int[] r = new int[NUM_CODEPEGS];

        for (int i = 0; i < code.length; i++) {
            codeCopy[i] = code[i];
        }

        hasWon = true;
        for (int i = 0; i < codeCopy.length; i++) {
            if (guess[i] == codeCopy[i]) {
                guess[i] = -1;
                codeCopy[i] = -1;
                r[rIndex] = 2;
                rIndex += 1;
            } else {
                hasWon = false;
            }
        }
        for (int i = 0; i < guess.length && !hasWon; i++) {
            if (codeCopy[i] != -1) {
                for (int j = 0; j < codeCopy.length; j++) {
                    if (codeCopy[j] == guess[i]) {
                        r[rIndex] = 1;
                        codeCopy[j] = -1;
                    }
                }
            }
        }
        return r;
    }

    public static int[] getGuess(Scanner scanner) {
        int[] guess = new int[4];
        for (int i = 0; i < Game.NUM_CODEPEGS; i++) {
            System.out.printf("Input guess for codepeg #%d: ", i);
            guess[i] = scanner.nextInt();
        }
        return guess;
    }

    public static void printArray(int[] arr) {
        for (int i = 0; i < arr.length; i++) {
            System.out.println(arr[i]);
        }
    }
}
