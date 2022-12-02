package mvp;

import java.util.Scanner;
import java.util.InputMismatchException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Arrays;

public class Game {
    private CodePeg[] code;
    public static final int NUM_CODEPEGS = 4, NUM_ROWS = 12, DIFFERENT_COLORS = 6, NUM_GUESSES = 12;

    public Game(CodePeg[] secretCode) {
        code = secretCode;
    }

    public Game(long seed) {
        Random random = new Random(seed);
        code = new CodePeg[NUM_CODEPEGS];
        for (int i = 0; i < code.length; i++) {
            code[i] = getCodePegValue(random.nextInt(DIFFERENT_COLORS));
        }
    }

    public Game() {
        this(new Random().nextLong());
    }

    public static CodePeg getCodePegValue(int value) {
        if (value >= DIFFERENT_COLORS || value < 0) {
            throw new IllegalArgumentException("CodePeg value is not valid");
        }
        return CodePeg.values()[value];
    }

    public static CodePeg getCodePegValue(String value) throws IllegalArgumentException {
        return CodePeg.valueOf(value);
    }

    public static void main(String[] args) {
        boolean isAIPlaying = true;
        Game game = new Game();
        if (isAIPlaying) {
            game.play();
        } else {
            Scanner scanner = new Scanner(System.in);
            game.play(scanner);
            scanner.close();
        }

    }

    public boolean play(Scanner scanner) {
        boolean hasWon = false;
        for (int i = 0; i < Game.NUM_GUESSES && !hasWon; i++) {
            Response[] response = makeGuess(code, getGuess(scanner));
            System.out.println("Response: " + Arrays.toString(response));
            hasWon = determineIfWon(response);
        }
        printGameResult(hasWon);
        return hasWon;
    }

    public boolean play() {
        MasterMindAI ai = new MasterMindAI();
        boolean hasWon = false;
        for (int i = 0; i < Game.NUM_GUESSES && !hasWon; i++) {
            CodePeg[] aiGuess = ai.makeGuess();
            System.out.println("AI Guess: " + Arrays.toString(aiGuess));
            Response[] response = makeGuess(code, aiGuess);
            System.out.println("Response: " + Arrays.toString(response));
            ai.updateState(response);
            hasWon = determineIfWon(response);
        }
        printGameResult(hasWon);
        return hasWon;
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

    public boolean determineIfWon(Response[] lastGuess) {
        boolean hasWon = true;
        for (int i = 0; i < lastGuess.length && hasWon; i++) {
            if (lastGuess[i] != Response.CORRECT_SPOT) {
                hasWon = false;
            }
        }
        return hasWon;
    }

    public static CodePeg[] getGuess(Scanner scanner) {
        CodePeg[] guess = new CodePeg[NUM_CODEPEGS];

        for (int i = 0; i < Game.NUM_CODEPEGS; i++) {
            System.out.printf("Input guess for codepeg #%d: ", i);
            boolean guessed = false;
            while (!guessed) {
                String val = scanner.next();
                try {
                    guess[i] = getCodePegValue(val);
                    guessed = true;
                } catch (IllegalArgumentException iae) {
                    try {
                        int pegNumber = Integer.parseInt(val);
                        guess[i] = getCodePegValue(pegNumber);
                        guessed = true;
                    } catch (NumberFormatException nfe) {
                        System.err.println("Invalid Input");
                    }
                }
            }

        }
        return guess;
    }

    private void printGameResult(boolean hasWon) {
        System.out.println("===========================================");
        if (hasWon) {
            System.out.println("Codebreaker has won");
        } else {
            System.out.println("Codemaker has won");
        }
        System.out.println("The code was: " + Arrays.toString(code));
        System.out.println("===========================================");
    }

    private class MasterMindAI {
        int guessCounter = 0;
        CodePeg[] pegs = CodePeg.values();
        LinkedList<CodePeg[]> possibleCodes;
        CodePeg[] lastGuess = null;

        private MasterMindAI() {
            possibleCodes = new LinkedList<>();
            for (int i = 0; i < Game.DIFFERENT_COLORS; i++) {
                for (int j = 0; j < Game.DIFFERENT_COLORS; j++) {
                    for (int k = 0; k < Game.DIFFERENT_COLORS; k++) {
                        for (int l = 0; l < Game.DIFFERENT_COLORS; l++) {
                            possibleCodes.push(new CodePeg[] { pegs[i], pegs[j], pegs[k], pegs[l] });
                        }
                    }
                }
            }
        }

        private CodePeg[] makeGuess() {
            CodePeg[] guess;
            if (guessCounter == 0) {
                guess = new CodePeg[] { pegs[0], pegs[0], pegs[1], pegs[1] };
            } else {
                guess = possibleCodes.pop();
            }
            guessCounter++;
            lastGuess = guess;
            return guess;
        }

        private void updateState(Response[] response) {
            pruneCodes(response);
        }

        private void pruneCodes(Response[] response) {
            if (lastGuess == null) {
                throw new IllegalStateException("No guess has been made yet");
            }
            for (Iterator<CodePeg[]> it = possibleCodes.iterator(); it.hasNext();) {
                CodePeg[] code = it.next();
                if (!Arrays.equals(Game.makeGuess(code, lastGuess), response)) {
                    it.remove();
                }
            }
        }

        /**
         * Uses minimax technique to get the best code
         * 
         * @return best possible code
         */
        private CodePeg[] getBestCode(Response[] response) {
            PriorityQueue queue = new PriorityQueue<>();
            for (CodePeg[] code : possibleCodes) {
                int codeScore = 0;
                for (CodePeg[] compareCode : possibleCodes) {
                    if (compareCode == code) {
                        continue;
                    }
                    if (Arrays.equals(Game.makeGuess(code, lastGuess), response)) {
                        codeScore += 1;
                    }
                }
            }
            return queue.peek();
        }
    }
}
