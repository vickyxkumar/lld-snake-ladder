public class Player {
    private final String name;
    private int position = 0; // 0 = not yet on the board; a roll of 1 lands on cell 1

    public Player(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Player name must not be blank");
        }
        this.name = name.strip();
    }

    public String getName() {
        return name;
    }

    public int getPosition() {
        return position;
    }

    void setPosition(int position) {
        this.position = position;
    }
}
