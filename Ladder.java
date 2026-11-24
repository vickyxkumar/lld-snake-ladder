public class Ladder extends Obstacle {

    public Ladder(int bottom, int top) {
        super(bottom, top);
        if (top <= bottom) {
            throw new IllegalArgumentException("Ladder top must be above its bottom: " + bottom + " -> " + top);
        }
    }

    @Override
    public ObstacleType getType() {
        return ObstacleType.LADDER;
    }
}
