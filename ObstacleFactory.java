public final class ObstacleFactory {
    private ObstacleFactory() {
    }

    /** @param start the cell a player lands on; @param end where the player ends up. */
    public static Obstacle create(ObstacleType type, int start, int end) {
        return switch (type) {
            case SNAKE -> new Snake(start, end);
            case LADDER -> new Ladder(start, end);
        };
    }
}
