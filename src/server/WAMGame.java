package server;

import common.WAMException;

import java.io.IOException;

import static common.WAMProtocol.SCORE;

/**
 *
 * @author Liang, Albin
 * @author Souza, Saakshi
 */


public class WAMGame implements Runnable {

    private int rows;

    private int cols;

    private int duration;

    public boolean RUNNING = true;
//    private WAM game;

    private WAMPlayer[] players;

    private Integer[] scores;

    private int numMoles;

    private Mole[] moles;





    public WAMGame(int rows, int cols, int duration, WAMPlayer... players){

        this.rows = rows;
        this.cols = cols;
        this.duration = duration;
        this.players = players;
        this.scores = new Integer[players.length];
        this.numMoles = rows*cols;
        this.moles = startHiding();
//        this.game = new WAM(this.rows, this.cols);

//        this.moles = game.startHiding();

    }


    public synchronized void score(String scoreMsg)throws WAMException{
        String[] ids = scoreMsg.split(" ");

        if(moles[Integer.parseInt(ids[0])].getStatus()){
            scores[Integer.parseInt(ids[1])] += 2;
            this.hide(Integer.parseInt(ids[0]));
        }
        else {
            scores[Integer.parseInt(ids[1])] -= 1;
        }
        for (WAMPlayer player: players){
            player.sendScores(tallyScores());
        }
    }

    public Mole[] startHiding() {
        Mole[] moles = new Mole[numMoles];
        for (int i = 0; i < numMoles; i++) {
            moles[i] = new Mole(i, this);
            moles[i].start();
        }
        return moles;
    }

    public String tallyScores(){
        StringBuilder scores = new StringBuilder();
        scores.append(SCORE);
        for (int i = 0; i<players.length; i++){
            scores.append(" ");
            scores.append(this.scores[i]);
        }
        String scoreboard = scores.toString();
        return scoreboard;
    }

    public synchronized void popUp(int id)throws WAMException{
        for (WAMPlayer player: players){
            String moleUp = player.moleUp(id);
            score(moleUp);
        }
    }

    public synchronized void hide (int id) throws WAMException{
        moles[id].down();
        for (WAMPlayer player : players){
            player.moleDown(id);
        }
    }

    @Override
    public void run(){

//        boolean running = true;
        while(RUNNING){
            int currentTimeElapsed = 0;
            for (WAMPlayer player : players){
//                    player.score();
                try {
                    player.close();
                }catch (Exception e){}
            }
            if (currentTimeElapsed >= duration){
                RUNNING=false;
            }
            currentTimeElapsed++;
        }
    }

    private boolean timeUp(){
            int startTime = 0;
            while (startTime<duration){
                startTime++;
            }
        return false;
    }





}
