package client.gui;

import common.WAMException;
import common.WAMProtocol;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.List;


/**
 *WAMGUI represents the view in the model-view-controller(MVC) design pattern
 *  and it is the interface in which users use to play the game.
 *
 *
 * @author D'Souza, Saakshi
 * @author Liang, Albin
 */

public class WAMGUI extends Application implements Observer<WAMBoard> {
    //client is the controller in the MVC design pattern
    private WAMClient client;

    //board is the model in the MVC design pattern.
    private WAMBoard board;

    //gridPane is used as the backbone of the drawing the user can see.
    private GridPane gridPane;

    //status is a label to represent the status of the game(currently just a placeholder)
    private Label status;

    private Label score;

    //private ImageView hole=new ImageView(new Image(getClass().getResourceAsStream("hole.png")));
    //private ImageView mole=new ImageView(new Image(getClass().getResourceAsStream("mole.png")));

    //created is used to tell if the board has been created or not initially.
    private boolean created=false;

    //stage is the stage on which the board is drawn.
    private Stage stage;


    /**
     * init is the always run first upon running the JavaFX thread,
     * it constructs the controller and model and then adds this class to the list of observers
     * lastly it starts the controller thread used to communicate with the server.
     *
     *
     */
    public void init(){
        try {
            List<String> args = getParameters().getRaw();//retrieve the command line arguments from main.
            String host = args.get(0); //the host name is provided.
            int port = Integer.parseInt(args.get(1));//the port number is the 2nd indexed argument

            this.board=new WAMBoard();//create a new board
            this.board.addObserver(this); //add itself to the board as an observer
            client=new WAMClient(host, port, this.board);//start up the controller
            client.startListener();//start controller thread

        }
        catch(NumberFormatException e) {
            System.err.println(e);
            throw new RuntimeException(e);
        }
        catch(WAMException exc){System.err.println(exc);}
    }

    /**
     * following init, start is run, with the stage passed in, since the gridpane is constructed after init has been
     * run
     *
     * @param stage the stage to be shown.
     */
    public void start(Stage stage){
        this.stage=stage;
       // client.startListener();
    }


    /**
     * createGUI is called to draw the board initially, it creates a new gridpane calling the helper function,
     * then creates a VBOX and places the gridpane and label in the VBOX. The scene is set to the VBOX and the title
     * is set. Lastly, the stage is shown.
     *
     */
    public void createGUI(){
        GridPane gridPane=makeGridPane();
        status=new Label();
        score=new Label();
        HBox hBox=new HBox(status, score);
        VBox vBox=new VBox(hBox, gridPane);
        Scene scene=new Scene(vBox);
        stage.setTitle(" Whack A Mole!");
        stage.setScene(scene);
        stage.setMinWidth(500);
        stage.show();
    }

    /**
     * Helper function to create the initial gridPane
     *
     * @return a gridpane that is the keystone of the drawing
     */
    public GridPane makeGridPane(){
        gridPane=new GridPane();
        gridPane.setGridLinesVisible(true);
        for(int i=0; i<board.COLS; i++){
            for(int j=0; j<board.ROWS; j++){
                Button button=new Button();
                button.setPrefSize(150,150);
                button.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
               // button.setGraphic(hole);
                gridPane.add(button, i,j);
            }
        }
        return gridPane;
    }

    /**
     * called when the JavaFX thread ends, it closes the client connection on the controller.
     */
    public void stop(){
        client.close();
    }

    /**
     * modifyGUI is called by the refreshing method for our gui and will change the button image to match the state
     * of the mole in the position on the grid.
     */
    public void modifyGUI() {
        for (int i = 0; i < board.COLS; i++) {
            for (int j = 0; j < board.ROWS; j++) {
                int finalI = i;
                int finalJ = j;
                Button b = new Button();
                b.setPrefSize(150,150);
                if (this.board.getMoleHole(i, j)) {//if true, mole is up
                    b.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));
                   // b.setGraphic(mole);
                    b.setOnAction(actionEvent -> {
                        client.Whacked(finalJ, finalI);
                        this.score.setText(this.board.score);
                    });
                    gridPane.add(b, i, j);
                }
                else {//else mole is down
                    b.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
                  //  b.setGraphic(hole);
                    b.setOnAction(actionEvent -> {
                       client.Whacked(finalJ,finalI);
                       this.score.setText(this.board.score);
                    });
                    gridPane.add(b, i, j);
                }
            }
        }
        if(!this.board.proceed){
            status.setText(board.result+"");
        }
        }


    /**
     * endgame wakes up all the other threads after the game has ended. Placeholder currently*
     */
    public void endGame(){
        this.notifyAll();
    }


    /**
     * update is called by the model whenever there is an update to the model states.
     *
     * @param wamboard is the drawing which the model passes to let the view know what to update.
     */
    public void update(WAMBoard wamboard) {

            Platform.runLater( () -> {//The model cannot change the nodes in the JavaFX thread directly.
                if(!created) {//if it's the first time, create the board.
                    createGUI();
                    created=true;//it's been created, set it to true.
                }
                else{
                    modifyGUI();
                }
            });

    }

    /**
     * checks whether or not the command line args were of the correct length, then launches the JavaFX thread.
     *
     * @param args the command line arguments.
     */
    public static void main(String[] args){
        if(args.length!=2){
            System.out.println("Usage: java WAMGUI host port");
            System.exit(-1);
        } else {
            Application.launch(args);
        }
    }
}
