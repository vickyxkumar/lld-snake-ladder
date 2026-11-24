import java.util.Collection;
import java.util.StringJoiner;

/** Renders a board as a zig-zag grid (cell 1 bottom-left). Works for any board size. */
public final class BoardPrinter {
    private static final String EMPTY = "";

    private BoardPrinter() {
    }

    public static String render(Board board, Collection<Player> players) {
        int size = board.getSize();
        int width = (int) Math.ceil(Math.sqrt(size));
        int rows = (size + width - 1) / width;
        StringBuilder out = new StringBuilder();

        for (int rowFromBottom = rows - 1; rowFromBottom >= 0; rowFromBottom--) {
            for (int col = 0; col < width; col++) {
                int offset = (rowFromBottom % 2 == 0) ? col : width - 1 - col;
                int position = rowFromBottom * width + offset + 1;
                String content = position <= size ? cellContent(board, players, position) : EMPTY;
                out.append(String.format("%-8s ", content)); // trailing space keeps long names from merging
            }
            out.append('\n');
        }

        StringJoiner waiting = new StringJoiner(", ");
        for (Player player : players) {
            if (player.getPosition() == 0) {
                waiting.add(player.getName());
            }
        }
        if (waiting.length() > 0) {
            out.append("Not on board yet: ").append(waiting).append('\n');
        }
        return out.toString();
    }

    private static String cellContent(Board board, Collection<Player> players, int position) {
        StringJoiner here = new StringJoiner(",", "[", "]");
        boolean anyone = false;
        for (Player player : players) {
            if (player.getPosition() == position) {
                here.add(player.getName());
                anyone = true;
            }
        }
        if (anyone) {
            return here.toString();
        }
        Obstacle obstacle = board.getObstacleAt(position);
        return obstacle == null
                ? String.valueOf(position)
                : obstacle.getType().getSymbol() + obstacle.getEnd();
    }
}
