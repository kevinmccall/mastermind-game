package mvp;

import java.util.Scanner;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;
import java.util.Arrays;

public class Game {

    // Codes are represented using an array of CodePeg Enums size NUM_GUESSES
    private CodePeg[] code;
    // Different colors are the number of valid CodePeg colors
    public static final int NUM_CODEPEGS = 4, DIFFERENT_COLORS = 6, NUM_GUESSES = 12;

    public static void main(String[] args) {
        boolean isAIPlaying = false;
        Game game = new Game();
        if (isAIPlaying) {
            game.play();
        } else {
            Scanner scanner = new Scanner(System.in);
            game.play(scanner);
            scanner.close();
        }

    }

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

    public boolean play(Scanner scanner) {
        boolean hasWon = false;
        int numGuesses;
        for (numGuesses = 0; numGuesses < Game.NUM_GUESSES && !hasWon; numGuesses++) {
            Response[] response = makeGuess(code, getGuess(scanner));
            System.out.println("Response: " + Arrays.toString(response));
            hasWon = determineIfWon(response);
        }
        printGameResult(hasWon, numGuesses);
        return hasWon;
    }

    public boolean play() {
        MasterMindAI ai = new MasterMindAI();
        boolean hasWon = false;
        int numGuesses;
        for (numGuesses = 0; numGuesses < Game.NUM_GUESSES && !hasWon; numGuesses++) {
            CodePeg[] aiGuess = ai.makeGuess();
            System.out.println("AI Guess: " + Arrays.toString(aiGuess));
            Response[] response = makeGuess(code, aiGuess);
            System.out.println("Response: " + Arrays.toString(response));
            ai.updateState(response);
            hasWon = determineIfWon(response);
        }
        printGameResult(hasWon, numGuesses);
        assert numGuesses <= 5;
        return hasWon;
    }

    public static Response[] makeGuess(CodePeg[] correctCode, CodePeg[] guess) {
        if (correctCode == null) {
            throw new IllegalArgumentException("Base code can not be null.");
        } else if (guess == null) {
            throw new IllegalArgumentException("Guess code can not be null.");
        }
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

    private void printGameResult(boolean hasWon, int numGuesses) {
        System.out.println("===========================================");
        if (hasWon) {
            System.out.printf("Codebreaker has won in %d guesses!\n", numGuesses);
        } else {
            System.out.println("Codemaker has made an unbeatable code and won!");
        }
        System.out.println("The code was: " + Arrays.toString(code));
        System.out.println("===========================================");
    }

    private class MasterMindAI {
        int guessCounter = 0;
        CodePeg[] pegs = CodePeg.values();
        LinkedList<CodePeg[]> possibleCodes;
        CodePeg[] lastGuess = null;
        CodePeg[] nextGuess = null;

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
                guess = nextGuess;
            }
            guessCounter++;
            lastGuess = guess;
            return guess;
        }

        private void updateState(Response[] response) {
            pruneCodes(response);
            setNextCode(response);
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
         * Uses minimax technique to set the best code
         */
        private void setNextCode(Response[] response) {
            int maxScore = Integer.MAX_VALUE;
            CodePeg[] bestCode = null;

            for (CodePeg[] code : possibleCodes) {
                int codeScore = 0;
                for (CodePeg[] compareCode : possibleCodes) {
                    if (compareCode == code) {
                        continue;
                    }
                    if (Arrays.equals(Game.makeGuess(code, compareCode), response)) {
                        codeScore += 1;
                    }
                }
                if (codeScore < maxScore) {
                    maxScore = codeScore;
                    bestCode = code;
                }
            }
            nextGuess = bestCode;
        }
    }
}
