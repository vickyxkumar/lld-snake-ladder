public enum ObstacleType {
    SNAKE("🐍"),
    LADDER("🪜");

    private final String symbol;

    ObstacleType(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }
}
