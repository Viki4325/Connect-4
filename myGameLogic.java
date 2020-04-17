import java.util.Random;
import java.util.Arrays;

public class myGameLogic implements GameLogic
{
    final private int MAX=12;
    final private int MIN=6;
    //players
    private Player AIPlayer;
    private Player humanPLayer;

    //pointer to hold the current player...who is playing currently
    private Player currentPlayer;
    //theBoard itself, 2D-array
    private Status[][] board;
    int size;
    //Move made by the player
    int currentPlayerMove=-1;
    int row;

    public myGameLogic()
    {
        size=boardSizeGenerator();

        board=new Status[size][size];
        fillBoard();

        AIPlayer=new AI();
        humanPLayer=new HumanPlayer();

        randomizeFirstPlayer();
        AIPlayer.setInfo(size,this);
        humanPLayer.setInfo(size,this);
    }

    public void setAnswer(int col)
    {
        currentPlayerMove=col;
        insertInBoard(currentPlayerMove);

        if(isBoardFull())
        {
            humanPLayer.gameOver(Status.NEITHER);
            AIPlayer.gameOver(Status.NEITHER);
        }
        if(gameOver())
        {
            humanPLayer.gameOver(getCurrentPlayerStatus());
            AIPlayer.gameOver(getCurrentPlayerStatus());
        }
        alternatePlayer();
        //call lastMove of on that player with lastMove being moveMade
        currentPlayer.lastMove(currentPlayerMove);
    }


    private int boardSizeGenerator()
    {
        Random r= new Random();
        return r.nextInt((MAX - MIN)+1)+MIN;
    }

    private void randomizeFirstPlayer()
    {
       Status[] participators={Status.ONE,Status.TWO};

       int randomIndex=new Random().nextInt(participators.length);
       if(participators[randomIndex]==Status.ONE)
       {
           currentPlayer=humanPLayer;
       }else
       {
           currentPlayer=AIPlayer;
       }
    }

    private void alternatePlayer()
    {
        if(currentPlayer.equals(humanPLayer))
        {
            currentPlayer = AIPlayer;
        }else if(currentPlayer.equals(AIPlayer))
        {
            currentPlayer = humanPLayer;
        }
    }

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
    private int drop(int col) {
        int posn = 0;
        while (posn < board.length && board[posn][col] == Status.NEITHER) {
            posn ++;
        }
        return posn-1;
    }

    private void insertInBoard(int col)
    {
        row=drop(col);
        if(currentPlayer.equals(humanPLayer))
        {
            board[row][col]=Status.ONE;
        }else if(currentPlayer.equals(AIPlayer))
        {
            board[row][col]=Status.TWO;
        }

    }

    private int diagonalLowerRight()
    {
        int winCounter=0;
        for(int row = 0; row < size -3; row++)
        {
            for(int col=0; col < size-3; col++)
            {
                if(board[row][col]==getCurrentPlayerStatus() &&board[row][col]==board[row+1][col+1] && board[row][col]==board[row+2][col+2] && board[row][col]==board[row+3][col+3])
                {
                    winCounter=4;
                }
            }
        }
        return winCounter;
    }

    private int diagonalUpperRight()
    {
        int winCounter=0;
        for(int row = 0; row < size -3; row++)
        {
            for(int col=3; col < size; col++)
            {
                if(board[row][col]==getCurrentPlayerStatus() &&board[row][col]==board[row+1][col-1] && board[row][col]==board[row+2][col-2] && board[row][col]==board[row+3][col-3])
                {
                    winCounter=4;
                }
            }
        }
            return winCounter;
        }

        private int checkHorizontalWin(int latestRow, int latestCol, int rowOffset, int colOffset)
        {
            int winCounter=0;
            if((!((size - latestCol) < 4)))
            {//This is the only time we need a loop
                for (int i = 1; i < 5; i++) {
                    if (board[latestRow][latestCol] == getCurrentPlayerStatus())
                    {
                        winCounter++;
                    }
                    latestRow += rowOffset;
                    latestCol += colOffset;
                }
        }
        return (winCounter);
    }

    private int checkVerticalWin(int latestRow, int latestCol, int rowOffset, int colOffset)
    {
        int winCounter=0;

        //don't need a for loop if
        if(!((size-latestRow)<4))
        {
            for(int i=0; i < 4; i++)
            {
                if(board[latestRow][latestCol]==getCurrentPlayerStatus())
                {
                    winCounter++;
                }
                latestRow+=rowOffset;
                latestCol+=colOffset;
            }
        }

        return (winCounter);
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

    private boolean isBoardFull()
    {
        int notVerified=0;
        //If all the columns can't be verified,(The columns is full) then it means all of them is full
        for(int i=0 ; i < board.length ; i++)
        {
            //the column should not be valid
            if(!verifyCol(i))
            {
                notVerified++;
            }
        }
        return (notVerified==board.length);

    }


    private boolean gameOver() {
        //vertical check
        if (checkVerticalWin(row, currentPlayerMove, 1, 0)==4)
        {
            return true;
        }

        //horizontal check
        for(int offset=0; offset <= currentPlayerMove ; offset++)
        {
            if(checkHorizontalWin(row, currentPlayerMove - offset, 0, 1)==4)
            {
                return true;
            }
        }

        //diagonal via lower right
        if (diagonalLowerRight()==4)
        {
            return true;
        }

        //diagonal via upper right
        if (diagonalUpperRight()==4)
        {
            return true;
        }
        return false;
    }



    private Status getCurrentPlayerStatus()
    {
        if(currentPlayer.equals(humanPLayer))
        {
            return Status.ONE;
        }
        else
        {
            return Status.TWO;
        }

    }

    public void startGame()
    {
        currentPlayer.lastMove(currentPlayerMove);
    }

}
