import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/** Composition root: reads config, wires the objects together, starts the game. */
public class Main {
    private static final int DICE_FACES = 6;

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            int boardSize = readInt(scanner, "Enter the board size : ", 4, 10_000);
            int maxObstacles = (boardSize - 2) / 2;
            int noOfSnakes = readInt(scanner, "Enter the number of snakes (max " + maxObstacles + ") : ",
                    0, maxObstacles);
            int noOfLadders = readInt(scanner, "Enter the number of ladders (max " + (maxObstacles - noOfSnakes) + ") : ",
                    0, maxObstacles - noOfSnakes);
            int noOfPlayers = readInt(scanner, "Enter the number of players : ", 2, 20);
            int noOfDice = readInt(scanner, "Enter the number of dice : ", 1, 5);

            List<Player> players = new ArrayList<>();
            for (int i = 0; i < noOfPlayers; i++) {
                players.add(new Player(readLine(scanner, "Enter the name of player " + (i + 1) + " : ")));
            }

            Random random = new Random();
            Board board = new Board(boardSize);
            new RandomObstaclePlacement(random).place(board, noOfSnakes, noOfLadders);

            Game game = new Game(board, new RandomDice(noOfDice, DICE_FACES, random), players,
                    new ConsoleGameListener());
            game.play();
        }
    }

    private static int readInt(Scanner scanner, String prompt, int min, int max) {
        while (true) {
            String line = readLine(scanner, prompt);
            try {
                int value = Integer.parseInt(line);
                if (value >= min && value <= max) {
                    return value;
                }
            } catch (NumberFormatException ignored) {
                // fall through to the error message
            }
            System.out.println("Please enter a whole number between " + min + " and " + max + ".");
        }
    }

    private static String readLine(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            if (!scanner.hasNextLine()) {
                throw new IllegalStateException("Input ended unexpectedly");
            }
            String line = scanner.nextLine().strip();
            if (!line.isEmpty()) {
                return line;
            }
        }
    }
}
