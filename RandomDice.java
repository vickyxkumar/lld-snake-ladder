import java.util.Random;

public class RandomDice implements Dice {
    private final int count;
    private final int faces;
    private final Random random;

    public RandomDice(int count, int faces, Random random) {
        if (count < 1 || faces < 2) {
            throw new IllegalArgumentException("Need at least 1 die with at least 2 faces");
        }
        this.count = count;
        this.faces = faces;
        this.random = random;
    }

    @Override
    public int roll() {
        int total = 0;
        for (int i = 0; i < count; i++) {
            total += random.nextInt(faces) + 1;
        }
        return total;
    }
}
