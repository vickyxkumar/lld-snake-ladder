import java.util.Random;

public class RandomObstaclePlacement implements ObstaclePlacementStrategy {
    private static final int MAX_ATTEMPTS = 10_000;
    private final Random random;

    public RandomObstaclePlacement(Random random) {
        this.random = random;
    }

    @Override
    public void place(Board board, int snakes, int ladders) {
        int size = board.getSize();
        // Each obstacle uses two distinct cells and its start must lie in [2, size-1].
        if (snakes < 0 || ladders < 0 || snakes + ladders > (size - 2) / 2) {
            throw new IllegalArgumentException("Too many snakes/ladders for a board of size " + size);
        }
        placeAll(board, ObstacleType.SNAKE, snakes);
        placeAll(board, ObstacleType.LADDER, ladders);
    }

    private void placeAll(Board board, ObstacleType type, int count) {
        int size = board.getSize();
        for (int attempts = 0; count > 0; attempts++) {
            if (attempts == MAX_ATTEMPTS) {
                throw new IllegalStateException("Could not place all " + type + "s; reduce the count");
            }
            int start = random.nextInt(size - 2) + 2; // [2, size-1]
            int end = (type == ObstacleType.SNAKE)
                    ? random.nextInt(start - 1) + 1              // [1, start-1]
                    : start + 1 + random.nextInt(size - start);  // [start+1, size]
            if (board.addObstacle(ObstacleFactory.create(type, start, end))) {
                count--;
            }
        }
    }
}
