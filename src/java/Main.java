package game;
import java.util.*;

public class Main {

    private final int mapSize;
    private String userPosition;
    private final String keyPosition;
    private List<String> villanPos;
    private int noBricks;
    private List<String> bricksPos;
    private String[][] grid;
    private int[] playerPos;
    private int lost = 0;
    private int win = 0;
    private boolean bombPlaced = false;
    private List<int[]> bombPosList = new ArrayList<>();
    private int bombcount = 0;
    private int bcount = 1;
    private int radius = 0;

    public Main(int mapSize, String userPosition, String keyPosition,
                List<String> villanPos,
                int noBricks, List<String> bricksPos) {

        this.mapSize = mapSize;
        this.userPosition = userPosition;
        this.keyPosition = keyPosition;
        this.villanPos = villanPos;
        this.noBricks = noBricks;
        this.bricksPos = bricksPos;
        this.grid = new String[mapSize][mapSize];
    }

    public String[][] constructGrid() {
        // initialize grid with "0"
        for (int i = 0; i < mapSize; i++) {
            for (int j = 0; j < mapSize; j++) {
                grid[i][j] = "0";
            }
        }

        // Player position
        int playerPosx = Character.toLowerCase(userPosition.charAt(0)) - 96;
        int playerPosy = Character.toLowerCase(userPosition.charAt(1)) - 96;

        // Key position
        int keyx = Character.toLowerCase(keyPosition.charAt(0)) - 96;
        int keyy = Character.toLowerCase(keyPosition.charAt(1)) - 96;

        this.playerPos = new int[]{playerPosx, playerPosy};
        grid[playerPosx][playerPosy] = "P";
        grid[keyx][keyy] = "K";

        // Villains
        for (String pos : villanPos) {
            int vx = Character.toLowerCase(pos.charAt(0)) - 96;
            int vy = Character.toLowerCase(pos.charAt(1)) - 96;
            grid[vx][vy] = "V";
        }

        // Bricks
        for (String pos : bricksPos) {
            int bx = Character.toLowerCase(pos.charAt(0)) - 96;
            int by = Character.toLowerCase(pos.charAt(1)) - 96;
            grid[bx][by] = "B";
        }

        grid[0][0] = " ";
        for (int i = 0; i < mapSize; i++) {
            for (int j = 0; j < mapSize; j++) {
                if (i == 0 && j == 0) {
                } else if (i == 0 || j == 0) {
                    if (i == 0) {
                        grid[i][j] = String.valueOf((char) (j + 96));
                    } else {
                        grid[i][j] = String.valueOf((char) (i + 96));
                    }
                } else if (i == 1 || j == 1 || i == mapSize - 1 || j == mapSize - 1) {
                    grid[i][j] = "*";
                } else if (grid[i][j].equals("0")) {
                    grid[i][j] = " ";
                }
            }
        }
        grid[5][2] = "1";
        grid[6][3] = "2";
        grid[10][6] = "3";

        // Walls
        String[] walls = {
                "CC", "CE", "CG", "CI",
                "EC", "EE", "EG", "EI",
                "GC", "GE", "GG", "GI",
                "IC", "IE", "IG", "II"
        };

        for (String wall : walls) {
            int wx = Character.toLowerCase(wall.charAt(0)) - 96;
            int wy = Character.toLowerCase(wall.charAt(1)) - 96;
            grid[wx][wy] = "*";
        }
        return grid;
    }

    public void printGrid() {
        for (int i = 0; i < mapSize; i++) {
            for (int j = 0; j < mapSize; j++) {
                String display = grid[i][j];
                if (bombPosList != null && !bombPosList.isEmpty()) {
                    for (int idx = 0; idx < bombPosList.size(); idx++) {
                        int[] b = bombPosList.get(idx);
                        if (b != null && b[0] == i && b[1] == j && !Objects.equals(display, "P")) {
                            display = "O" + (idx + 1);
                            break;
                        }
                    }
                }
                System.out.print(display + " ");
            }
            System.out.println();
        }
    }

    public boolean boundaryCheck(int x, int y) {

        if ((x < 2 && y < 2) && (x >= grid.length - 1 && y >= grid[0].length - 1)) {
            return false;
        }

        if (Objects.equals(grid[x][y], "B")) {
            System.out.println("There is a Brick without breaking no further movement allowed");
            return false;
        }
        if (Objects.equals(grid[x][y], "*")) {
            System.out.println("This is a wall , no movement allowed");
            return false;
        }
        if (Objects.equals(grid[x][y], "V")) {
            System.out.println("You Lost");
            this.lost = 1;
            return false;
        }
        if (Objects.equals(grid[x][y], "K")) {
            System.out.println("You Won");
            this.win = 1;
            return false;
        }
        if (Objects.equals(grid[x][y], "1")) {
            System.out.println("you found an extra bomb");
            bcount += 1;
            grid[x][y] = " ";
        }
        if (Objects.equals(grid[x][y], "2")) {
            System.out.println("Bomb blast directions increased");
            radius += 1;
            grid[x][y] = " ";
        }
        if (Objects.equals(grid[x][y], "3")) {
            System.out.println("Bomb blast radius increased");
            radius += 1;
            grid[x][y] = " ";
        }

        return true;
    }

    public void plantBomb() {
        int x = playerPos[0];
        int y = playerPos[1];
        bombPosList.add(new int[]{x, y});
        if (bombPosList.size() > bcount) {
            System.out.println("You have reached the maximum planted bombs (" + bcount + "). Detonate before planting another.");
            bombPosList.remove(bombPosList.size() - 1);
            return;
        }

        bombPlaced = true;
        bombcount = bombPosList.size();
        System.out.println("Bomb planted at " + (char) (x + 96) + (char) (y + 96) + ", id=" + bombPosList.size());
    }

    public void detonateBomb() {
        if (bombPosList == null || bombPosList.isEmpty()) {
            System.out.println("No bomb to detonate");
            return;
        }

        int indexToDetonate = 0;
        if (bombPosList.size() > 1) {

            System.out.print("Enter choice: ");
            Scanner sc = new Scanner(System.in);
            int choice = sc.nextInt();

            if(choice == 5){
                System.out.println("Detonating all bombs!");
                while (!bombPosList.isEmpty()) {
                    detonateBomb();
                }
                return;
            }
            else if (choice < 0 || choice >= bombPosList.size()) {
                System.out.println("Invalid choice. Aborting detonation.");
                return;
            }
            else {
                System.out.println("Choose which bomb to detonate: else 5 to detonate all bombs");
                for (int i = 0; i < bombPosList.size(); i++) {
                    int[] b = bombPosList.get(i);
                    System.out.println(i + ": at " + (char) (b[0] + 96) + (char) (b[1] + 96));
                }
            }
                indexToDetonate = choice;
            }

            int[] chosen = bombPosList.get(indexToDetonate);
            int bx = chosen[0];
            int by = chosen[1];
            System.out.println("Bomb detonated at " + (char) (bx + 96) + (char) (by + 96));

            int[][] affected = {
                    {bx, by},
                    {bx - 1, by},
                    {bx + 1, by},
                    {bx, by - 1},
                    {bx, by + 1}
            };
            int[][] affected2 = {
                    {bx, by},
                    {bx - 1, by},
                    {bx + 1, by},
                    {bx, by - 1},
                    {bx, by + 1},
                    {bx - 1, by - 1},
                    {bx - 1, by + 1},
                    {bx + 1, by - 1},
                    {bx + 1, by + 1}
            };
            int[][] affected3 = {
                    {bx, by},
                    {bx - 2, by},
                    {bx + 2, by},
                    {bx, by - 2},
                    {bx, by + 2},
                    {bx - 2, by - 2},
                    {bx - 2, by + 2},
                    {bx + 2, by - 2},
                    {bx + 2, by + 2}
            };
            if (radius == 1) {
                affected = affected2;
            } else if (radius == 2) {
                affected = affected3;
            }
            for (int[] cell : affected) {
                int i = cell[0];
                int j = cell[1];
                if (i <= 1 || j <= 1 || i >= mapSize - 1 || j >= mapSize - 1) continue;
                String content = grid[i][j];
                if (Objects.equals(content, "P")) {
                    System.out.println("Player was inside blast radius. You Died.");
                    this.lost = 1;
                } else if (Objects.equals(content, "V")) {
                    System.out.println("Villain at " + (char) (i + 96) + (char) (j + 96) + " killed by blast");
                    grid[i][j] = " ";
                } else if (Objects.equals(content, "B")) {
                    System.out.println("Brick at " + (char) (i + 96) + (char) (j + 96) + " destroyed by blast");
                    grid[i][j] = " ";
                } else {
                    grid[i][j] = " ";
                }
            }
            // remove the detonated bomb from list
            bombPosList.remove(indexToDetonate);
            bombPlaced = !bombPosList.isEmpty();
            bombcount = bombPosList.size();
        }

        public int movements (String user){
            String[] moves = {"UP", "DOWN", "RIGHT", "LEFT", "TOP_RIGHT_D", "TOP_LEFT_D", "BOTTOM_RIGHT_D", "BOTTOM_LEFT_D", "PLANT_BOMB", "DETONATE_BOMB"};
            for (int i = 0; i < 8; i++) {
                System.out.println("Enter " + i + " for the " + moves[i] + " move");
            }
            System.out.println("Enter 8 for PLANT_BOMB");
            System.out.println("Enter 9 for DETONATE_BOMB");
            Scanner sc = new Scanner(System.in);
            int choice = sc.nextInt();
            int x = Character.toLowerCase(user.charAt(0)) - 96;
            int y = Character.toLowerCase(user.charAt(1)) - 96;
            int oldX = x;
            int oldY = y;
            System.out.println(x + " " + y);
            System.out.println(userPosition);

            switch (choice) {
                case 0:
                    if (boundaryCheck(x - 1, y)) {
                        x -= 1;
                    }
                    break;

                case 1:
                    if (boundaryCheck(x + 1, y)) x += 1;
                    break;
                case 2:
                    if (boundaryCheck(x, y + 1)) y += 1;
                    break;
                case 3:
                    if (boundaryCheck(x, y - 1)) y -= 1;
                    break;
                case 4:
                    if (boundaryCheck(x + 1, y + 1)) {
                        x += 1;
                        y += 1;
                    }
                    break;
                case 5:
                    if (boundaryCheck(x + 1, y - 1)) {
                        x += 1;
                        y -= 1;
                    }
                    break;
                case 6:
                    if (boundaryCheck(x - 1, y + 1)) {
                        x -= 1;
                        y += 1;
                    }
                    break;
                case 7:
                    if (boundaryCheck(x - 1, y - 1)) {
                        x -= 1;
                        y -= 1;
                    }
                    break;
                case 8:
                    plantBomb();
                    break;
                case 9:
                    detonateBomb();
                    break;
                default:
                    System.out.println("Invalid choice");
            }

            if (oldX != x || oldY != y) {
                this.grid[oldX][oldY] = " ";
                this.grid[x][y] = "P";
                this.userPosition = String.valueOf((char) (x + 96)) + (char) (y + 96);
                this.playerPos = new int[]{x, y};
                oldY = y;
                oldX = x;
            }

            if (this.lost == 1 || this.win == 1) {
                return 0;
            }

            System.out.println(x + " " + y);
            printGrid();
            return 1;
        }


        public static void main (String[]args){
            List<String> villains = Arrays.asList("BH", "DF");
            List<String> bricks = Arrays.asList("DD", "ED", "FB", "FF", "GB", "HD");

            Main s = new Main(12, "CB", "FD", villains, 6, bricks);
            String[][] grid = s.constructGrid();
            s.printGrid();
            int move = 1;
            while (move != 0) {
                String user = s.userPosition;
                move = s.movements(user);
            }
        }
}