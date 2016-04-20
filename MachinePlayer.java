/* MachinePlayer.java */

package player;

import player.list.*;



/**
 *  An implementation of an automatic Network player.  Keeps track of moves
 *  made by both players.  Can select a move for itself.
 */

public class MachinePlayer extends Player {

  public static final int NUM_CHIPS = 10;
  private GameBoard gameBoard;
  private int color;
  private int searchDepth;
  private ChipList myChips;
  private ChipList opponentChips;

  // Creates a machine player with the given color.  Color is either 0 (black)
  // or 1 (white).  (White has the first move.)
  public MachinePlayer(int color) {
    this.color = color;
    searchDepth = 2;
    myChips = new ChipList(color);
    opponentChips = new ChipList(Math.abs(color-1));
    for(int i=0; i<NUM_CHIPS; i++) {
      myChips.insertBack(new Chip(color, 0, 0, myChips));
      opponentChips.insertBack(new Chip(Math.abs(color-1), 0, 0, opponentChips));
    }
    gameBoard = new GameBoard(myChips, opponentChips);
  }

  

  // Creates a machine player with the given color and search depth.  Color is
  // either 0 (black) or 1 (white).  (White has the first move.)
  public MachinePlayer(int color, int searchDepth) {
    this(color);
    this.searchDepth = searchDepth;
  }

  // Returns a new move by "this" player.  Internally records the move (updates
  // the internal game board) as a move by "this" player.
  public Move chooseMove() {
    MoveListNode bestMove = minimax(gameBoard, color, searchDepth, -GameBoard.MAXIMUM_SCORE);
    Move theMove = bestMove.getMove();
    gameBoard.doMove(color, theMove);
    return theMove;
  } 

  /**
   * Need a GameBoard.getChips(int player)
   * Need a Chips.getChipsLeft()  
   * Need a Chips.selectChip(int i)
   * Need a GameBoard.removeChip(Chip c)
   * Need a GameBoard.MAXIMUM_SCORE
   * Need a GameBoard.validMoves(int player)
   * 
   **/

  private MoveListNode minimax( GameBoard gb, int player, int depth, int beta) {
    MoveList possibleMoves = gb.validMoves(player);	
    if(possibleMoves.front().getMove().moveKind == Move.STEP) {
      depth = depth-1;
    }
    MoveListNode possibleMove;
    MoveListIterator listIter = possibleMoves.iterator();
    while(listIter.hasNext()) {
      possibleMove = listIter.next();
      possibleMove.setScore(gb.evaluate(player, possibleMove.getMove()));
      if(possibleMove.getScore() == GameBoard.MAXIMUM_SCORE || possibleMove.getScore()>=(-beta)) {
        return possibleMove;
      }
    }
    if(depth <= 1) {	
	    return possibleMoves.topScore();
    }
    else {
	    int maxScore = -GameBoard.MAXIMUM_SCORE;
      listIter = possibleMoves.iterator();
      while(listIter.hasNext()) {
        possibleMove = listIter.next();
        maxScore = Math.max(maxScore, possibleMove.getScore());
        GameBoard gbNew = gb.copy();
        gbNew.doMove(player, possibleMove.getMove());
        possibleMove.setScore(-((minimax(gbNew, Math.abs(player-1), depth-1, maxScore).getScore())-5));
      }
    }
	  possibleMove = possibleMoves.topScore();
    return possibleMove;
  }

// If the Move m is legal, records the move as a move by the opponent
  // (updates the internal game board) and returns true.  If the move is
  // illegal, returns false without modifying the internal state of "this"
  // player.  This method allows your opponents to inform you of their moves.

  public boolean opponentMove(Move m) {
    if(gameBoard.isValidMove(Math.abs(color-1), m)) {
      gameBoard.doMove(Math.abs(color-1), m);
      return true;
    }
    return false;
  }

  // If the Move m is legal, records the move as a move by "this" player
  // (updates the internal game board) and returns true.  If the move is
  // illegal, returns false without modifying the internal state of "this"
  // player.  This method is used to help set up "Network problems" for your
  // player to solve.
  public boolean forceMove(Move m) {
    if(gameBoard.isValidMove(color, m)) {
      gameBoard.doMove(color, m);
      return true;
    }
    return false;
  }

}
