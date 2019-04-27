
package client.gui;

import java.util.LinkedList;
import java.util.List;

/**
 * WAMBoard represents the model in the MVC design pattern. It communicates with the view about updates sent from the
 * controller from the server.
 *
 *
 * @author D'Souza, Saakshi
 * @author Liang, Albin
 */

public class WAMBoard {

    /**
     * enum for the results that would end the game.
     */
    public enum Result{
        WON,
        LOST,
        TIE,
        ERROR
    }


    //The individual mole holes
    public boolean[][] board;

    //the row of the moles
    public int ROWS;

    //the columns of the moles.
    public int COLS;

    //the results that end the game.
    public Result result;

    //the score of all the players
    public String score;

    //a variable to check if the game is on or not
    public boolean proceed=true;

    //the list of observers, in this case the WAMGUI.
    private List<Observer<WAMBoard>> observers;


    /**
     * sendRnC is called by the controller to let the model know how many rows and columns of moles to hold
     * then initializes all the moles states to down by default.
     *
     * @param r the rows being passed from the controller.
     * @param c the columns being passed from the controller.
     */
    public void sendRnC(int r, int c){
        this.ROWS=r;
        this.COLS=c;
        this.board=new boolean[ROWS][COLS];
        for(int i=0; i<ROWS; i++){
            for( int j=0; j<COLS; j++){
                this.board[i][j]= false;//down state of the moles.
            }
        }
    }

    /**
     * Constructs the model, initially constructing an empty list to hold the observers and rows and cols are zeroed
     * out.
     *
     */
    public WAMBoard(){
        this.observers = new LinkedList<>();
        this.ROWS=0;
        this.COLS=0;
        score="";

    }

    /**
     * moleAppearance is called by the controller to let the model know that a mole has come out of it's hiding hole
     * it takes in the mole_num and the controller will parse whether it is a mole going up or down represented by
     * true or false.
     *
     * @param mole_num the unique number of the mole
     * @param flag the state of the mole(true : up, false : down)
     */
    public void moleAppearance(int mole_num, boolean flag){
        int r=getRow(mole_num);
        int c=getCol(mole_num);
        if(flag){
            this.board[r][c]=flag;
            alert();
        }
        else{
            this.board[r][c]=flag;
            alert();
        }
    }

    /**
     * alert is the model letting all it's observers (view) know that something has changed.
     *
     */
    public void alert(){
        for (Observer<WAMBoard> observe: this.observers ) {
            observe.update(this);
        }
    }

    /**
     * addObserver is called to add the view as an observer to this model.
     *
     * @param observer the observer of this model.
     */
    public void addObserver(Observer<WAMBoard> observer) {
        this.observers.add(observer);
    }

    /**
     * gets the mole number when given rows and column, to be used later on when events are handled.
     *
     * @param row the row of the mole
     * @param column the column of the mole
     * @return the unique number of the individual mole.
     */
    public int getMoleNum(int row, int column){
        return row*COLS+column;
    }


    /**
     * gets the row of a specific mole
     *
     * @param mole_num the specific mole's number
     * @return the row of the mole
     */
    public int getRow(int mole_num){
        return mole_num/COLS;
    }

    /**
     * gets the column of a specific mole
     *
     * @param mole_num the specific mole's number
     * @return the column of the mole
     */
    public int getCol(int mole_num){
        return mole_num%COLS;
    }


    /**
     * returns the state of the mole(up or down) at row and col.
     *
     * @param c the column of the hole
     * @param r the row of the hole
     * @return the state of the mole in this hole.
     */
    public boolean getMoleHole(int c, int r) {
        return this.board[r][c];
    }


    /**
     * wonGame is called when the game is won and lets the observers know.
     *
     */
    public void wonGame() {
        this.result = Result.WON;
        this.proceed=false;
        alert();
    }

    /**
     * lostGame is called when the game is lost and lets the observers know.
     */
    public void lostGame() {
        this.result = Result.LOST;
        this.proceed=false;
        alert();
    }

    /**
     * tiedGame is called when the game is tied and lets the observers know.
     */
    public void tiedGame() {
        this.result = Result.TIE;
        this.proceed=false;
        alert();
    }

    /**
     * error is called when there was an error in the game and lets the observers know.
     */
    public void error(){
     this.result = Result.ERROR;
     this.proceed=false;
     alert();
    }

    public void getScore(String score){
        this.score=score;
    }

    /**
     * close out the model, and alerts the view one last time.
     *
     */
    public void close() {
        alert();
    }


}
