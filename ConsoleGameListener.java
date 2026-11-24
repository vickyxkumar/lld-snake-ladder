import java.util.Collection;
import java.util.List;

public class ConsoleGameListener implements GameEventListener {

    @Override
    public void onGameStart(Board board, Collection<Player> players) {
        System.out.println("\nStarting board (🐍n = snake down to n, 🪜n = ladder up to n):");
        System.out.println(BoardPrinter.render(board, players));
    }

    @Override
    public void onTurn(Player player, MoveResult r, Board board, Collection<Player> players) {
        System.out.println("-----------------------------------");
        System.out.println(player.getName() + " rolled " + r.roll());
        switch (r.type()) {
            case OVERSHOOT -> System.out.println(player.getName() + " needs an exact roll to finish; stays at " + r.from());
            case NORMAL -> System.out.println(player.getName() + " moved from " + r.from() + " to " + r.to());
            case SNAKE -> System.out.println("Oops! Snake at " + r.landedOn() + " has bitten "
                    + player.getName() + ", down to " + r.to());
            case LADDER -> System.out.println("Congratulations! " + player.getName() + " climbed a ladder from "
                    + r.landedOn() + " to " + r.to());
        }
        if (r.moved()) {
            System.out.println(BoardPrinter.render(board, players));
        }
    }

    @Override
    public void onPlayerFinished(Player player, int rank) {
        System.out.println(rank == 1
                ? "🏆 " + player.getName() + " has won the game!"
                : player.getName() + " finished in position " + rank);
    }

    @Override
    public void onGameOver(List<Player> rankings) {
        System.out.println("\nFinal rankings:");
        for (int i = 0; i < rankings.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + rankings.get(i).getName());
        }
    }
}
