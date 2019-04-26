package server;

import java.util.Random;

/**
 *
 * @author Liang, Albin
 * @author Souza, Saakshi
 */

public class WAM {
    private final static Integer[] ROWS = new Integer[1];
    private final static Integer[] COLS = new Integer[1];
    private int rows;
    private int cols;
    private int numMoles;



    public WAM(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        ROWS[1] = rows;
        COLS[1] = cols;
        numMoles = rows * cols;



    }

//    public Mole[] startHiding() {
//        Mole[] moles = new Mole[numMoles];
//        for (int i = 0; i < numMoles; i++) {
//            moles[i] = new Mole(i);
//            moles[i].start();
//        }
//        return moles;
//    }






    public static void main(String[] args){



    }
}

