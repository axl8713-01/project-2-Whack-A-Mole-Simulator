package server;

import common.WAMException;

import java.io.IOException;
import java.util.ArrayList;

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


    public WAMGame(int rows, int cols, int duration, WAMPlayer... players) {

        this.rows = rows;
        this.cols = cols;
        this.duration = duration;
        this.players = players;
        this.scores = new Integer[players.length];
        this.numMoles = rows * cols;
        this.moles = startHiding();
//        this.game = new WAM(this.rows, this.cols);

//        this.moles = game.startHiding();

    }


    public synchronized void score(String scoreMsg) throws WAMException {

        if (scoreMsg != "") {
            String[] ids = scoreMsg.split(" ");
            if (moles[Integer.parseInt(ids[0])].getStatus()) {
                scores[Integer.parseInt(ids[1])] += 2;
                this.hide(Integer.parseInt(ids[0]));
            } else {
                scores[Integer.parseInt(ids[1])] -= 1;
            }
            for (WAMPlayer player : players) {
                player.sendScores(tallyScores());
            }
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
            String moleUp = player.moleUp(id);
            score(moleUp);
        }
    }

    public synchronized void hide(int id) throws WAMException {
        moles[id].down();
        for (WAMPlayer player : players) {
            player.moleDown(id);
        }
    }


    public void endGame(){

        int highScore = 0;

        ArrayList<WAMPlayer> winners = new ArrayList<>();

        ArrayList<WAMPlayer> losers = new ArrayList<>();

        for (int i = 0; i < scores.length; i++){
            if (scores[i] > highScore){
                highScore = scores[i];

            }
        }

        for (int i = 0; i < scores.length; i++){
            if (scores[i] == highScore){
                winners.add(players[i]);
            }
            else {
                losers.add(players[i]);
            }
        }

        if (winners.size() == 1){
            winners.get(0).win();
        }else{
            for (WAMPlayer player : winners){
                player.tie();
            }
        }

        for (WAMPlayer player : losers){
            player.lose();
        }
    }

    @Override
    public void run() {
        while (RUNNING) {

        }
        endGame();
        for(WAMPlayer player : players){
            player.setGameOff();
        }
    }
}






