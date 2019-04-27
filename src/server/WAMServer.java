package server;

import common.WAMException;
import common.WAMProtocol;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *WAMServer is the server class for the Whack-A-Mole game and initializes the game by setting up the board, number of
 * players and the duration of the game.
 *
 * @author Liang, Albin
 * @author Souza, Saakshi
 */

public class WAMServer extends Thread implements WAMProtocol {

    //The server socket
    private ServerSocket server;

    //The number of rows of the game
    private int rows;

    //the number of columns of the game
    private int col;

    //The number of players
    private int players;

    //The duration of the game.
    private int duration;

    //A collection of the Player threads.
    private WAMPlayer[] WAMPlayers;


    /**
     * constructor for the server class.
     *
     * @param port port number
     * @param rows # of rows
     * @param col # of columns
     * @param players # of players
     * @param duration duration in seconds.
     * @throws IOException if the IO runs into an issue
     * @throws WAMException catches other exceptions.
     */
    public WAMServer (int port, int rows, int col, int players, int duration) throws IOException, WAMException {
        try {server = new ServerSocket(port);
            this.rows = rows;
            this.col = col;
            this.players = players;
            this.duration = duration;
            WAMPlayers = new WAMPlayer[players];
        }catch (IOException e){throw new WAMException(e);}
    }

    /**
     * A method to manual close the server socket.
     *
     * @throws IOException if it is not open to begin with.
     */
    public void close() throws IOException{
        server.close();
    }

    /**
     * The main method, it parses command line arguments and creates the WAM server based on those arguments. It ends
     * after starting the server thread.
     *
     * @param args the command line arguments
     * @throws IOException if it is unable to connect
     * @throws WAMException for other exceptions
     */
    public static void main(String[] args) throws IOException, WAMException{
        if (args.length != 5){//in the case of incorrect command line arguments.
            System.out.println("Usage: java WAMServer <game-port#> <#rows> <#columns> <#players> <game-duration-seconds>");
        }

        int port = Integer.parseInt(args[0]);
        int rows = Integer.parseInt(args[1]);
        int col = Integer.parseInt(args[2]);
        int players = Integer.parseInt(args[3]);
        if (players < 1){ //checks for players at least 1
            throw new WAMException("Minimum number of players cannot be less than 1");
        }
        int duration = Integer.parseInt(args[4]);
        if (duration < 10){//Game takes about 10 seconds to initialize
            throw new WAMException("Game Duration should not be less than 10");
        }
        WAMServer server = new WAMServer(port, rows , col, players, duration);
        Thread serverThread = new Thread(server);
        serverThread.start();


    }


    @Override
    public void run(){
        try {//Start a player thread for the number of players and store them in a collection.
            for (int i = 0; i < players; i++) {
                System.out.println("Waiting for player " + i);
                Socket playerSocket = server.accept();
                WAMPlayer player = new WAMPlayer(playerSocket);
                WAMPlayers[i] = player;
                player.welcome(rows, col, players, i);
                player.startListening();
            }
            WAMGame game = new WAMGame(rows, col, duration, WAMPlayers);//create a new WAMgame game.
            new Thread(game).start();//start game thread
            try {
                sleep(duration*1000);
            }catch (InterruptedException ie){}
        }catch (IOException e){ System.err.println("Invalid IO");}
        try {
            close();
        }catch (IOException ioe){}

        }


    }
