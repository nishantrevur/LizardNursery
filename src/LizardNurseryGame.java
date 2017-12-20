import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class LizardNurseryGame extends Thread {

    FileInputStream in = null;
    FileOutputStream out = null;
    String algoType, fileLine;
    char lineChar[];
    int n, p, lizardCount;
    int grid[][];
    int lizardPos[][];
    int nextSafe[];
    int placed;
    int prevI, prevJ;
    int treeCount = 0;
    ArrayList<Lizards> liz;
    Queue<BFSGrid> q = new LinkedList<>();
    Queue<BFSGrid> used = new LinkedList<>();
    static int numL;
    long start = 0, end = 0;

    public void solveGame() {
        start = System.currentTimeMillis();
        lizardCount = 0;
        try {
            in = new FileInputStream("/Users/nishantrevur/Java Code/AI Assignment/input_sa_50.txt");
            out = new FileOutputStream("/Users/nishantrevur/Java Code/AI Assignment/output.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            algoType = br.readLine();
            n = Integer.parseInt(br.readLine());
            p = Integer.parseInt(br.readLine());
            numL = p;
            //System.out.println(algoType + " " + n + " " + p);
            grid = new int[n][n];
            lizardPos = new int[p][2];
            fileLine = br.readLine();
            liz = new ArrayList<Lizards>();
            liz.ensureCapacity(21474);
            for (int i = 0; i < n; i++) {
                lineChar = fileLine.toCharArray();
                for (int j = 0; j < n; j++) {
                    grid[i][j] = Character.getNumericValue(lineChar[j]);
                    if (grid[i][j] == 2)
                        treeCount++;
                }
                fileLine = br.readLine();
            }
            for (int i = 0; i < p; i++) {
                lizardPos[i][0] = -1;
                lizardPos[i][1] = -1;
            }
            if (p == 0) {
                printAnswerToFile(grid, out, "OK");
                return;
            }
            if (treeCount == n * n) {
                printAnswerToFile(grid, out, "FAIL");
                return;
            }
            if (treeCount == 0 && p > n) {
                printAnswerToFile(grid, out, "FAIL");
                return;
            }
            try {
                if (algoType.equals("DFS")) {
                    int res[][] = doDFS(0, 0, (n * n) - treeCount);
                    if (res == null) {
                        System.out.println("FAIL");
                        printAnswerToFile(grid, out, "FAIL");
                    }
                } else if (algoType.equals("BFS")) {
                    doBFS();
                } else {
                    doSA();
                }
            }catch (OutOfMemoryError error)
            {
                printAnswerToFile(grid,out,"FAIL");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int[][] doDFS(int x, int y, int safeC) {
        if (lizardCount == p) {
            printFinal(grid);
            printAnswerToFile(grid, out, "OK");
            return grid;
        } else if ((System.currentTimeMillis() - start) > 270 * 1000) {
            printAnswerToFile(grid, out, "FAIL");
            return null;
        } else if (safeC < (p - lizardCount)) {
            return null;
        } else {
            //printGrid(grid);
            for (int i = x; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (grid[i][j] == 0) {
                        placeLizard(i, j, safeC);
                        int temp[][];
                        temp = doDFS(i, j, safeC);
                        if (temp != null)
                            return temp;
                        removeLizard(i, j, safeC);
                    }
                }
            }
        }
        return null;
    }

    public void doBFS() throws OutOfMemoryError {
        BFSGrid gr = new BFSGrid(grid, 0, n * n, 0);
        q.add(gr);
        BFSGrid prev;
        String t = "FAIL";
        boolean nextRow = false;
        int c = 0;
        int row, col, lizCount;
        row = 0;
        col = 0;
        lizCount = 0;
        while (!q.isEmpty()) {
            gr = q.element();
            q.remove();
            prev = new BFSGrid(createNewGrid(gr.grid), gr.lizardCount, gr.safeCount, gr.rowSafe);
            if ((System.currentTimeMillis() - start) > 270 * 1000) {
                printAnswerToFile(grid, out, "FAIL");
                break;
            }
            if (gr.lizardCount == p) {
                printFinal(gr.grid);
                t = "OK";
                printAnswerToFile(gr.grid, out, "OK");
                System.exit(0);
            }
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (gr.grid[i][j] == 0) {

                        if (gr.lizardCount > 0 && (gr.pos[0] > i || (gr.pos[0] == i && gr.pos[1] > j)) && (gr.safeCount > p - gr.lizardCount)) {
                        } else {
                            gr.lizardCount++;
                            placeLizard(gr.grid, i, j, gr.lizardCount);
                            if (i > gr.pos[0])
                                nextRow = true;
                            gr.pos[0] = i;
                            gr.pos[1] = j;
                            markNotSafe(gr.grid, i, j, gr.lizardCount, gr.safeCount, n - j);
                            q.add(gr);
                            gr = new BFSGrid(prev.grid, prev.lizardCount, prev.safeCount, prev.rowSafe);
                        }
                    }
                }
                //printGrid(gr.grid);
            }
        }

        /*while(!q.isEmpty())
        {
            gr = q.element();
            q.remove();
            prev = new BFSGrid2(createNewGrid(gr.grid),gr.lizardCount,gr.safeCount,gr.row,gr.col);
            if(gr.lizardCount==p)
            {
                printGrid(gr.grid);
                System.exit(0);
            }
            for(int i=gr.row;i<n;i++)
            {
                for(int j=gr.col;j<n;j++)
                {
                    placeNextRow(gr,i,j, gr.lizardCount);
                    printGrid(gr.grid);
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if(gr.safeCount>(p-gr.lizardCount))
                    {
                    }
                    q.add(gr);
                    gr = new BFSGrid2(prev.grid,prev.lizardCount,prev.safeCount,prev.row,prev.safeCount);
                }
                printGrid(prev.grid);
            }
            System.out.println("Exited");
        }*/

        if (t.equals("FAIL")) {
            printAnswerToFile(gr.grid, out, t);
        }
    }

    public void doSA() {
        double tempF, tempC, tempI;
        double delta;
        placed = 0;
        lizardCount = 0;
        tempF = 0;
        tempI = 100000000;
        tempC = tempI;
        int t = 1;
        SAGrid current = new SAGrid(grid, t);
        randomInitialPositions(current.grid);
        SAGrid next;
        int revert[];
        current.energy = findConflicts(current.grid);
        if (current.energy == 0) {
            printGrid(current.grid);
            System.exit(0);
        }
        //printGrid(current.grid);
        //System.exit(0);
        while (t > -1) {
            tempC = schedule(t, tempF, tempI, tempC, t);
            //System.out.println(tempC+" Temp");
            if (tempC < tempF) {
                System.out.println("Failed");
                printAnswerToFile(grid, out, "FAIL");
                break;
            }
            if ((System.currentTimeMillis() - start) > 270 * 1000) {
                printAnswerToFile(grid, out, "FAIL");
                break;
            }
            next = new SAGrid(current.grid, current.energy, t);
           /*System.out.println("1");*/
            //printGrid(current.grid);
            revert = nextRandomPosition(next.grid);
           /*printGrid(next.grid);*/
            next.energy = findConflicts(next.grid);
           /*printGrid(next.grid);*/
            delta = next.energy - current.energy;
            double prob = Math.exp((-delta / tempC));
            //System.out.println(prob+"Prob");
            double rand = (Math.random());
            //prob = (prob-Math.floor(prob));
            if (next.energy == 0) {
                printFinal(next.grid);
                printAnswerToFile(next.grid, out, "OK");
                return;
            }
           /*System.out.println("temp "+tempC+" current energy "+current.energy+" new energy "+next.energy+" DELTA  "+delta+" unique "+next.uniq);
           System.out.println("Rand: "+rand+"  prob: "+prob);*/
            if (delta < 0) {
                current = next;
                //System.out.println("Selected good");
              /* System.out.println("6");
               printGrid(current.grid);*/
            } else if (rand <= prob) {
                //System.out.println("Selected bad");
                current = next;
               /*System.out.println("7");
               printGrid(current.grid);*/
            } else {
                //System.out.println("Skipped");
                lizardPos[revert[0]][0] = revert[1];
                lizardPos[revert[0]][1] = revert[2];
            }
            t++;
            //printGrid(current.grid);
           /*System.out.println("Current");
           printGrid(current.grid);
           System.out.println("Next");
           printGrid(next.grid);
           System.out.println("*****************");*/
            next = null;
            /*System.out.println("8");
            printGrid(current.grid);*/
        }
    }

    public void markNotSafe(int i, int j, int safeC) {

        //System.out.println("Row right");
        // row right
        ArrayList<UnsafePosition> t = new ArrayList<UnsafePosition>();
        t.ensureCapacity(21474);
        for (int k = j + 1; k < n; k++) {
            if (grid[i][k] == 2) {
                break;
            }
            if (grid[i][k] == 0) {
                t.add(new UnsafePosition(i, k));
                //System.out.println("Unsafe position"+t.get(t.size()-1).i+"  "+t.get(t.size()-1).j);
                grid[i][k] = -lizardCount;
                safeC--;
            }
        }

        //printGrid(grid);
        //System.out.println("Col down");
        //col down
        for (int k = i + 1; k < n; k++) {
            if (grid[k][j] == 2) {
                break;
            }
            if (grid[k][j] == 0) {
                t.add(new UnsafePosition(k, j));
                //System.out.println("Unsafe position"+t.get(t.size()-1).i+"  "+t.get(t.size()-1).j);
                grid[k][j] = -lizardCount;
                safeC--;
            }
        }

        int m, n1;
        m = i + 1;
        n1 = j + 1;
        //System.out.println("Diag down right");
        //diag down right
        while (m < n && n1 < n) {
            if (grid[m][n1] == 2)
                break;
            if (grid[m][n1] == 0) {
                t.add(new UnsafePosition(m, n1));
                //System.out.println("Unsafe position"+t.get(t.size()-1).i+"  "+t.get(t.size()-1).j);
                grid[m][n1] = -lizardCount;
                safeC--;
            }
            m++;
            n1++;
        }

        //diag down left
        // System.out.println("Diag down left");
        m = i + 1;
        n1 = j - 1;
        while (n1 > -1 && m < n) {
            if (grid[m][n1] == 2)
                break;
            if (grid[m][n1] == 0) {
                t.add(new UnsafePosition(m, n1));
                //System.out.println("Unsafe position"+t.get(t.size()-1).i+"  "+t.get(t.size()-1).j);
                grid[m][n1] = -lizardCount;
                safeC--;
            }
            m++;
            n1--;
        }

        // System.out.println("Row left");
        //row left
        for (int k = j - 1; k > -1; k--) {
            if (grid[i][k] == 2) {
                break;
            }
            if (grid[i][k] == 0) {
                t.add(new UnsafePosition(i, k));
                //System.out.println("Unsafe position"+t.get(t.size()-1).i+"  "+t.get(t.size()-1).j);
                grid[i][k] = -lizardCount;
                safeC--;
            }
        }
        liz.add(new Lizards(t));
    }

    public void markNotSafe(int gr[][], int i, int j, int lizardCount, int safeCount, int rowSafe) {

        //System.out.println("Row right");
        // row right
        for (int k = j + 1; k < n; k++) {
            if (gr[i][k] == 2) {
                rowSafe--;
                break;
            }
            if (gr[i][k] == 0) {
                gr[i][k] = -lizardCount;
                safeCount--;
                rowSafe--;
            }
        }

        //printGrid(grid);
        //System.out.println("Col down");
        //col down
        for (int k = i + 1; k < n; k++) {
            if (gr[k][j] == 2) {
                break;
            }
            if (gr[k][j] == 0) {
                gr[k][j] = -lizardCount;
                safeCount--;
            }
        }

        int m, n1;
        m = i + 1;
        n1 = j + 1;
        //System.out.println("Diag down right");
        //diag down right
        while (m < n && n1 < n) {
            if (gr[m][n1] == 2)
                break;
            if (gr[m][n1] == 0) {
                gr[m][n1] = -lizardCount;
                safeCount--;
            }
            m++;
            n1++;
        }

        //diag down left
        // System.out.println("Diag down left");
        m = i + 1;
        n1 = j - 1;
        while (n1 > -1 && m < n) {
            if (gr[m][n1] == 2)
                break;
            if (gr[m][n1] == 0) {
                gr[m][n1] = -lizardCount;
                safeCount--;
            }
            m++;
            n1--;
        }

        // System.out.println("Row left");
        //row left
        for (int k = j - 1; k > -1; k--) {
            if (gr[i][k] == 2) {
                break;
            }
            if (gr[i][k] == 0) {
                gr[i][k] = -lizardCount;
                safeCount--;
            }
        }
    }


    public void placeLizard(int i, int j, int safeC) {
        lizardCount++;
        grid[i][j] = 1;
        lizardPos[lizardCount - 1][0] = i;
        lizardPos[lizardCount - 1][1] = j;
        markNotSafe(i, j, safeC);
    }

    public void placeLizard(int gr[][], int i, int j, int lizardCount) {
        gr[i][j] = 1;
    }

    public void removeLizard(int i, int j, int safeC) {
        if (lizardCount > 0) {
            grid[i][j] = 0;
            lizardPos[lizardCount - 1][0] = -1;
            lizardPos[lizardCount - 1][1] = -1;
            markSafe(safeC);
            lizardCount--;
        }
    }

    public void markSafe(int safeC) {
        ArrayList<UnsafePosition> t = liz.get(liz.size() - 1).u;
        for (int i = 0; i < t.size(); i++) {
            grid[t.get(i).i][t.get(i).j] = 0;
            safeC++;
        }

        liz.remove(liz.size() - 1);
    }

    public void printGrid(int grid[][]) {
        try {
            Runtime.getRuntime().exec("");
        } catch (Exception e) {

        }
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                System.out.print(" " + grid[i][j] + "      ");
            }
            System.out.println("");
        }

/*        for(int i=0;i<lizardCount;i++)
        {
            System.out.println(lizardPos[i][0]+" "+lizardPos[i][1]);
        }*/
        System.out.println("--------");
    }

    public void printFinal(int grid[][]) {
//        int count=0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (grid[i][j] < 0)
                    grid[i][j] = 0;
                System.out.print(grid[i][j] + "  ");
            }
            System.out.println();
        }
        //System.out.println(count);
    }

    public int[][] createNewGrid(int g[][]) {
        int c[][] = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                c[i][j] = g[i][j];
            }
        }
        return g;
    }

    public void printAnswerToFile(int grid[][], FileOutputStream out, String answer) {
        String t = "";
        try {
            if (answer.equals("OK")) {
                out.write("OK\n".getBytes());
                for (int i = 0; i < n; i++) {
                    for (int j = 0; j < n; j++) {
                        if (grid[i][j] < 0)
                            t += "0";
                        else
                            t += grid[i][j];
                    }
                    t += "\n";
                    out.write(t.getBytes());
                    t = "";
                }
            } else {
                out.write("FAIL".getBytes());
            }
            out.close();
        } catch (IOException e) {

        }
    }

    public void randomInitialPositions(int grid[][]) {
        int x = 0, y = 0;
        Random r = new Random();
        r.ints(0, n);
        while (lizardCount < p) {
            x = r.nextInt(n);
            y = r.nextInt(n);
            if (notRepeated(x, y) && grid[x][y] != 2) {
                lizardCount++;
                placed++;
                lizardPos[lizardCount - 1][0] = x;
                lizardPos[lizardCount - 1][1] = y;
                grid[x][y] = 1;
            }
        }
    }

    public boolean notRepeated(int x, int y) {
        for (int i = 0; i < lizardCount; i++) {
            if (lizardPos[i][0] == x && lizardPos[i][1] == y)
                return false;
        }

        return true;
    }

    public int[] nextRandomPosition(int gr[][]) {
        Random r = new Random();
        int randomLizard = r.nextInt(lizardCount);
        int cx, cy;
        int x = 0, y = 0;
        cx = lizardPos[randomLizard][0];
        cy = lizardPos[randomLizard][1];
        int revert[] = new int[3];
        revert[0] = randomLizard;
        revert[1] = cx;
        revert[2] = cy;
        boolean isPlaced = false;
        /*System.out.println("2");
       printGrid(gr);*/
        while (!isPlaced) {
            x = r.nextInt(n);
            y = r.nextInt(n);
            if ((System.currentTimeMillis() - start) > 270 * 1000) {
                printAnswerToFile(grid, out, "FAIL");
            }
            if (notRepeated(x, y) && gr[x][y] == 0) {
               /*System.out.println("3");
               printGrid(gr);*/
                gr[cx][cy] = 0;
                //System.out.println("gr[cx]:"+cx+" cy: "+cy+"  "+"lizardpos: "+lizardPos[randomLizard][0]+" :"+lizardPos[randomLizard][1]);
                gr[x][y] = 1;
               /*System.out.println("4");
               printGrid(gr);*/
                lizardPos[randomLizard][0] = x;
                lizardPos[randomLizard][1] = y;
                isPlaced = true;
            }
        }
        //System.out.println("Position cx,cy"+ cx+" "+cy+" moved to"+" x,y"+x+" "+y);
        /*System.out.println("5");
       printGrid(gr);*/
        return revert;
    }

    public int findConflicts(int gr[][]) {
        //System.out.println("Row right");
        // row right
        int conflicts = 0;
        int i, j;
        for (int c = 0; c < lizardCount; c++) {
            i = lizardPos[c][0];
            j = lizardPos[c][1];
            for (int k = j + 1; k < n; k++) {
                if (gr[i][k] == 2)
                    break;
                if (gr[i][k] == 0) {
                    //gr[i][k] = -lizardCount;
                }
                if (gr[i][k] == 1) {
                    conflicts++;
                }
            }
            //printGrid(grid);
            // System.out.println("Row left");
            //row left
            for (int k = j - 1; k > -1; k--) {
                if (gr[i][k] == 2)
                    break;
                if (gr[i][k] == 0) {
                    //gr[i][k] = -lizardCount;
                }
                if (gr[i][k] == 1) {
                    conflicts++;
                }
            }

            //System.out.println("Col down");
            //col down
            for (int k = i + 1; k < n; k++) {
                if (gr[k][j] == 2)
                    break;
                if (gr[k][j] == 0) {
                    //gr[k][j] = -lizardCount;
                }
                if (gr[k][j] == 1) {
                    conflicts++;
                }
            }

            //System.out.println("Col up");
            //col up
            for (int k = i - 1; k > -1; k--) {
                if (gr[k][j] == 2)
                    break;
                if (gr[k][j] == 0) {
                    //gr[k][j] = -lizardCount;
                }
                if (gr[k][j] == 1) {
                    conflicts++;
                }
            }

            int m, n1;
            m = i + 1;
            n1 = j + 1;
            //System.out.println("Diag down right");
            //diag down right
            while (m > -1 && n1 > -1 && m < n && n1 < n) {
                if (gr[m][n1] == 2)
                    break;
                if (gr[m][n1] == 0) {
                    //gr[m][n1] = -lizardCount;
                }
                if (gr[m][n1] == 1) {
                    conflicts++;
                }
                m++;
                n1++;
            }

            //diag down left
            // System.out.println("Diag down left");
            m = i + 1;
            n1 = j - 1;
            while (m > -1 && n1 > -1 && m < n && n1 < n) {
                if (gr[m][n1] == 2)
                    break;
                if (gr[m][n1] == 0) {
                    //gr[m][n1] = -lizardCount;
                }
                if (gr[m][n1] == 1) {
                    conflicts++;
                }
                m++;
                n1--;
            }

            //diag up right
            //System.out.println("Diag up right");
            m = i - 1;
            n1 = j + 1;
            while (m > -1 && n1 > -1 && m < n && n1 < n) {
                if (gr[m][n1] == 2)
                    break;
                if (gr[m][n1] == 0) {
                    //gr[m][n1] = -lizardCount;
                }
                if (gr[m][n1] == 1) {
                    conflicts++;
                }
                m--;
                n1++;
            }

            //diag up left
            //System.out.println("Diag up left");
            m = i - 1;
            n1 = j - 1;
            while (m > -1 && n1 > -1 && m < n && n1 < n) {
                if (gr[m][n1] == 2)
                    break;
                if (gr[m][n1] == 0) {
                    //gr[m][n1] = -lizardCount;
                }
                if (gr[m][n1] == 1) {
                    conflicts++;
                }
                m--;
                n1--;
            }
        }
        //System.out.println("No of con " + conflicts*0.5);
        return (int) (conflicts * 0.5);
    }

    public double schedule(int n, double tF, double tI, double tC, int t) {
        double pL = 0.25;
        double pE = 0.31;
        double A = 0;
        double B = 0;
        double alpha = 0.3;
        A = ((tI - tF) * (n + 1)) / n;
        B = tI - A;
        tC = (A / (n + 1) + B) * pE + ((alpha * tI) / Math.log(n + 1)) * pL;
        //tC = 1 / Math.log(t);
        //tC = (1/Math.log(tC+p));
        return tC;
    }

    public boolean randomProb(double e) {
        int p = (int) (Math.random() * 4 * n);
        return e > p;
    }

    public void placeNextRow(BFSGrid2 gr, int row, int col, int lizCount) {
        for (int j = col; j < n; j++) {
            if (gr.grid[row][j] == 0) {
                gr.grid[row][j] = 1;
                gr.lizardCount++;
                gr.row = row;
                gr.col = j;
                markNotSafe(gr.grid, row, j, gr.lizardCount, gr.safeCount, gr.rowSafe);
            }
        }
    }
}

class UnsafePosition {
    int i, j;

    public UnsafePosition(int x, int y) {
        i = x;
        j = y;
    }
}

class Lizards {
    ArrayList<UnsafePosition> u;

    public Lizards(ArrayList<UnsafePosition> un) {
        u = un;
    }
}

class BFSGrid {
    int grid[][];
    int lizardCount;
    int safeCount;
    int pos[] = new int[2];
    int rowSafe;

    public BFSGrid(int g[][], int lc, int safe, int row) {
        int n = g[0].length;
        grid = new int[n][n];
        safeCount = safe;
        rowSafe = row;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                grid[i][j] = g[i][j];
            }
        }
        lizardCount = lc;
    }
}

class BFSGrid2 {
    int grid[][];
    int lizardCount;
    int safeCount;
    int rowSafe;
    int row;
    int col;

    public BFSGrid2(int g[][], int lc, int safe, int r, int c) {
        int n = g[0].length;
        grid = new int[n][n];
        safeCount = safe;
        rowSafe = row;
        row = r;
        col = c;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                grid[i][j] = g[i][j];
            }
        }
        lizardCount = lc;
    }
}

class SAGrid {
    int grid[][];
    int energy;
    int uniq;

    public SAGrid(int g[][], int u) {
        int n = g[0].length;
        grid = new int[n][n];
        uniq = u;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                grid[i][j] = g[i][j];
            }
        }
    }

    public SAGrid(int g[][], int e, int u) {
        int n = g[0].length;
        energy = e;
        uniq = u;
        grid = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                grid[i][j] = g[i][j];
            }
        }
    }
}