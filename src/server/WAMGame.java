package server;

import common.WAMException;

import java.io.IOException;


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

    }

    @Override
    public void run(){
        //accept player whacks here?
        game = new WAM(rows, cols);
        while(true){
            game.startHiding();
            if (timeUp()){
                for (WAMPlayer player : players){
//                    player.score();
                    try {

                        player.close();
                    }catch (Exception e){}
                }break;
            }
            else {
                break;
            }
        }
    }

    private boolean timeUp(){
            int startTime = 0;
            while (startTime<duration){
                startTime++;
            }

    }




}
