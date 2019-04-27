package server;

import common.WAMException;
import common.WAMProtocol;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author Liang, Albin
 * @author Souza, Saakshi
 */

public class WAMServer extends Thread implements WAMProtocol {

    private ServerSocket server;
    private int rows;
    private int col;
    private int players;
    private int duration;
    private WAMPlayer[] WAMPlayers;


    public WAMServer (int port, int rows, int col, int players, int duration) throws IOException, WAMException {
        try {server = new ServerSocket(port);
            this.rows = rows;
            this.col = col;
            this.players = players;
            this.duration = duration;
            WAMPlayers = new WAMPlayer[players];
        }catch (IOException e){throw new WAMException(e);}
    }

    public void close() throws IOException{
        server.close();
    }

    public static void main(String[] args) throws IOException, WAMException{
        if (args.length != 5){
            System.out.println("Usage: java WAMServer <game-port#> <#rows> <#columns> <#players> <game-duration-seconds>");
        }

        int port = Integer.parseInt(args[0]);
        int rows = Integer.parseInt(args[1]);
        int col = Integer.parseInt(args[2]);
        int players = Integer.parseInt(args[3]);
        if (players < 1){
            throw new WAMException("Minimum number of players cannot be less than 1");
        }
        int duration = Integer.parseInt(args[4]);
        if (duration < 1){
            throw new WAMException("Game Duration should not be less than 1");
        }
        WAMServer server = new WAMServer(port, rows , col, players, duration);
        Thread serverThread = new Thread(server);
        serverThread.start();


    }
    @Override
    public void run(){
        try {
            for (int i = 0; i < players; i++) {
                System.out.println("Waiting for player " + i);
                Socket playerSocket = server.accept();
                WAMPlayer player = new WAMPlayer(playerSocket);
                WAMPlayers[i] = player;
                player.welcome(rows, col, players, i);
                player.startListening();
            }
            WAMGame game = new WAMGame(rows, col, duration, WAMPlayers);//implement game logic
            new Thread(game).start();//implement game thread here
            try {
                sleep(duration*1000);
            }catch (InterruptedException ie){}
        }catch (IOException e){ System.err.println("Invalid IO");}
        try {
            close();
        }catch (IOException ioe){}

        }


    }
