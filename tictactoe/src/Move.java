/**
 * Created by mohamed on 1/6/17.
 */
public class Move {
    private String player;
    private int x, y;

    public Move(int x, int y, String player) {
        this.player = player;
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public String getPlayer() {
        return player;
    }
}
