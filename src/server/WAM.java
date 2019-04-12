package server;

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

    public WAM(int rows, int cols){
        this.rows=rows;
        this.cols=cols;
        ROWS[1] = rows;
        COLS[1] = cols;
        numMoles = rows*cols;



    }

    public void startHiding(){
        Thread[] moles = new Thread[numMoles];
        for (int i = 0 ; i < numMoles; i++){
        moles[i]= new Thread(new Mole(i));
        moles[i].start();
        }
    }

    public static void main(String[] args){




}

public class Mole extends Thread{

        //This mole thread's unique ID
        final static int MINUPTIME = 3;
        final static int MAXUPTIME = 5;
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

        }


    }
}