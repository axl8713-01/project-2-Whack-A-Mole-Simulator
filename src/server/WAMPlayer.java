package server;

import common.WAMException;
import common.WAMProtocol;

import java.io.Closeable;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;
import java.util.StringJoiner;



public class WAMPlayer implements WAMProtocol, Closeable {

    private Socket clientSocket;
    private Scanner networkIn;
    private PrintStream networkOut;


    /**
     *
     */
    public WAMPlayer (Socket socket) throws IOException{
        this.clientSocket = socket;
        try {
            networkIn = new Scanner(clientSocket.getInputStream()); //From Server Side
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
     * mole Up is the server informing the clients that a mole has come out of it's hole.
     *
     * @param moleNum The unique number of the mole that came up.
     */
    public String moleUp(int moleNum) throws WAMException{
        networkOut.println(MOLE_UP + moleNum);
        String whack = networkIn.nextLine();

        if (whack.startsWith(WHACK)){
            String[] tokens = whack.split(" ");
            if(tokens.length == 3){
                return tokens[1] + tokens[2];
            }
            else {
                throw new WAMException("Something went wrong" + whack);
            }
        }
        else {
            throw new WAMException("Something went wrong" + whack);
        }
    }

    public void moleDown(int moleNum){
        networkOut.println(MOLE_DOWN + moleNum);
    }

//    public void scores()

    @Override
    public void close(){
        try {
            clientSocket.close();
        }catch (IOException ioe){System.err.println("Something went wrong");}
    }

}
