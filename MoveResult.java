/** Outcome of one dice roll; the board computes it, listeners decide how to show it. */
public record MoveResult(int from, int roll, int landedOn, int to, MoveType type) {

    public enum MoveType {
        NORMAL,
        SNAKE,
        LADDER,
        OVERSHOOT
    }

    public boolean moved() {
        return type != MoveType.OVERSHOOT;
    }
}
