import java.util.Collection;
import java.util.List;

/** Observer for game events, keeping all output out of the model classes. */
public interface GameEventListener {
    void onGameStart(Board board, Collection<Player> players);

    void onTurn(Player player, MoveResult result, Board board, Collection<Player> players);

    void onPlayerFinished(Player player, int rank);

    void onGameOver(List<Player> rankings);
}
