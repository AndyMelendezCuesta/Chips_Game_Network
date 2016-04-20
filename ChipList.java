package player.list;

import player.MachinePlayer;

public class ChipList extends List {
	
	private Chip head;
	private int player;
	int chipsLeft;

	/**
	 *  Each Chip in the ChipList must belong to the same player.
	 *
	 */

	/* KEVIN - I added a player category so that chiplist know their player
	 * That way if I need to get a players chips from a gameboard, I know
	 * whose they are even if there are no chips placed yet (that way I can).
	 * place the chip in the right list. */

	public ChipList() {
		head = new Chip();
		head.next = head;
		head.prev = head;
		player = -1;
		chipsLeft = 10;
	}

	public ChipList(int p) {
		this();
		player = p;
	}

	public int player() {
		return player;
	}


    public int getChipsLeft() {
        return chipsLeft;
    }

    public Chip selectChip(int index) {
    	Chip chip = head.next();
    	for(int i=0; i<index; i++) {
    		chip = chip.next();
    	}
    	return chip;
    }


	/**
	 *  Inserts Chip c at the front of the ChipList.
	 */
	public void insertFront(Object c) {
		head.next.prev = (Chip)c;
		((Chip)c).next = head.next;
		((Chip)c).prev = head;
		head.next = (Chip)c;
		size++;
	}

	/**
	 *  Inserts a Chip c at the back of the ChipList.
	 */
	public void insertBack(Object c) {
		head.prev.next = (Chip)c;
		((Chip)c).prev = head.prev;
		head.prev = (Chip)c;
		((Chip)c).next = head;
		size++;
	}

	/**
	 *  Returns the first valid Chip (not head) of this ChipList.
	 */
	public Chip front() {
		return (Chip)head.next;
	}

	/**
	 *  Returns back of the list.
	 */
	public Chip back() {
		return (Chip)head.prev;
	}

	/**
	 *  It just takes the toString() of the Chips inside and sticks them togeether.
	 */
	public String toString() {
		String s = "[";
		Chip curr = this.front();
		while (curr.isValid()) {
			s = s + curr.toString() + ", ";
			curr = curr.next();
		}
		return s + "]";
	}

	/**
	 *  Checks to see if this list is a valid Chip List.
	 *  A ChipList is a valid Chip List if all the Chips in the List belong to the same player.
	 *  May be useful for debugging?
	 */
	public boolean validChipList() {
		Chip curr = this.front();
		int p = curr.player();
		while (curr.isValid()) {
			if (curr.player() != p) {
				return false;
			}
			curr = curr.next();
		}
		return true;
	}

	/**
	 *  Returns whether a Chip is in the List or not.
	 */
	public boolean inList(Chip c) {
		Chip curr = this.front();
		while (curr.isValid()) {
			if (curr.player() == c.player() && curr.x() == c.x() && curr.y() == c.y()) {
				return true;
			}
			curr = curr.next();
		}
	return false;
	}

	/**
	 *  Returns a ChipList with the same information as this ChipList.
	 */
	public ChipList copy() {
		ChipList nwlst = new ChipList();
		Chip curr = this.front();
		while (curr.isValid()) {
			nwlst.insertBack(curr.copy());
			curr = curr.next();
		}
		return nwlst;
	}

	public static void main(String[] args) {
		Chip c = new Chip(0,2,6);
		System.out.println("Chip c info: " + c);

		Chip d = c.copy();
		System.out.println("Chip d (copy of c) info: " + d);

		Chip e = new Chip();
		System.out.println("Chip e (nothing initialized): " + e);

		ChipList lst = new ChipList();
		lst.insertFront(c);
		lst.insertFront(d);
		System.out.println("lst size: " + lst.length());
		System.out.println("lst = " + lst);

		Chip f = new Chip(1,7,4);
		System.out.println("f is a valid chip: " + f.isValid());

		lst.insertBack(f);
		System.out.println("After inserting f, lst is: " + lst);

		System.out.println("lst is a validChipList: " + lst.validChipList());

		System.out.println(lst.inList(f));

		Chip j = new Chip(0, 3, 5);
		System.out.println(lst.inList(j));

		ChipList copied = lst.copy();
		System.out.println(lst);
		System.out.println(copied);


	}


}
