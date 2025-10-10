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

    public Main(int mapSize, String userPosition, String keyPosition,
                int noVillan, List<String> villanPos,
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
                if (i == 0 && j == 0) {}
                else if (i == 0 || j == 0) {
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

        // Walls
        String[] walls = {
                "CC","CE","CG","CI",
                "EC","EE","EG","EI",
                "GC","GE","GG","GI",
                "IC","IE","IG","II"
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
                System.out.print(grid[i][j] + " ");
            }
            System.out.println();
        }
    }

    public boolean boundaryCheck(int x, int y) {

        if ((x<2 && y<2 )&& (x>=grid.length-1 && y>=grid[0].length-1))
        {
            return false;
        }

        if (Objects.equals(grid[x][y], "B")) {
            System.out.println("There is a Brick without breaking no further movement allowed");
            return false;
        }
        if(Objects.equals(grid[x][y], "*")) {
            System.out.println("This is a wall , no movement allowed");
            return false;
        }
        if(Objects.equals(grid[x][y], "V")) {
            System.out.println("You Lost");
            this.lost=1;
            return false;
        }
        if(Objects.equals(grid[x][y], "K")) {
            System.out.println("You Won");
            this.win=1;
            return false;
        }

        return true;
    }
    public int movements (String[][] grid, String user) {
        String[] moves = {"UP", "DOWN", "RIGHT", "LEFT", "TOP_RIGHT_D", "TOP_LEFT_D", "BOTTOM_RIGHT_D", "BOTTOM_LEFT_D"};
        for(int i=0; i<8; i++) {
            System.out.println("Enter " + i + " for the " + moves[i] + " move");
        }
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
                if (boundaryCheck(x-1,y))
                {
                    x-=1;
                }
                break;

            case 1:
                if (boundaryCheck(x+1,y)) x+=1;
                break;
            case 2:
                if (boundaryCheck(x,y+1)) y+=1;
                break;
            case 3:
                if (boundaryCheck(x,y-1))y-=1;
                break;
            case 4:
                if (boundaryCheck(x+1,y+1)) {
                    x += 1;
                    y += 1;
                }
                break;
            case 5:
                if (boundaryCheck(x+1,y-1)){
                    x += 1;
                    y -= 1;
                }
                break;
            case 6:
                if (boundaryCheck(x-1,y+1)){
                    x-=1;
                    y+=1;
                }
                break;
            case 7:
                if (boundaryCheck(x-1,y-1)) {
                    x -= 1;
                    y -= 1;
                }
                break;
            default:
                System.out.println("Invalid choice");
        }

        if(oldX != x || oldY != y)
        {
            this.grid[oldX][oldY] = " ";
            this.grid[x][y] = "P";
            this.userPosition = String.valueOf((char)(x + 96)) + (char)(y + 96);
            oldY = y;
            oldX = x;
        }

        if(this.lost == 1 || this.win == 1) {
            return 0;
        }

        System.out.println(x+ " " +y);
        printGrid();
        return 1;
    }


    public static void main(String[] args) {
//        you can plant a bomb and detonate ,
//        if the bomb is detonated damage range is 1 cell ( 4direction ) ,
//        if u are in that possition u’ll die ,
//        so detonate bomb when u are not within the range
        List<String> villains = Arrays.asList("BH", "DF");
        List<String> bricks = Arrays.asList("DD", "ED", "FB", "FF", "GB", "HD");

        Main s = new Main(12, "CB", "FD", 2, villains, 6, bricks);
        String [][] grid = s.constructGrid();
        s.printGrid();
        int move = 1;
        while(move != 0) {
            String user = s.userPosition;
            move = s.movements(grid, user);
        }
    }

}
