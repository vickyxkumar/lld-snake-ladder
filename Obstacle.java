/** Moves a player who lands on {@code start} to {@code end}. */
public abstract class Obstacle {
    private final int start;
    private final int end;

    protected Obstacle(int start, int end) {
        if (start < 1 || end < 1 || start == end) {
            throw new IllegalArgumentException("Invalid obstacle " + start + " -> " + end);
        }
        this.start = start;
        this.end = end;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public abstract ObstacleType getType();
}
