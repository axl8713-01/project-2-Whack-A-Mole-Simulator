package server;

import common.WAMException;
import common.WAMProtocol;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class WAMServer implements WAMProtocol, Runnable {

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
        WAMServer server = new WAMServer(port, rows , col, players, duration);
        server.run();

    }
    @Override
    public void run(){
        try {
            for (int i=0 ; i<players; i++){
                System.out.println("Waiting for player " + i);
                Socket playerSocket = server.accept();
                WAMPlayer player = new WAMPlayer(playerSocket);
                WAMPlayers[i]= player;
                player.welcome(rows,col,players,i);
            }
//            WAMGame game = new WAMGame(rows, col, duration);//implement game logic
//            new Thread(game).run()//implement game thread here

        }catch (IOException e){System.err.println("Invalid IO");}
//        catch (WAMException e){//will be thrown by the game logic
//            System.err.println("Failed to connect to clients.");
//            e.printStackTrace();
//        }



    }
}
