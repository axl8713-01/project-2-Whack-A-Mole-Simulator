package server;

import common.WAMException;

import java.io.IOException;
import java.util.ArrayList;

import static common.WAMProtocol.SCORE;
import static java.lang.Thread.sleep;

/**
 * The thread that handles all the game logic, keeps track of the time for the game, and handles all the moles.
 *
 * @author Liang, Albin
 * @author D'Souza, Saakshi
 */


public class WAMGame extends Thread {

    //the number of rows
    private int rows;

    //the number of columns
    private int cols;

    //the duration of the game in seconds
    private int duration;

    //boolean if the game is still going on or not.
    public boolean RUNNING = true;

    //the collection of players in the game.
    private WAMPlayer[] players;

    //the scores of all the players.
    private int[] scores;

    //The number of moles in the game
    private int numMoles;

    //a collection of all the mole threads.
    private Mole[] moles;


    public WAMGame(int rows, int cols, int duration, WAMPlayer... players) {

        this.rows = rows;
        this.cols = cols;
        this.duration = duration;
        this.players = players;
        for (WAMPlayer player : players){//informs all the players that this thread has started.
            player.start(this);
        }
        this.scores = new int[players.length];
        this.numMoles = this.rows * this.cols;
        Mole[] moles = new Mole[numMoles];
        this.moles = moles;
    }

    /**
     * score is called by the player threads whenever there is a whack message received from the players.
     *
     * @param scoreMsg the mole number and player number
     * @throws WAMException if there is an fatal error
     */
    public synchronized void score(String scoreMsg) throws WAMException {

        if (scoreMsg != "") {//if the players sent a whack
            String[] score = scoreMsg.split(" ");//split the line into mole id and player id.
            int id=Integer.valueOf(score[0]);//the mole id

            int p_no=Integer.valueOf(score[1]);// the player id


            if (moles[id].getStatus()) {//If the mole is up
                scores[p_no] += 2;// they get two points
                for (WAMPlayer player : players) {
                    player.sendScores(tallyScores());// let all the players know someone has scored
                }
                this.hide(id);//tell the mole to hide.
            } else {//otherwise the mole is down and they lose points.
                scores[p_no] -= 1;//lost a point for the mole being down.
                for (WAMPlayer player : players) {//update the players on the scores
                    player.sendScores(tallyScores());
                }
            }
        }
    }

    /**
     * startHiding initializes all the mole threads and updates the collection of moles attribute.
     */
    public void startHiding() {
        for (int i = 0; i < numMoles; i++) {
            moles[i] = new Mole(i, this);
            System.out.println("mole" + i + " spawned and hiding!");
            moles[i].start();
        }
    }

    /**
     * tallyScores is a helper function to convert the player scores into a string
     *
     * @return a string representation of all the player scores appended with spaces between the numbers.
     */
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

    /**
     * popUp is called by the moles to let the players know that a mole has come up.
     *
     * @param id the id of the mole that popped up
     * @throws WAMException if there is any error
     */
    public synchronized void popUp(int id) throws WAMException {
        for (WAMPlayer player : players) {
            player.moleUp(id);
        }
    }

    /**
     * hide is called when the mole goes down naturally or when whacked.
     *
     * @param id the id of the mole that popped up
     * @throws WAMException if there is any exceptions thrown.
     */
    public synchronized void hide(int id) throws WAMException {
        moles[id].down();
        for (WAMPlayer player : players) {
            player.moleDown(id);
        }
    }

    /**
     * endGame is the cleanup method that finalizes the score and calculates the winners/losers/ties and sends the
     * appropriate message to the player.
     *
     */
    public void endGame() {

        int highScore=scores[0];

        ArrayList<WAMPlayer> winners = new ArrayList<>();

        ArrayList<WAMPlayer> losers = new ArrayList<>();

        for (int i = 0; i < scores.length; i++) {
            if (scores[i] > highScore) {
                highScore = scores[i];//find the highest score
            }
        }

        for (int i = 0; i < scores.length; i++) {
            if (scores[i] == highScore) { //go through the scores again to find the highest ones.
                winners.add(players[i]);
            } else {//seperate the players into piles
                losers.add(players[i]);
            }
        }

        if (winners.size() == 1) {//if there is only one winner.
            winners.get(0).win();//they win
        } else {
            for (WAMPlayer player : winners) {
                player.tie();//otherwise these players have tied.
            }
        }

        for (WAMPlayer player : losers) {//send the lose messages out to the losers.
            player.lose();
        }


    }

    /**
     * The game thread starts a timer thread that sleeps for the duration of the game and waits for it finish then
     * begins the endgame process since the time is up.
     */
    @Override
    public void run() {
        startHiding();
        Thread timerThread = new Thread(() -> {
            try {
                sleep(duration * 1000);
                }catch (InterruptedException ie) {}
            });
        timerThread.start();
        try{
            timerThread.join();//don't unblock until timer thread has finished running.
            }catch(InterruptedException ie){}
        endGame();//begin endgame.
        for (WAMPlayer player : players) {
             player.setGameOff();//close the game and set the players attribute to false to let them know the game ended
            }
        System.out.println("END");
    }
}






