package server;

import common.WAMException;

import java.io.IOException;
import java.util.ArrayList;

import static common.WAMProtocol.SCORE;
import static java.lang.Thread.sleep;

/**
 *
 * @author Liang, Albin
 * @author Souza, Saakshi
 */


public class WAMGame extends Thread {

    private int rows;

    private int cols;

    private int duration;

    public boolean RUNNING = true;
//    private WAM game;

    private WAMPlayer[] players;

    private int[] scores;

    private int numMoles;

    private Mole[] moles;


    public WAMGame(int rows, int cols, int duration, WAMPlayer... players) {

        this.rows = rows;
        this.cols = cols;
        this.duration = duration;
        this.players = players;
        for (WAMPlayer player : players){
            player.start(this);
        }
        this.scores = new int[players.length];
        this.numMoles = rows * cols;
        Mole[] moles = new Mole[numMoles];
        this.moles = moles;
//        this.game = new WAM(this.rows, this.cols);

//        this.moles = game.startHiding();

    }


    public synchronized void score(String scoreMsg) throws WAMException {

        if (scoreMsg != "") {
            String[] score = scoreMsg.split(" ");
            int id=Integer.valueOf(score[0]);

            int p_no=Integer.valueOf(score[1]);

            if (moles[id].getStatus()) {
                scores[p_no] += 2;
                for (WAMPlayer player : players) {
                    player.sendScores(tallyScores());
                }
                this.hide(id);
            } else {
                scores[p_no] -= 1;
                for (WAMPlayer player : players) {
                    player.sendScores(tallyScores());
                }

            }

        }
    }

    public void startHiding() {
        for (int i = 0; i < numMoles; i++) {
            moles[i] = new Mole(i, this);
            System.out.println("mole" + i);
            moles[i].start();
        }
    }

    public String tallyScores() {
        StringBuilder scores = new StringBuilder();
        scores.append(SCORE);
        for (int i = 0; i < players.length; i++) {
            scores.append(" ");
            scores.append(this.scores[i]);
        }
        String scoreboard = scores.toString();
        return scoreboard;
    }

    public synchronized void popUp(int id) throws WAMException {
        for (WAMPlayer player : players) {
            player.moleUp(id);
            // score(moleUp);
        }
    }

    public synchronized void hide(int id) throws WAMException {
        moles[id].down();
        for (WAMPlayer player : players) {
            player.moleDown(id);
        }
    }

    public void changeState(){
        this.RUNNING = false;
    }

    public void endGame() {

        int highScore=scores[0];

        ArrayList<WAMPlayer> winners = new ArrayList<>();

        ArrayList<WAMPlayer> losers = new ArrayList<>();

        for (int i = 0; i < scores.length; i++) {
            if (scores[i] > highScore) {
                highScore = scores[i];

            }
        }

        for (int i = 0; i < scores.length; i++) {
            if (scores[i] == highScore) {
                winners.add(players[i]);
            } else {
                losers.add(players[i]);
            }
        }

        if (winners.size() == 1) {
            winners.get(0).win();
        } else {
            for (WAMPlayer player : winners) {
                player.tie();
            }
        }

        for (WAMPlayer player : losers) {
            player.lose();
        }


    }

//    public void startTime() {
//        Thread timerThread = new Thread(() -> {
//            try {
//                sleep(duration * 1000);
//            } catch (InterruptedException ie) {
//            }
//        });
//        Thread endTimer = new Thread(() -> {
//            try {
//                timerThread.join();
//                changeState();
//            } catch (InterruptedException ie) {
//            }
//        });
//        timerThread.start();
//        endTimer.start();
//    }



    @Override
    public void run() {
        startHiding();
       // startTime();
                Thread timerThread = new Thread(() -> {
                    try {
                        sleep(duration * 1000);
                            } catch (InterruptedException ie) {
                        }
                    });

                    timerThread.start();
                    try{
                    timerThread.join();} catch(InterruptedException ie){}
            endGame();
         for (WAMPlayer player : players) {
             player.setGameOff();
         }
         System.out.println("END");
    }
}






