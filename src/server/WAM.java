package server;

import java.util.Random;

/**
 *
 * @author Liang, Albin
 * @author Souza, Saakshi
 */

public class WAM{
    private final static Integer[] ROWS = new Integer[1];
    private final static Integer[] COLS = new Integer[1];
    private int rows;
    private int cols;
    private int numMoles;
    private final static Random rng = new Random();
    private final static int SEED = 0;



    public WAM(int rows, int cols){
        this.rows=rows;
        this.cols=cols;
        ROWS[1] = rows;
        COLS[1] = cols;
        numMoles = rows*cols;
        this.rng.setSeed(SEED);



    }

    public void startHiding(){
        Thread[] moles = new Thread[numMoles];
        for (int i = 0 ; i < numMoles; i++){
            moles[i]= new Thread(new Mole(i));
            moles[i].start();
        }
    }

    public void appear(int id){

    }


    public int getRandomTime(int min, int max) {return rng.nextInt(max-min + 1 ) + min;}



    public static void main(String[] args){



    }
public class Mole extends Thread{

        //This mole thread's unique ID
        private final static int MINUPTIME = 3;
        private final static int MAXUPTIME = 5;
        private final static int MINDOWNTIME = 2;
        private final static int MAXDOWNTIME = 10;
        private int id;
        private boolean up;
        private WAM game;



        public Mole(int id){
            this.id = id;
            this.up=false;
        }

        public int getID(){return id;}




        @Override
        public void run(){
            try {
                this.sleep(getRandomTime(MINDOWNTIME, MAXDOWNTIME));
                this.up = true;
                this.sleep(getRandomTime(MINUPTIME, MAXUPTIME));
                this.up = false;

            }catch(InterruptedException ie){}


       }
    }
}
