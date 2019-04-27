package server;

import common.WAMException;
import common.WAMProtocol;

import java.io.Closeable;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

/**
 *  WAMPlayer is the middle person between the server and each of the players. There is one thread per player in the
 *  game.
 *
 * @author Liang, Albin
 * @author D'Souza, Saakshi
 */

public class WAMPlayer extends Thread implements WAMProtocol, Closeable {

    //the socket connection
    private Socket clientSocket;

    //the output from the players.
    private Scanner networkIn;

    //output from server to the player.
    private PrintStream networkOut;

    //boolean to let the player thread know when to stop
    private boolean gameOn=true;

    //the game being run by the server.
    private WAMGame game;



    /**
     * Constructor that takes a socket and creates the communication channel between the server and player.
     *
     * @param socket the logical end point.
     */
    public WAMPlayer (Socket socket) throws IOException{
        this.clientSocket = socket;
        try {
            networkIn = new Scanner(clientSocket.getInputStream()); //From Client Side
            networkOut = new PrintStream(clientSocket.getOutputStream());//To the client
        }catch (IOException e){}
    }

    /**
     * Welcome is the initial connection request sent to client
     *
     * @param rows The rows in the game
     * @param columns The columns in the game
     * @param players number of players
     * @param playerNum clients position
     */
    public void welcome(int rows, int columns, int players, int playerNum) {

        networkOut.println(WELCOME + " " + rows + " " + columns + " " + players + " " + playerNum);
    }

    /**
     * Method used by the game thread to share itself with all the players communication threads.
     *
     * @param game the WAMgame thread.
     */
    public void start(WAMGame game){
        this.game = game;
    }

    /**
     * mole Up is the server informing the clients that a mole has come out of it's hole.
     *
     * @param moleNum The unique number of the mole that came up.
     */
    public void moleUp(int moleNum){
        networkOut.println(MOLE_UP + " " + moleNum);
    }

    /**
     * moleDown is called when the server informs the client that a mole has gone into it's hole.
     *
     * @param moleNum the unique number of the mole that went into hiding.
     */
    public void moleDown(int moleNum){
        networkOut.println(MOLE_DOWN + " " + moleNum);
    }

    /**
     * sendScores sends all the player scores to the player
     *
     * @param scoreBoard the string representation of the score.
     */
    public synchronized void sendScores(String scoreBoard){
        networkOut.println(scoreBoard);
    }

    /**
     * win lets the player know that they won the game.
     */
    public void win(){
        networkOut.println(WAMProtocol.GAME_WON);
        this.close();
    }

    /**
     * lose lets the player know that they have lost the game.
     */
    public void lose(){
        networkOut.println(WAMProtocol.GAME_LOST);
        this.close();
    }

    /**
     * tie lets the player know that they tied the game with other players.
     */
    public void tie(){
        networkOut.println(WAMProtocol.GAME_TIED);
        this.close();
    }

    /**
     * called in the case therer is an error and lets the player know. It then closes the socket.
     */
    public void error(){
        networkOut.println(ERROR);
        this.close();
    }

    /**
     * Starts the player thread and is called by the server when the threads are initialized.
     */
    public void startListening(){ new Thread(() -> this.run()).start();}


    /**
     * The player thread runs until the game has ended as indicated by the boolean attribute gameOn. While it is
     * active, it will be constantly listening for whacks from the player and it then parses it and returns it
     * to the game thread.
     */
    @Override
    public void run() {
        while (gameOn) {
            if (networkIn.hasNextLine()) {
                String whack = networkIn.nextLine();
                if (whack.startsWith(WHACK)){
                    String[] tokens = whack.split(" ");
                    if(tokens.length == 3){
                       try {
                           game.score(tokens[1] +" "+ tokens[2]);
                       }catch (WAMException we){}
                    }
                }
                else {
                    error();
                }
            }

        }
        close();
    }

    /**
     * setGameoff is called when the duration is over and lets the player thread know to stop.
     */
    public void setGameOff(){
        this.gameOn=false;
    }

    /**
     * close tidys up and closes the player socket at the end.
     */
    @Override
    public void close(){
        try {
            clientSocket.close();
        }catch (IOException ioe){System.err.println("Something went wrong");}
    }

}
