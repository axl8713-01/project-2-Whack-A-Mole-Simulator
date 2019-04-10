package client.gui;

import common.WAMProtocol;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class WAMClient {

    private Socket clientSocket;
    private Scanner networkIn;
    private PrintStream networkOut;
    private boolean proceed;

    public WAMClient(String host, int port)throws Exception{
        try {
            this.clientSocket = new Socket(host, port);
            this.networkIn = new Scanner(clientSocket.getInputStream());
            this.networkOut = new PrintStream(clientSocket.getOutputStream());
            this.proceed=true;

            String message = this.networkIn.next();
            int rows=this.networkIn.nextInt();
            int columns=this.networkIn.nextInt();
            int num_of_players=this.networkIn.nextInt();
            int player_num=this.networkIn.nextInt();
            if (!message.equals(WAMProtocol.WELCOME )) {
                throw new Exception("Expected Connect from server");
            }
            System.out.println("Connected to server " + this.clientSocket);
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }
    /**
    private synchronized boolean shouldGameProceed() {
        return this.proceed;
    }

    private synchronized void stop() {
        this.proceed = false;
    }

     public void wonGame() {
     System.out.println( '!' + GAME_WON );

     dPrint( "You won! Yay!" );
     this.board.gameWon();
     this.stop();
     }
    */

    public void close() {
        try {
            this.clientSocket.close();
        }
        catch( IOException e ) {
        }
        //this.game.close();
    }

    public void startListener() {
        new Thread(() -> this.run()).start();
    }

    public void run(){}
}
