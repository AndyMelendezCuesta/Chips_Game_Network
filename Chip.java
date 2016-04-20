package player.list;

public class Chip extends ListNode{

    private int player;
    private int direction;
    private int x;
    private int y;
    private ChipList list;
    /**
     *  Default constructor for Chip. 
     *  Creates an Invalid Chip (Player value is not 0 or 1).
     */
    public Chip() {
        this.player = 10;
        list = null;
    }

    /**
     *  Valid Chips will have player value of 0 or 1.
     */
    public Chip(int player, int x, int y) {
        this.player = player;
        this.x = x;
        this.y = y;
    }

    public Chip(int player, int x, int y, ChipList l) {
        this(player, x, y);
        list = l;
    }

    /**
     *  Returns player of chip.
     */
    public int player() {
        return player;
    }

    /**
     *  Returns Chip's x coordinate.
     */
    public int x() {
        return x;
    }

    /**
     *  Returns Chip's y coordinate.
     */
    public int y() {
        return y;
    }

    /**
     *  Points the chip to a specific coordinate (x, y) on a Gameboard.
     */
    public void set (int x, int y) {
        this.x = x;
        this.y = y;
        list.chipsLeft--;
    }

    /**
     *  Called only by Gameboard.hasNetwork().
     *  Sets the orientation of the chip in relation to the connected chip.
     */
    public void setDirection(int i) {
        this.direction = i;
    }


    /**
     *  Returns direction of Chip.
     */
    public int direction() {
        return this.direction;
    }

    /** 
     *  Checks to see if the current Chip is a valid chip.
     *  Valid Chips have a player value of 0 or 1.
     */ 
    public boolean isValid() {
        return this.player == 0 || this.player == 1;
    }

    /**
     *  Returns Chip after this Chip.
     */
    public Chip next() {
        if (this.next != null) {
            return (Chip)this.next;
        }
        return null;
    }

    /**
     *  This Chip is removed from the board and does not point to any location on the Gameboard.
     *  It should only be called by Gameboard.removeChip().
     */
    public void reset() {
        this.x = 0;
        this.y = 0;
        list.chipsLeft++;
    }

    /**
     *  Removes a chip from the list.
     *  (I'm considering deleting this method because it might be unnecessary.
     *    I don't think any method calls so far involve removing a Chip from a list).
     */
    public void remove() {
        this.next.prev = this.prev;
        this.prev.next = this.next;
        this.next = null;
        this.prev = null;
    }

    /**
     *  Returns a chip with the same information as this chip.
     *  Does not copy prev or next pointers so it does not intefere with the ChipList.
     */
    public Chip copy() {
        Chip c = new Chip();
        c.x = this.x;
        c.y = this.y;
        c.player = this.player;
        return c;
    }

    public String toString() {
        String s = "Player " + this.player + " @ (" + this.x + ", " + this.y + ") --> " + this.direction;
        return s;
    }

}
