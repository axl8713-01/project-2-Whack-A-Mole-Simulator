package client.gui;

import common.WAMException;
import common.WAMProtocol;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * WAMClient is the controller of the MVC design pattern, it deals with communication between the view and server
 * and server to model.
 *
 * @author D'Souza, Saakshi
 * @author Liang, Albin
 */

public class WAMClient {

    //the logical end point with the server.
    private Socket clientSocket;

    //communication from the server
    private Scanner networkIn;

    //communication to the server
    private PrintStream networkOut;

    //boolean status used to check whether it is appropriate to continue to listen to the server while game is running.
    private boolean proceed;

    //the model that is observed by the view.
    private WAMBoard board;

    //the current player number that is sent from the server
    private int player_num;

    //the number of players that is sent from the server
    private int num_of_players;

    /**
     * the constructor for the controller, it takes in the hostname, port number and the model shared with the view.
     *
     * @param host the host name
     * @param port the port number of the host
     * @param board the model
     * @throws WAMException if there is a problem with connecting
     */
    public WAMClient(String host, int port, WAMBoard board)throws WAMException{
        try {
            this.clientSocket = new Socket(host, port);
            this.networkIn = new Scanner(clientSocket.getInputStream());
            this.networkOut = new PrintStream(clientSocket.getOutputStream());
            this.proceed=true;
            String message = this.networkIn.next();//receive the pertinent information from server.
            int rows=this.networkIn.nextInt();
            int columns=this.networkIn.nextInt();
            this.num_of_players=this.networkIn.nextInt();
            this.player_num=this.networkIn.nextInt();
            this.board=board;
            this.board.sendRnC(rows, columns);//send the rows and columns to the model
            if (!message.equals(WAMProtocol.WELCOME )) {
                throw new WAMException("Expected Connect from server");
            }
            System.out.println("Connected to server " + this.clientSocket);
        }
        catch(IOException e) {
            throw new WAMException(e);
        }
    }

    /**
     * A getter function that gets the number of players sent from the server
     * @return the total number of players playing the game
     */
    public int get_num_play(){
        return this.num_of_players;
    }


    /**
     * simple helper function to check whether the game is still running.
     *
     * @return true if the game is still on, false otherwise.
     */
    private synchronized boolean shouldGameProceed() {
        return this.proceed;
    }

    /**
     * lets the controller know to stop
     *
     */
    private synchronized void stop() {
        this.proceed = false;
    }

    /**
     * The game has ended, we won, call the stop method, begin clean up.
     *
     */
    public void wonGame() {
        System.out.println(" YOU WON! ");
        this.board.wonGame();
        this.stop();
    }

    /**
     * The game has ended, we lost, call the stop method, begin clean up.
     *
     */
    public void lostGame() {
        System.out.println(" YOU LOST! ");
        this.board.lostGame();
        this.stop();
    }

    /**
     * The game has ended, we tied, call the stop method, begin clean up.
     *
     */
    public void tiedGame() {
        System.out.println(" TIED GAME! ");
        this.board.tiedGame();
        this.stop();
    }

    /**
     * close out the controller by closing the socket, then call the models close method.
     *
     */
    public void close() {
        try {
            this.clientSocket.close();
        }
        catch( IOException e ) {
        }
        this.board.close();
    }

    /**
     * method for dealing with error messages from the server.
     *
     * @param err_msg the error message
     */
    public void error( String err_msg ) {
        System.out.println(" ERROR: " + err_msg );
        this.board.error();
        this.stop();
    }

    /**
     * moleAppearance is called whenever the server lets us know that there is mole movement. We then let the model
     * know that a mole has moved.
     *
     * @param mole_num the number of the mole
     * @param flag whether it is going up or down
     */
    public void moleAppearance(int mole_num, boolean flag) {
        if (flag) {
            System.out.println(" MOLE " + mole_num + " UP ");
            this.board.moleAppearance(mole_num, true);
        } else {
            System.out.println(" MOLE " + mole_num + " DOWN ");
            this.board.moleAppearance(mole_num, false);
        }
    }

    /**
     * the method called by the view to begin the controller thread.
     *
     */
    public void startListener() {
        new Thread(() -> this.run()).start();
    }

    /**
     * the main loop of the controller thread that deals with incoming traffic from the server. It parses the message
     * from server and deals with it appropriately. It runs until the proceed is false.
     *
     */
    private void run() {
        while (this.shouldGameProceed() && networkIn.hasNextLine()) {
            try {
                String proto_msg = this.networkIn.next();// the message from server
                String args = this.networkIn.nextLine().trim();

                switch ( proto_msg ) {

                    case WAMProtocol.GAME_WON:
                        wonGame();
                        break;
                    case WAMProtocol.GAME_LOST:
                        lostGame();
                        break;
                    case WAMProtocol.GAME_TIED:
                        tiedGame();
                        break;
                    case WAMProtocol.ERROR:
                        error( args );
                        break;
                    case WAMProtocol.MOLE_UP:
                        moleAppearance(Integer.parseInt(args), true);
                        break;
                    case WAMProtocol.MOLE_DOWN:
                        moleAppearance(Integer.parseInt(args), false);
                        break;
                    case WAMProtocol.SCORE:
                        sendScore(args);
                        break;
                    default:
                        System.err.println("Unrecognized request: " + proto_msg);
                        this.stop();
                        break;
                }
            }
            catch( NoSuchElementException nse ) {
                this.error( "Lost connection to server." );
                this.stop();
            }
            catch( Exception e ) {
                this.error( e.getMessage() + '?' );
                this.stop();
            }
        }
        this.close();
    }

    /**
     * Send the score recieved from the server to the board for updating
     * @param score the string containing the updated scores from the server
     */
    public void sendScore(String score){
        this.board.getScore(score);
}

    /**
     * sends server the whack message along with the mole number and the player number who whacked it
     * @param row the row number of the mole
     * @param col the column number of the mole
     */
    public void Whacked(int row, int col){
            networkOut.println(WAMProtocol.WHACK+" "+board.getMoleNum(row, col)+" "+player_num);
            networkOut.flush();
        }
    }
