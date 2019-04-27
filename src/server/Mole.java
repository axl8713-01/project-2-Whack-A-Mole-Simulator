package server;

import common.WAMException;

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

    private WAMGame game;


    public Mole(int id, WAMGame game) {
        this.id = id;
        this.up = false;
        this.rng.setSeed(SEED);
        this.game = game;
    }

    public int getID(){return id;}

    public boolean getStatus(){return this.up;}

    public void down()throws WAMException{
        this.up = false ;
    }

    public int getRandomTime(int min, int max) {return rng.nextInt(max-min + 1 ) + min;}

    private void hideOrAppear(int minDownTime, int maxDownTime, boolean upOrDown) throws InterruptedException, WAMException{
        if (!up == upOrDown) {
            this.up = upOrDown;
        }
        this.sleep(getRandomTime(minDownTime, maxDownTime)*1000);
    }

    @Override
    public void run(){
        while (game.RUNNING) {
            try {
                this.hideOrAppear(MINDOWNTIME,MAXDOWNTIME, false);
                game.popUp(this.getID());
                this.hideOrAppear(MINUPTIME,MAXUPTIME,true);
                game.hide(this.getID());
            } catch (InterruptedException ie) {}
            catch (WAMException we){}
        }
    }
}