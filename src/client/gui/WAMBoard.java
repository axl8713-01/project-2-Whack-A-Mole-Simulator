
package client.gui;

import java.util.LinkedList;
import java.util.List;

public class WAMBoard {

    public enum Result{
        WON,
        LOST,
        TIE,
        ERROR
    }
    /**public enum Status{
     WHACKED,
     NOT_WHACKED
     }*/
    public boolean[][] board;

    public int ROWS;
    public int COLS;
    public Result result;

    private List<Observer<WAMBoard>> observers;

    public void sendRnC(int r, int c){
        this.ROWS=r;
        this.COLS=c;
    }

    public WAMBoard(){
        this.observers = new LinkedList<>();
        this.ROWS=0;
        this.COLS=0;
        this.board=new boolean[ROWS][COLS];

        for(int i=0; i<ROWS; i++){
            for( int j=0; j<COLS; j++){
                this.board[i][j]= false;
            }
        }
    }

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
    public void alert(){
        for (Observer<WAMBoard> observe: this.observers ) {
            observe.update(this);
        }
    }

    public void addObserver(Observer<WAMBoard> observer) {
        this.observers.add(observer);
    }

    public int getMoleNum(int row, int column){
        return row*COLS+column;
    }

    public int getRow(int mole_num){
        return mole_num/COLS;
    }
    public int getCol(int mole_num){
        return mole_num%COLS;
    }

    public boolean getMoleHole(int c, int r) {
        return this.board[r][c];
    }


    public void wonGame() {
        this.result = Result.WON;
        alert();
    }

/**
    public void lostGame() {
        this.result = Result.I_LOST;
        alert();
    }

    public void tiedGame() {
        this.result = Result.TIE;
        alert();

     public void error( String err_msg){
     this.result = Result.ERROR;
     this.status.setMessage(arguments);
     alert();
     }
    }*/

    public void close() {
        alert();
    }


}
