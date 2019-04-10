package client.gui;

import common.WAMProtocol;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class WAMClient {

    private Socket clientSocket;
    private Scanner networkIn;
    private PrintStream networkOut;
    private boolean proceed;
    private WAMBoard board;

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
            this.board=new WAMBoard(rows, columns);
            if (!message.equals(WAMProtocol.WELCOME )) {
                throw new Exception("Expected Connect from server");
            }
            System.out.println("Connected to server " + this.clientSocket);
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    private synchronized boolean shouldGameProceed() {
        return this.proceed;
    }

    private synchronized void stop() {
        this.proceed = false;
    }

    public void wonGame() {
        System.out.println(" YOU WON! ");
        // this.board.wonGame();
        this.stop();
    }
    public void lostGame() {
        System.out.println(" YOU LOST! ");
        //  this.board.lostGame();
        this.stop();
    }

    public void tiedGame() {
        System.out.println(" TIED GAME! ");
        //  this.board.tiedGame();
        this.stop();
    }


    public void close() {
        try {
            this.clientSocket.close();
        }
        catch( IOException e ) {
        }
        //this.board.close();
    }

    public void error( String err ) {
        System.out.println(" ERROR: " + err );
        //   this.board.error( err );
        this.stop();
    }

    public void moleAppearance(int mole_num, boolean flag) {
        if (flag) {
            System.out.println(" MOLE " + mole_num + " UP ");
            this.board.moleAppearance(true);
        } else {
            System.out.println(" MOLE " + mole_num + " DOWN ");
            this.board.moleAppearance(false);
        }
    }

    public void startListener() {
        new Thread(() -> this.run()).start();
    }

    private void run() {
        while (this.shouldGameProceed()) {
            try {
                String proto_msg = this.networkIn.next();
                String args = this.networkIn.nextLine().trim();
                //ConnectFourNetworkClient.dPrint( "Net message in = \"" + request + '"' );

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
                    default:
                        System.err.println("Unrecognized request: " + proto_msg);
                        this.stop();
                        break;
                }
            }
            catch( NoSuchElementException nse ) {
                // Looks like the connection shut down.
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
}
