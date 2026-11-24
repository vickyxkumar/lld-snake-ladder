import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/** Pure game model: cells 1..size plus the obstacles on them. No I/O, no knowledge of players. */
public class Board {
    private final int size;
    private final Map<Integer, Obstacle> obstaclesByStart = new HashMap<>();
    // Every cell used as the start OR end of an obstacle, so obstacles never chain or overlap.
    private final Set<Integer> occupied = new HashSet<>();

    public Board(int size) {
        if (size < 4) {
            throw new IllegalArgumentException("Board size must be at least 4, got " + size);
        }
        this.size = size;
    }

    public int getSize() {
        return size;
    }

    /** @return false if the obstacle is off the board or touches a cell another obstacle already uses. */
    public boolean addObstacle(Obstacle obstacle) {
        int start = obstacle.getStart();
        int end = obstacle.getEnd();
        // Nothing may start on cell 1 (players never land there) or on the last cell (game would be unwinnable).
        if (start < 2 || start >= size || end > size
                || occupied.contains(start) || occupied.contains(end)) {
            return false;
        }
        obstaclesByStart.put(start, obstacle);
        occupied.add(start);
        occupied.add(end);
        return true;
    }

    public Obstacle getObstacleAt(int position) {
        return obstaclesByStart.get(position);
    }

    public Map<Integer, Obstacle> getObstacles() {
        return Collections.unmodifiableMap(obstaclesByStart);
    }

    public MoveResult computeMove(int from, int roll) {
        int landedOn = from + roll;
        if (landedOn > size) {
            return new MoveResult(from, roll, landedOn, from, MoveResult.MoveType.OVERSHOOT);
        }
        Obstacle obstacle = obstaclesByStart.get(landedOn);
        if (obstacle == null) {
            return new MoveResult(from, roll, landedOn, landedOn, MoveResult.MoveType.NORMAL);
        }
        MoveResult.MoveType type = obstacle.getType() == ObstacleType.SNAKE
                ? MoveResult.MoveType.SNAKE
                : MoveResult.MoveType.LADDER;
        return new MoveResult(from, roll, landedOn, obstacle.getEnd(), type);
    }
}
