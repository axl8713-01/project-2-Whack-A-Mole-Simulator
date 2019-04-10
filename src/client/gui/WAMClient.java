package client.gui;

import common.WAMException;
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

    public WAMClient(String host, int port, WAMBoard board)throws WAMException{
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
            this.board=board;
            board.sendRnC(rows, columns);
            if (!message.equals(WAMProtocol.WELCOME )) {
                throw new WAMException("Expected Connect from server");
            }
            System.out.println("Connected to server " + this.clientSocket);
        }
        catch(IOException e) {
            throw new WAMException(e);
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
        this.board.close();
    }

    public void error( String err_msg ) {
        System.out.println(" ERROR: " + err_msg );
      //this.board.error( err_msg );
        this.stop();
    }

    public void moleAppearance(int mole_num, boolean flag) {
        if (flag) {
            System.out.println(" MOLE " + mole_num + " UP ");
            this.board.moleAppearance(mole_num, true);
        } else {
            System.out.println(" MOLE " + mole_num + " DOWN ");
            this.board.moleAppearance(mole_num, false);
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
