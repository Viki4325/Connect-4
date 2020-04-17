public interface UI {
    void lastMove(int lastCol);
    void gameOver(Status winner);
    void setInfo(Human h, int size);
}
