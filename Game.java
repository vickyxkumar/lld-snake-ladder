import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Queue;

/**
 * Turn order and rules. Players who reach the last cell leave the queue and are ranked;
 * the game ends when only one player is left, who takes the last rank.
 */
public class Game {
    private final Board board;
    private final Dice dice;
    private final GameEventListener listener;
    private final Queue<Player> activePlayers;
    private final List<Player> allPlayers;
    private final List<Player> rankings = new ArrayList<>();
    private GameStatus status = GameStatus.NOT_STARTED;

    public Game(Board board, Dice dice, List<Player> players, GameEventListener listener) {
        if (players.size() < 2) {
            throw new IllegalArgumentException("Need at least 2 players");
        }
        this.board = board;
        this.dice = dice;
        this.listener = listener;
        this.allPlayers = List.copyOf(players);
        this.activePlayers = new ArrayDeque<>(players);
    }

    public void play() {
        while (status != GameStatus.FINISHED) {
            playTurn();
        }
    }

    /** Plays exactly one turn for the player whose turn it is. */
    public MoveResult playTurn() {
        if (status == GameStatus.FINISHED) {
            throw new IllegalStateException("Game is already over");
        }
        if (status == GameStatus.NOT_STARTED) {
            status = GameStatus.IN_PROGRESS;
            listener.onGameStart(board, allPlayers);
        }

        Player player = activePlayers.poll();
        MoveResult result = board.computeMove(player.getPosition(), dice.roll());
        player.setPosition(result.to());
        listener.onTurn(player, result, board, allPlayers);

        if (result.to() == board.getSize()) {
            finish(player);
        } else {
            activePlayers.offer(player);
        }

        if (activePlayers.size() == 1) {
            finish(activePlayers.poll());
            status = GameStatus.FINISHED;
            listener.onGameOver(getRankings());
        }
        return result;
    }

    private void finish(Player player) {
        rankings.add(player);
        listener.onPlayerFinished(player, rankings.size());
    }

    public GameStatus getStatus() {
        return status;
    }

    public List<Player> getRankings() {
        return Collections.unmodifiableList(rankings);
    }

    public Collection<Player> getPlayers() {
        return allPlayers;
    }
}
