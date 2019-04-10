
package client.gui;

import java.util.List;

public class WAMBoard {

    /**public enum Status{
     WHACKED,
     NOT_WHACKED
     }*/
    public boolean[][] board;

    public int ROWS;
    public int COLS;

    public WAMBoard( int rows, int columns){
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
           // alertObservers();
        }
        else{
            this.board[r][c]=flag;
           // alertObservers();
        }
    }
   /** public void alertObservers(){
        for (Observer<WAMBoard> obs: this.observers ) {
            obs.update(this);
        }
    }*/

    public int getMoleNum(int row, int column){
        return row*COLS+column;
    }

    public int getRow(int mole_num){
        return mole_num/COLS;
    }
    public int getCol(int mole_num){
        return mole_num%COLS;
    }



}
