public class Snake extends Obstacle {

    public Snake(int head, int tail) {
        super(head, tail);
        if (head <= tail) {
            throw new IllegalArgumentException("Snake head must be above its tail: " + head + " -> " + tail);
        }
    }

    @Override
    public ObstacleType getType() {
        return ObstacleType.SNAKE;
    }
}
