package server;

import common.WAMException;

import java.io.IOException;

/**
 *
 * @author Liang, Albin
 * @author Souza, Saakshi
 */


public class WAMGame implements Runnable {

    private int rows;

    private int cols;

    private int duration;

    private WAM game;

    private WAMPlayer[] players;



    public WAMGame(int rows, int cols, int duration, WAMPlayer... players){

        this.rows = rows;
        this.cols = cols;
        this.duration=duration;
        this.players = players;
        this.game = new WAM(rows, cols);

    }

    @Override
    public void run(){

        boolean running = true;
        while(running){
            int currentTimeElapsed = 0;
            game.startHiding();
            for (WAMPlayer player : players){
//                    player.score();
                try {
                    player.close();
                }catch (Exception e){}
            }
            if (currentTimeElapsed >= duration){
                running=false;
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
