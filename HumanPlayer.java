public class HumanPlayer  implements Human,Player
{
    //this class communicates with the UI
    private UI userInterfaceObj;
    //Game Logic pointer
    private GameLogic theLogic;
    //board size
    private int boardSize;
    //move made by opponent
    private int opponentMove=-1;
    //move made by Human player
    private int humanMoveMade;


    public HumanPlayer()
    {
        userInterfaceObj=new SwingGUI();
    }

    public void setAnswer(int col)
    {
        humanMoveMade=col;
        theLogic.setAnswer(humanMoveMade);
    }


    public void lastMove(int col)
    {
        opponentMove=col;
        userInterfaceObj.lastMove(col);
    }

    public void gameOver(Status winner)
    {
        userInterfaceObj.gameOver(winner);
        System.exit(0);
    }

    public void setInfo(int size, GameLogic gl)
    {
        boardSize=size;
        theLogic=gl;

        userInterfaceObj.setInfo(this,boardSize );

    }
}
