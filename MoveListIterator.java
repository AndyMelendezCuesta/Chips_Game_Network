package player.list;

public class MoveListIterator {
	private MoveListNode curr;
	private MoveList iterList;

	public MoveListIterator(MoveList lst) {
		iterList = lst;
		curr = iterList.front();
	}

	public boolean hasNext() {
		if(iterList.isHead(curr)) {
			return false;
		}
		return true;
	}

	public MoveListNode next() {
		MoveListNode nextReturn = curr;
		curr = curr.getNext();
		return nextReturn;
	}
}
