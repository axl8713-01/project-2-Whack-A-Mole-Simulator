package server;

import common.WAMException;

import java.util.Random;

public class Mole extends Thread{

    //the minimum time the mole can be up
    private final static int MINUPTIME = 3;

    //the maximum time the mole can be up
    private final static int MAXUPTIME = 5;

    //the minimum time the mole can be down
    private final static int MINDOWNTIME = 2;

    //the maximum time the mole can be down
    private final static int MAXDOWNTIME = 10;

    //rng to get the random up and down times.
    private final static Random rng = new Random();

    //the seed for the random.
    private final static int SEED = 0;

    //This mole thread's unique ID
    private int id;

    //whether this mole is up or down.
    private boolean up;

    //the game that controls all the moles and player communication.
    private WAMGame game;

    /**
     * the constructor for a mole
     *
     * @param id the unique id of a mole
     * @param game the game that the mole is part of
     */
    public Mole(int id, WAMGame game) {
        this.id = id;
        this.up = false;
        this.rng.setSeed(SEED);
        this.game = game;
    }

    //basic id getter for the moles.
    public int getID(){return id;}

    //basic status getter for the moles
    public boolean getStatus(){return this.up;}

    //setter that makes the mole go down, Whacks can cause this.
    public void down()throws WAMException{
        this.up = false ;
    }

    /**
     * gets a random time for the mole to hide with the range passed in as arguments.
     * @param min the lower limit of the range
     * @param max the upper limit of the range
     * @return the random integer.
     */
    public int getRandomTime(int min, int max) {return rng.nextInt(max-min + 1 ) + min;}

    /**
     * the method that causes the mole to sleep and simulate the up and down movement a mole can produce.
     *
     * @param minTime the lower range to sleep for
     * @param maxTime the upper range to sleep for
     * @param upOrDown whether you are going up or down with mole true for up, false for down.
     * @throws InterruptedException if the concurrent threading runs into an issue.
     * @throws WAMException catches other exceptions.
     */
    private void hideOrAppear(int minTime, int maxTime, boolean upOrDown) throws InterruptedException, WAMException{
        if (!up == upOrDown) {//if it doesn't match what you are trying to set it to.
            this.up = upOrDown;//change the state.
        }
        this.sleep(getRandomTime(minTime, maxTime)*1000);//then sleep for that long to simulate the up/down.
    }

    @Override
    public void run(){
        while (game.RUNNING) {//while the game is still running
            try {
                this.hideOrAppear(MINDOWNTIME,MAXDOWNTIME, false);// start with being hidden
                game.popUp(this.getID());//then popup and let them know
                this.hideOrAppear(MINUPTIME,MAXUPTIME,true);//then pop up
                game.hide(this.getID());//let them know after you go down from being up.
            } catch (InterruptedException ie) {}
            catch (WAMException we){}
        }
    }
}