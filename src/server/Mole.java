package server;

import java.util.Random;

public class Mole extends Thread{


    private final static int MINUPTIME = 3;

    private final static int MAXUPTIME = 5;

    private final static int MINDOWNTIME = 2;

    private final static int MAXDOWNTIME = 10;

    private final static Random rng = new Random();

    private final static int SEED = 0;

    //This mole thread's unique ID
    private int id;

    private boolean up;



    public Mole(int id){
        this.id = id;
        this.up = false;
        this.rng.setSeed(SEED);
    }

    public int getID(){return id;}

    public boolean getStatus(){return this.up;}

    public void up(){
        this.up = true;
    }

    public void down(){
        this.up = false ;
    }

    public int getRandomTime(int min, int max) {return rng.nextInt(max-min + 1 ) + min;}


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