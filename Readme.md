# Snake and Ladder (Low-Level Design)

A Java console implementation of Snake and Ladder for any number of players. The board size, number of snakes,
number of ladders and number of dice are all configurable. The project is a low-level design exercise. It keeps the
game rules in plain model classes with no I/O. Dice, obstacle placement and output are pluggable, so every part can
be swapped or tested on its own.

## Features

- **Any board size of 4 or more.** Perfect squares (25, 100) draw as a full grid. Other sizes (50) draw with a
  shorter top row.
- **Random but always-valid boards:**
  - No snake or ladder starts on cell 1 or on the last cell, so every game can be won.
  - No two obstacles share a cell, so a ladder never drops you straight onto a snake's head.
- **Exact finish.** A roll that would go past the last cell leaves the player where they are.
- **Full rankings.** Players who reach the last cell leave the turn queue. Play continues until one player is left,
  who takes last place.
- **Multiple dice.** The rolls are summed, and the number of faces is configurable.
- **Validated input.** Invalid numbers are asked for again, and player names may contain spaces.

## Project Structure

```
.
├── Main.java                         # Composition root: reads input, wires objects, starts the game
├── Game.java                         # Turn order, rules, status and rankings
├── GameStatus.java                   # NOT_STARTED, IN_PROGRESS, FINISHED
├── Player.java                       # Name + position (0 = not on the board yet)
├── Board.java                        # Size + map of cell → obstacle; computes moves. No I/O
├── MoveResult.java                   # Outcome of one roll: from, roll, landedOn, to, type
├── Obstacle.java                     # Abstract base: start → end (validated)
├── Snake.java                        # Snake(head, tail), head > tail
├── Ladder.java                       # Ladder(bottom, top), top > bottom
├── ObstacleType.java                 # SNAKE / LADDER, each with its display symbol
├── ObstacleFactory.java              # Creates a Snake or Ladder from a type
├── ObstaclePlacementStrategy.java    # Interface: put N snakes and M ladders on a board
├── RandomObstaclePlacement.java      # Random placement that respects the board's rules
├── Dice.java                         # Interface: int roll()
├── RandomDice.java                   # N dice with F faces, using an injected Random
├── GameEventListener.java            # Observer: game start, each turn, player finished, game over
├── ConsoleGameListener.java          # Prints events and the board to the console
└── BoardPrinter.java                 # Draws the zig-zag board as text
```

## Design

```
                 ┌──────────────┐
   Main ───────▶ │     Game     │ ── events ──▶ GameEventListener ◀── ConsoleGameListener ──▶ BoardPrinter
 (wires it all)  └──────┬───────┘
                        │ uses
          ┌─────────────┼──────────────┐
          ▼             ▼              ▼
       Board          Dice           Player
          │       (RandomDice)
          │ holds
          ▼
   Obstacle (abstract) ◀── ObstacleFactory ◀── ObstaclePlacementStrategy
     ├── Snake                                  (RandomObstaclePlacement)
     └── Ladder
```

A turn runs like this:

1. `Game` takes the next player from the queue and rolls the `Dice`.
2. `Board.computeMove(from, roll)` returns a `MoveResult` of type `NORMAL`, `SNAKE`, `LADDER` or `OVERSHOOT`.
   The board only computes the move; it does not change the player or print anything.
3. `Game` updates the player's position and passes the result to the listener.
4. If the player reached the last cell, they are ranked. Otherwise they go to the back of the queue.

Design patterns used:

| Pattern | Where | Why |
|---------|-------|-----|
| Factory | `ObstacleFactory.create(type, start, end)` | Callers ask for an obstacle by type and never construct it themselves. |
| Strategy | `Dice`, `ObstaclePlacementStrategy` | How dice roll and how obstacles are placed can change without touching `Game` or `Board`. |
| Observer | `GameEventListener` | The model never prints. A console, GUI or test listener can all watch the same game. |
| Dependency injection | `Game` constructor, `Main` | `Game` receives its board, dice, players and listener, so tests can pass fixed versions. |

### Board rules

`Board.addObstacle` returns `false` and adds nothing when an obstacle:

- starts on cell 1 or on the last cell,
- ends beyond the last cell, or
- uses a cell (start or end) that another obstacle already uses.

`RandomObstaclePlacement` also allows at most `(size − 2) / 2` obstacles in total, because each one needs two free
cells. It gives up with an error after 10,000 attempts rather than looping forever.

### Board display

Cell 1 is at the bottom left, and the numbering zig-zags upward. In each cell:

| Shown | Meaning |
|-------|---------|
| `17` | An empty cell |
| `🐍4` | A snake's head; landing here takes you down to 4 |
| `🪜21` | A ladder's foot; landing here takes you up to 21 |
| `[Ana,Cy]` | The players standing on this cell |

Players who have not rolled onto the board yet are listed under the grid.

## Getting Started

### Prerequisites

- JDK 21 (the code uses records and switch expressions, so JDK 17 or newer is required)

### Build and run

The project has no build tool, so compile it with `javac` from the project root:

```bash
javac -d out *.java
java -cp out Main
```

### Example session

```
Enter the board size : 25
Enter the number of snakes (max 11) : 3
Enter the number of ladders (max 8) : 3
Enter the number of players : 3
Enter the number of dice : 1
Enter the name of player 1 : Ana
Enter the name of player 2 : Bob Lee
Enter the name of player 3 : Cy

Starting board (🐍n = snake down to n, 🪜n = ladder up to n):
21       22       23       24       25
🪜22     19       18       🪜24     16
11       12       🪜21     14       🐍10
10       🐍1      🐍4      7        6
1        2        3        4        5
Not on board yet: Ana, Bob Lee, Cy

-----------------------------------
Ana rolled 5
Ana moved from 0 to 5
...
🏆 Cy has won the game!
Ana finished in position 2
Bob Lee finished in position 3

Final rankings:
  1. Cy
  2. Ana
  3. Bob Lee
```

The board and rolls are random, so your output will differ.

## Usage

`Main` builds a game from console input. To build one in code:

```java
Random random = new Random(42);                       // fixed seed → same board and rolls every run
Board board = new Board(100);
new RandomObstaclePlacement(random).place(board, 8, 8);

Game game = new Game(
        board,
        new RandomDice(1, 6, random),
        List.of(new Player("Ana"), new Player("Bob")),
        new ConsoleGameListener());
game.play();                                          // or call game.playTurn() one turn at a time
```

To set up a fixed board instead of a random one:

```java
Board board = new Board(25);
board.addObstacle(new Snake(14, 4));                  // head 14 → tail 4
board.addObstacle(new Ladder(3, 20));                 // bottom 3 → top 20
```

### Testing

Because `Dice` and `GameEventListener` are interfaces, a test can script every roll and silence the output:

```java
class FixedDice implements Dice {
    private final Deque<Integer> rolls;
    FixedDice(Integer... rolls) { this.rolls = new ArrayDeque<>(List.of(rolls)); }
    public int roll() { return rolls.poll(); }
}

Game game = new Game(board, new FixedDice(3, 1, 2, 5), players, silentListener);
MoveResult first = game.playTurn();                   // Ana: 0 → 3, ladder → 20
assert first.type() == MoveResult.MoveType.LADDER;
```

After each `playTurn()`, check `MoveResult`, `game.getStatus()` and `game.getRankings()`.

## Extending

- **New obstacle type** (for example, a portal):
  1. Add a value with its symbol to `ObstacleType`.
  2. Subclass `Obstacle`.
  3. Add a `case` in `ObstacleFactory`. The compiler will flag every `switch` that needs updating.
- **Different dice** (loaded, or a single die with 12 faces): implement `Dice`.
- **Hand-designed boards:** implement `ObstaclePlacementStrategy`, for example to read positions from a file.
- **A different UI:** implement `GameEventListener`. `BoardPrinter.render` returns a `String`, so it can be reused.
- **Extra rules** (an extra turn on a 6, losing the turn after three 6s): change `Game.playTurn()`. `Board` stays
  unchanged.

## Known Limitations

- **Only a console UI.** Moves happen without pauses, so a long game scrolls quickly.
- **No undo or save.** All game state lives in memory for one run.
- **Cells are 8 characters wide.** Several long names on one cell will push the rest of that row out of line.
