
package client.gui;

import java.util.List;

public class WAMBoard {
    public enum Mole{
        MOLE_UP,
        MOLE_DOWN
    }

    /**public enum Status{
     WHACKED,
     NOT_WHACKED
     }*/
    public Mole[][] board;

    public int ROWS;
    public int COLS;

    public WAMBoard( int rows, int columns){
        this.ROWS=rows;
        this.COLS=columns;
        this.board=new Mole[ROWS][COLS];

        for(int i=0; i<ROWS; i++){
            for( int j=0; j<COLS; j++){
                this.board[i][j]= Mole.MOLE_DOWN;
            }
        }
    }

    public void moleAppearance(boolean flag){
        if(flag){

        }
    }

}
