package player.list;

import java.util.Random;
	
public class MoveList extends List{

	private MoveListNode head;
	private int size;


	public MoveList() {
		size = 0;
		head = new MoveListNode(null, null, null);
		head.next = head;
		head.prev = head;
	}


	/**
	 * Inserts an object at the end of a list
	 **/

	public void insertBack(Object m) {
		head.getPrev().setNext(new MoveListNode(m, head, head.getPrev()));
		head.setPrev(head.getPrev().getNext());
		size++;
	}

	/**
	 * Inserts an object at the end of a list, also takes in an integer s, which will be
	 * assigned to the score of the move.
	 **/

	public void insertBack(Object m, int s){
		insertBack(m);
		head.getPrev().setScore(s);
	}

	/**
	 * Inserts an object at the beginning of a list
	 **/
	public void insertFront(Object m) {
		head.getNext().setPrev(new MoveListNode(m, head.getNext(), head));
		head.setNext(head.getNext().getPrev());
		size++;
	}

	/**
	 * Determines whether the node reference is the head of this list.
	 **/

	boolean isHead(MoveListNode node) {
		if(node == head) {
			return true;
		}
		return false;
	}

	/**
	 * Returns the first node of the list.
	 **/

	public MoveListNode front() {
		return head.getNext();
	}

	/**
	 * Returns the last node of the list
	 **/

	public MoveListNode back() {
		return head.getPrev();
	} 
	
	/**
	 * Returns an iterator which allows iteration through the list, as required 
	 * since this implements iterable
	 **/

	public MoveListIterator iterator() {
		return new MoveListIterator(this);
	}

	/**
	 * Provides a string interpretation of this list.
	 **/

	public String toString() {
		String line = "";
		MoveListIterator listIter = iterator();
		MoveListNode node;
		while(listIter.hasNext()) {
			node = listIter.next();
			line += node.toString() + "\n";
			node = node.getNext();
		}
		return line;
	}

	/**
	 * topScore() returns the MoveListNode with the highest scored move.
	 **/

	public MoveListNode topScore() {
		if(size==0) {
			System.out.println("The list was empty");
		}
		MoveListNode node;
		MoveListNode nextNode;
		MoveListIterator listIter = iterator();
		MoveList bestList = new MoveList();
		node = listIter.next();
		while(listIter.hasNext()) {
			nextNode = listIter.next();
			if(node.getScore() > nextNode.getScore()) {
				bestList.insertBack(node.getMove(), node.getScore());
			}
			if(node.getScore() == nextNode.getScore()) {
				bestList.insertBack(node.getMove(), node.getScore());
				node = nextNode;
				bestList.insertBack(node.getMove(), node.getScore());
			}
			if(node.getScore() < nextNode.getScore()) {
				node = nextNode;
				bestList = new MoveList();
				bestList.insertBack(node.getMove(), node.getScore());
			}
		}
		Random randomGenerator = new Random();
		int randomIndex = randomGenerator.nextInt(bestList.size);
		listIter = iterator();
		while(randomIndex > 0) {
			node = listIter.next();
			randomIndex--;
		}
		return node;
	}

}
