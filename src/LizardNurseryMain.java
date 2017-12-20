
import java.io.*;
/*
 0 - Nothing
 1 - Placed baby lizard
 2 - Tree

 output.txt : First line OK or FAIL

 -----------------------------------

 n - Number of rows and columns
 p - Number of lizards to place
 */
class LizardNurseryMain {
    public static void main (String args[]){
        long start;
        long end;
        start = System.currentTimeMillis();
       LizardNurseryGame game = new LizardNurseryGame();
       game.solveGame();
       end = System.currentTimeMillis();
       System.out.println((end-start)+"s");
    }
}