package server;

public class WAM{
    private final static Integer[] ROWS = new Integer[1];
    private final static Integer[] COLS = new Integer[1];
    private int rows;
    private int cols;
    private int duration;

    public WAM(int rows, int cols, int duration){
        this.rows=rows;
        this.cols=cols;
        ROWS[1] = rows;
        COLS[1] = cols;
        this.duration=duration;


    }

    public void run(){

    }

    public void startHiding(){
        int total = rows * cols;
        Thread[] moles = new Thread[total];
        for (int i = 0 ; i < total; i++){
        moles[i]= new Thread(new Mole(i));
        }
    }

    public static void main(String[] args){
        final int rows = ROWS[0];
        final int cols = COLS[0];
        int total = rows * cols;




}

public class Mole extends Thread{

        //This mole thread's unique ID
        private int id;
        private WAM game;



        public Mole(int id){
            this.id = id;

        }

        public int getID(){return id;}

        @Override
    public void run(){
            game.startHiding(this);
        }


    }
}