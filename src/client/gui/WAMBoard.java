
package client.gui;

import java.util.LinkedList;
import java.util.List;

public class WAMBoard {

    /**public enum Status{
     WHACKED,
     NOT_WHACKED
     }*/
    public boolean[][] board;

    public int ROWS;
    public int COLS;

    private List<Observer<WAMBoard>> observers;


    public WAMBoard( int rows, int columns){
        this.observers = new LinkedList<>();
        this.ROWS=rows;
        this.COLS=columns;
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

    public boolean getMoleHole(int r, int c) {
        return this.board[r][c];
    }

    /**
    public void wonGame() {
        this.status = Status.I_WON;
        alert();
    }

    public void lostGame() {
        this.status = Status.I_LOST;
        alert();
    }

    public void tiedGame() {
        this.status = Status.TIE;
        alert();
    }*/

    public void close() {
        alert();
    }
}
