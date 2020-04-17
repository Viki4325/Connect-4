import java.util.Arrays;
import java.util.Random;

public class AI implements Player
{
    //Game Logic pointer
    private GameLogic theLogic;

    //the board itself
    private Status[][] board;
    //board size
    private int boardSize;
    //move made by opponent
    private int lastHumanMove;
    private int lastHumanRowMove;
    //move made by AI player
    private int AIMoveMade;

    public AI() {}

    public void lastMove(int col)
    {
        //System.out.println("LastMove of the AI made");
        lastHumanMove=col;
        //To keep our copy of the board updated, we have to insert the human chip
        if(lastHumanMove!=-1)
        {
            insertHumanInBoard(lastHumanMove);
        }

        makeMove();
        while(!verifyCol(AIMoveMade))
        {
            makeMove();
        }

        //now insert the AI chip into our board copy
        insertAIInBoard(AIMoveMade);
        theLogic.setAnswer(AIMoveMade);

    }


    public void setInfo(int size, GameLogic gl)
    {
        boardSize=size;
        theLogic=gl;
        //Initialize the board, when this info is set.
        board=new Status[boardSize][boardSize];
        //fill the same board
        fillBoard();
    }


    public void gameOver(Status winner)
    { }

    private void fillBoard()
    {
        for (Status[] s : board) {
            Arrays.fill(s, Status.NEITHER);
        }
    }

    /**
     * drop - a private helper method that finds the position of a marker
     * when it is dropped in a column.
     * @param col the column where the piece is dropped
     * @return the row where the piece lands
     */
    private int drop(int col)
    {
        int posn = 0;
        while (posn < board.length && board[posn][col] == Status.NEITHER) {
            posn ++;
        }
        return posn-1;
    }
    /*This method is used to insert the human chips into the location, "Opponents Move" */
    private void insertHumanInBoard(int col)
    {
        lastHumanRowMove=drop(col);
        board[lastHumanRowMove][col] = Status.ONE;
    }

    /*This method is used to insert the AI chips into the location, "AI MoveMade"
    * This method is called after the AI has decided to make a move*/
    private void insertAIInBoard(int col)
    {
        int row=drop(col);
        board[row][col] = Status.TWO;
    }


    /*Scans through the whole board to see if there is any vertical chance of winning for the human(defensive)
    * It also scans to see if there is any vertical chance of winning for the AI player(Offensive)
    * It returns an array with the column where to play, index 0-> rep column where it should play to be defensive*/
    private int[] verticalCheck()
    {
       int[] count = new int[2];
       Arrays.fill(count,-1);

       for(int col = 0; col < boardSize ; col++)
       {
           for (int row = 1; row <= boardSize-3; row++)
           {
               if (board[row][col] == Status.ONE && board[row][col] == board[row + 1][col] && board[row ][col] == board[row+2][col] && board[row-1 ][col] == Status.NEITHER)
               {
                   count[0]=col;
               }else if(board[row][col] == Status.TWO && board[row][col] == board[row + 1][col] && board[row ][col] == board[row+2][col] && board[row-1 ][col] == Status.NEITHER)
               {
                   count[1]=col;
               }
           }
       }
       return count;
    }

    /*Scans through the whole board to see if there is any horizontal chance of winning for the human
     * It also scans to see if there is any horizontal chance of winning for the AI plyer
     *  It returns an array with the column where to play, index 0-> rep column where it should play to be defensive*/
    private int[] horizontalCheck()
    {
        int[] count = new int[2];
        Arrays.fill(count,-1);

        for(int row=0; row < boardSize ; row++ )
        {
            for(int col=0; col < boardSize -3 ; col++)
            {
                /*OOO_*/
                if(board[row][col]==Status.ONE && board[row][col]==board[row][col+1] && board[row][col]==board[row][col+2] && board[row][col+3]==Status.NEITHER)
                {
                    count[0] = col + 3;
                 /*OO_O*/
                }else if(board[row][col]==Status.ONE && board[row][col]==board[row][col+1] && board[row][col]==board[row][col+3] && board[row][col+2]==Status.NEITHER)
                {
                    count[0] = col + 2;
                /*O_OO*/
                }else if(board[row][col]==Status.ONE && board[row][col]==board[row][col+2] && board[row][col]==board[row][col+2] && board[row][col+1]==Status.NEITHER)
                {
                    count[0] = col + 1;
                 /*_OOO*/
                }else if(board[row][col]==Status.NEITHER && board[row][col]==board[row][col+1] && board[row][col]==board[row][col+2] && board[row][col+3]==Status.ONE)
                {
                    count[0] =col;
                }
                /*Offensive checks*/
                else if(board[row][col]==Status.TWO && board[row][col]==board[row][col+1] && board[row][col]==board[row][col+2] && board[row][col+3] == Status.NEITHER)
                {
                    count[1] = col+3;
                }else if(board[row][col]==Status.TWO && board[row][col]==board[row][col+1] && board[row][col]==board[row][col+3] && board[row][col+2] == Status.NEITHER)
                {
                    count[1] = col+2;
                }else if(board[row][col]==Status.TWO && board[row][col]==board[row][col+2] && board[row][col]==board[row][col+3] && board[row][col+1] == Status.NEITHER)
                {
                    count[1] = col+1;
                }else if(board[row][col+1]==Status.TWO && board[row][col]==board[row][col+2] && board[row][col]==board[row][col+3] && board[row][col] == Status.NEITHER)
                {
                    count[1] = col;
                }
            }
        }
        return count;
    }

    /*Scans through the whole board to see if there is any diagonal chance of winning for the human
     * It also scans to see if there is any diagonal chance of winning for the AI plyer
     *  It returns an array with the column where to play, index 0-> rep column where it should play to be defensive*/
    private int[] checkDiagonallyFirst()
    {
        int[] count=new int[2];
        Arrays.fill(count,-1);

        for(int row = 0; row < boardSize -3; row++)
        {
            for(int col=0; col < boardSize-3; col++)
            {
                if(board[row][col]==Status.ONE &&board[row][col]==board[row+1][col+1] && board[row][col]==board[row+2][col+2] && board[row+3][col+3]==Status.NEITHER)
                {
                    count[0]= col+3;
                }
                else if(board[row][col]==Status.ONE &&board[row][col]==board[row+1][col+1] && board[row][col]==board[row+3][col+3] && board[row+2][col+2]==Status.NEITHER)
                {
                    count[0]= col+2;
                }
                else if(board[row][col]==Status.ONE &&board[row][col]==board[row+3][col+3] && board[row][col]==board[row+2][col+2] && board[row+1][col+1]==Status.NEITHER)
                {
                    count[0]= col+1;
                }else if(board[row+1][col+1]==Status.ONE &&board[row][col]==board[row+3][col+3] && board[row][col]==board[row+2][col+2] && board[row][col]==Status.NEITHER)
                {
                    count[0]=col;
                }
                /*Offensive*/
                else if(board[row][col]==Status.TWO &&board[row][col]==board[row+1][col+1] && board[row][col]==board[row+2][col+2] && board[row+3][col+3]==Status.NEITHER
                        && board[row][col+3]!=Status.NEITHER && board[row+1][col+3]!=Status.NEITHER && board[row+2][col+3]!=Status.NEITHER )
                {
                    count[1]=col+3;
                }else if(board[row][col]==Status.TWO &&board[row][col]==board[row+1][col+1] && board[row][col]==board[row+3][col+3] && board[row+2][col+2]==Status.NEITHER
                          && board[row][col+2]!=Status.NEITHER && board[row+1][col+2]!=Status.NEITHER  )
                {
                    count[1]=col+2;
                }else if(board[row][col]==Status.TWO &&board[row][col]==board[row+3][col+3] && board[row][col]==board[row+2][col+2] && board[row+1][col+1]==Status.NEITHER
                        &&  board[row][col+1]!=Status.NEITHER )
                {
                    count[1]=col+1;
                }else if(board[row+1][col+1]==Status.TWO &&board[row][col]==board[row+2][col+2] && board[row][col]==board[row+3][col+3] && board[row][col]==Status.NEITHER)
                {
                    count[1]=col;
                }
            }
        }
        return count;
    }

    /*Scans through the whole board to see if there is any diagonal chance of winning for the human
     * It also scans to see if there is any diagonal chance of winning for the AI plyer
     *  It returns an array with the column where to play, index 0-> rep column where it should play to be defensive*/
    private int[] checkDiagonallySecond()
    {
        int[] count=new int[2];
        Arrays.fill(count,-1);

        for(int row = 0; row < boardSize -3; row++)
        {
            for(int col=3; col < boardSize; col++)
            {
                if(board[row][col]==Status.ONE &&board[row][col]==board[row+1][col-1] && board[row][col]==board[row+2][col-2] && board[row+3][col-3]==Status.NEITHER)
                {
                    count[0]= col-3;
                }
                else if(board[row][col]==Status.ONE &&board[row][col]==board[row+1][col-1] && board[row][col]==board[row+3][col-3] && board[row+2][col-2]==Status.NEITHER)
                {
                    count[0]= col-2;
                }
                else if(board[row][col]==Status.ONE &&board[row][col]==board[row+3][col-3] && board[row][col]==board[row+2][col-2] && board[row+1][col-1]==Status.NEITHER)
                {
                    count[0]= col-1;
                }else if(board[row+1][col-1]==Status.ONE &&board[row][col]==board[row+3][col-3] && board[row][col]==board[row+2][col-2] && board[row][col]==Status.NEITHER)
                {
                    count[0]=col;
                }
                /*Offensive*/
                else if(board[row][col]==Status.TWO &&board[row][col]==board[row+1][col-1] && board[row][col]==board[row+2][col-2] && board[row+3][col-3]==Status.NEITHER &&
                board[row][col-3]!=Status.NEITHER && board[row+1][col-3]!=Status.NEITHER && board[row+2][col-3]!=Status.NEITHER)
                {
                    count[1]=col-3;
                }else if(board[row][col]==Status.TWO &&board[row][col]==board[row+1][col-1] && board[row][col]==board[row+3][col-3] && board[row+2][col-2]==Status.NEITHER &&
                        board[row][col-2]!=Status.NEITHER && board[row+1][col-2]!=Status.NEITHER)
                {
                    count[1]=col-2;
                }else if(board[row][col]==Status.TWO &&board[row][col]==board[row+3][col-3] && board[row][col]==board[row+2][col-2] && board[row+1][col-1]==Status.NEITHER
                        && board[row][col-1]!=Status.NEITHER)
                {
                    count[1]=col-1;
                }else if(board[row+1][col-1]==Status.TWO &&board[row][col]==board[row+2][col-2] && board[row][col]==board[row+3][col-3] && board[row][col]==Status.NEITHER)
                {
                    count[1]=col;
                }
            }
        }
        return count;
    }

    /*This method calls all the methods to check if there is any spot to be defended. If not, then check for any spot to be offended*/
    private void makeMove()
    {
        AIMoveMade = verticalCheck()[0];
        if(AIMoveMade == -1)
        {
            AIMoveMade = horizontalCheck()[0];
            if(AIMoveMade == -1)
            {
                AIMoveMade = checkDiagonallyFirst()[0];
                if(AIMoveMade == -1)
                {
                    AIMoveMade = checkDiagonallySecond()[0];
                    if(AIMoveMade == -1)
                    {
                        AIMoveMade = verticalCheck()[1];
                        if(AIMoveMade == -1)
                        {
                            AIMoveMade = horizontalCheck()[1];
                            if(AIMoveMade == -1)
                            {
                                AIMoveMade = checkDiagonallyFirst()[1];
                                if(AIMoveMade == -1)
                                {
                                    AIMoveMade = checkDiagonallySecond()[1];
                                    if(AIMoveMade == -1)
                                    {
                                        AIMoveMade=randomCol();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * verifyCol - private helper method to determine if an integer is a valid
     * column that still has spots left.
     * @param col - integer (potential column number)
     * @return - is the column valid?
     */
    private boolean verifyCol(int col) {
        return (col >= 0 && col < board[0].length && board[0][col] == Status.NEITHER);
    }

    private int randomCol()
    {
        Random r= new Random();
        return r.nextInt((boardSize));
    }
}
