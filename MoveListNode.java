package player.list;

import player.Move;

public class MoveListNode extends ListNode{

	private Move move;
	private int score;


	MoveListNode(Object m, MoveListNode n, MoveListNode p) {
		next = n;
		prev = p;
		move = (Move) m;
		score = 0;
	}

	public void setScore(int s) {
		score = s;
	}

	public int getScore() {
		return score;
	}

	public Move getMove() {
		return (Move) move;
	}

	public void setMove(Move m) {
		move = m;
	}

	MoveListNode getNext() {
		return (MoveListNode) next;
	}

	MoveListNode getPrev() {
		return (MoveListNode) prev;
	}

	void setNext(MoveListNode n) {
		next = n;
	}

	void setPrev(MoveListNode p) {
		prev = p;
	}

	public String toString() {
		String line = "";
		line = line + "X-" + move.x1 + ", Y-" + move.y1 + ", S-" + score;
		if(move.moveKind == Move.STEP){
			line = line + ", OldX-" + move.x2 + ", OldY-" + move.y2;
		}
		line += "\n";
		return line;
	}
}
