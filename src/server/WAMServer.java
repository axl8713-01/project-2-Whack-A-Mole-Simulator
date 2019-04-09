package server;

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
    private


    public WAMServer (int port, int rows, int col, int players, int duration) throws IOException {
        try {server = new ServerSocket(port);
            this.rows = rows;
            this.col = col;
            this.players = players;
            this.duration = duration;
        }catch (IOException e){}
    }

    public static void main(String[] args) throws IOException{
        if (args.length != 5){
            System.out.println("Usage: java WAMServer <game-port#> <#rows> <#columns> <#players> <game-duration-seconds>");
        }

        int port = Integer.parseInt(args[0]);
        int rows = Integer.parseInt(args[1]);
        int col = Integer.parseInt(args[2]);
        int players = Integer.parseInt(args[3]);
        int duration = Integer.parseInt(args[4]);
        WAMServer server = new WAMServer(port, rows , col, players, duration);
        server.run();

    }

    public void run(){
        try {
            for (int i=0 ; i<players; i++){
                System.out.println("Waiting for player " + i);
                Socket playerSocket = server.accept();
                WAMPlayer player = new WAMPlayer();
            }
        }catch (IOException e){}
    }
}
