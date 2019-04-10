package client.gui;

import common.WAMException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.util.List;

public class WAMGUI extends Application implements Observer<WAMBoard> {
    private WAMClient client;
    private WAMBoard board;

    public void init(){
        try {
            List<String> args = getParameters().getRaw();
            String host = args.get(0);
            int port = Integer.parseInt(args.get(1));

            this.board=new WAMBoard();
            this.board.addObserver(this);
            client=new WAMClient(host, port, this.board);
        }
        catch(NumberFormatException e) {
            System.err.println(e);
            throw new RuntimeException(e);
        }
        catch(WAMException exc){System.err.println(exc);}
    }

    public void start(Stage stage){

    }

    public void stop(){
        client.close();
    }

    public void refresh(){}

    public void update(WAMBoard wamboard) {
        if ( Platform.isFxApplicationThread() ) {
            this.refresh();
        }
        else {
            Platform.runLater( () -> this.refresh() );
        }
    }

    public static void main(String[] args){
        if(args.length!=2){
            System.out.println("Usage: java WAMGUI host port");
            System.exit(-1);
        } else {
            Application.launch(args);
        }
    }
}
