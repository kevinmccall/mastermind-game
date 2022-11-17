package mvp;

import java.util.Scanner;
import java.util.InputMismatchException;
import java.util.Random;

public class Game {
    private CodePeg[] code;
    public boolean hasWon = false;
    public static final int NUM_CODEPEGS = 4, NUM_ROWS = 12, DIFFERENT_COLORS = 6, NUM_GUESSES = 12;

    public Game(CodePeg[] secretCode) {
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
            Response[] response = makeGuess(code, getGuess(scanner));
            Game.printArray(response);
            determineIfWon(response);
        }
        if (hasWon) {
            System.out.println("Codebreaker has won");
        } else {
            System.out.println("Codemaker has won");
        }
    }

    public static Response[] makeGuess(CodePeg[] correctCode, CodePeg[] guess) {
        int responseIndex = 0;
        int difIndex = 0;
        CodePeg[] potentialPegs = new CodePeg[NUM_CODEPEGS];
        Response[] response = new Response[NUM_CODEPEGS];

        for (int i = 0; i < correctCode.length; i++) {
            if (guess[i] != correctCode[i]) {
                potentialPegs[difIndex] = correctCode[i];
                difIndex++;
            }
        }

        for (int i = 0; i < guess.length; i++) {
            if (correctCode[i] == guess[i]) {
                response[responseIndex] = Response.CORRECT_SPOT;
                responseIndex += 1;
            } else {
                boolean inWordDifferentPos = false;
                for (int j = 0; j < difIndex && !inWordDifferentPos; j++) {
                    if (potentialPegs[j] == guess[i]) {
                        response[responseIndex] = Response.DIFFERENT_SPOT;
                        responseIndex++;
                        potentialPegs[j] = CodePeg.INVALID;
                        inWordDifferentPos = true;
                    }
                }
                if (!inWordDifferentPos) {
                    response[responseIndex] = Response.INCORRECT;
                    responseIndex++;
                }
            }
        }
        return response;
    }

    public void determineIfWon(Response[] lastGuess) {
        hasWon = true;
        for (int i = 0; i < lastGuess.length && hasWon; i++) {
            if (lastGuess[i] != Response.CORRECT_SPOT) {
                hasWon = false;
            }
        }
    }

    public static CodePeg[] getGuess(Scanner scanner) {
        CodePeg[] guess = new CodePeg[NUM_CODEPEGS];

        for (int i = 0; i < Game.NUM_CODEPEGS; i++) {
            System.out.printf("Input guess for codepeg #%d: ", i);
            try {
                guess[i] = CodePeg.values()[scanner.nextInt()];
            } catch (InputMismatchException ime) {
                guess[i] = CodePeg.valueOf(scanner.next());
            }
        }
        return guess;
    }

    public static void printArray(Response[] arr) {
        for (int i = 0; i < arr.length; i++) {
            System.out.println(arr[i]);
        }
    }

    private class MasterMindAI {
        int guessCounter = 0;
        CodePeg[] pegs = CodePeg.values();
        CodePeg[][] possibleCodes;
        CodePeg[] lastGuess = null;

        private MasterMindAI() {
            int validCodePegCount = pegs.length - 1; // Don't include the invalid peg
            possibleCodes = new CodePeg[(int) Math.pow(validCodePegCount, 4)][NUM_CODEPEGS];
            for (int i = 0; i < validCodePegCount; i++) {
                for (int j = 0; j < validCodePegCount; j++) {
                    for (int k = 0; k < validCodePegCount; k++) {
                        for (int l = 0; l < validCodePegCount; l++) {
                            possibleCodes[i * i * i * i + j * j * j + k * k + l] = new CodePeg[] { pegs[i], pegs[j],
                                    pegs[k], pegs[l] };
                        }
                    }
                }
            }
        }

        private CodePeg[] makeGuess(Response[] response) {
            CodePeg[] guess;
            if (guessCounter == 0) {
                guess = new CodePeg[] { pegs[0], pegs[0], pegs[1], pegs[1] };
            } else {
                for (int i = 0; i < possibleCodes.length; i++) {
                    if (possibleCodes[i] != null) {
                        if (Game.makeGuess(lastGuess, possibleCodes[i]) != response) {
                            System.out.println("my suspicions proved true");
                            possibleCodes[i] = null;
                        }
                    }
                }
                for (int i = 0)
            }
            guessCounter++;
            lastGuess = guess;
            return guess;
        }
    }
}
