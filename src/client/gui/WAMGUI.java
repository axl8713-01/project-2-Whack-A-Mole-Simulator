package client.gui;

import common.WAMException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.List;

public class WAMGUI extends Application implements Observer<WAMBoard> {
    private WAMClient client;
    private WAMBoard board;
    private GridPane gridPane;
    private Label status;
    private boolean created=false;
    private Stage stage;

    public void init(){
        try {
            List<String> args = getParameters().getRaw();
            String host = args.get(0);
            int port = Integer.parseInt(args.get(1));

            this.board=new WAMBoard();
            this.board.addObserver(this);
            client=new WAMClient(host, port, this.board);
            client.startListener();

        }
        catch(NumberFormatException e) {
            System.err.println(e);
            throw new RuntimeException(e);
        }
        catch(WAMException exc){System.err.println(exc);}
    }
    public void start(Stage stage){
        this.stage=stage;
       // client.startListener();
    }

    public void createGUI(){
        GridPane gridPane=makeGridPane();
        status=new Label();
        VBox vBox=new VBox(gridPane, status);
        Scene scene=new Scene(vBox);
        stage.setTitle(" Whack A Mole!");
        stage.setScene(scene);
        stage.setMinWidth(500);
        stage.show();
    }

    public GridPane makeGridPane(){
        gridPane=new GridPane();
        gridPane.setGridLinesVisible(true);
        for(int i=0; i<board.COLS; i++){
            for(int j=0; j<board.ROWS; j++){
                Button button=new Button();
                button.setPrefSize(150,150);
                button.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
                gridPane.add(button, i,j);
            }
        }
        return gridPane;
    }

    public void stop(){
        client.close();
    }

    public void modifyGUI() {
        for (int i = 0; i < board.COLS; i++) {
            for (int j = 0; j < board.ROWS; j++) {
                Button b = new Button();
                b.setPrefSize(150,150);
                if (this.board.getMoleHole(i, j)) {
                    b.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));
                    gridPane.add(b, i, j);
                }
                else {
                    b.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
                    gridPane.add(b, i, j);
                }
            }
        }
//        WAMBoard.Result res=board.result;
//        switch(res){
//            case WON:
//                this.status.setText(" YOU WON ");
//                //endGame();
//                break;
//            case LOST:
//                this.status.setText(" YOU LOST ");
//                //endGame();
//                break;
//            case TIE:
//                this.status.setText(" TIED GAME ");
//                //endGame();
//                break;
//            default:
//                break;
//        }
    }

    public void endGame(){
        this.notifyAll();
    }

    public void update(WAMBoard wamboard) {

            Platform.runLater( () -> {
                if(!created) {
                    createGUI();
                    created=true;
                }
                else{
                    modifyGUI();
                }
            });

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
