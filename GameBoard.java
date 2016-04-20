/* GameBoard.java */

package player;

import player.list.*;


public class GameBoard {

    private Chip[][] board;
    private ChipList player1;
    private ChipList player2;
    private static final int DIMENSION = 8;
    static final int MAXIMUM_SCORE = 200;

    /**
     * Gameboard invariants:
     *  1) Board size will not change.
     *  2) board[0][0], board[0][7], board[7][0], board[7][7] should always point to null.
     *       - Methods that alter the location of a chip or place a chip should check for this.
     *  3) For any Chip c on (i, j) of the Gameboard, c.x() == i and c.y() == j.
     *  4) White goals should only have white chips and black goals should only have black chips.
     */

    public GameBoard() {
        board = new Chip[8][8];
    }

    ChipList getChipList(int player) {
        if(player1.player()==player) {
            return player1;
        }
        return player2;
    }

    /**
     * By validMoves takes an integer representing the player's color and returns a list of moves
     * that represent all possible moves for the current gameboard for that player.
     **/

    MoveList validMoves(int player) {
        MoveList validList = new MoveList();
        Move currMove;
        Chip chip;
        ChipList chips = getChipList(player);
        if(chips.getChipsLeft()==0) {
	        for(int chp=0; chp<MachinePlayer.NUM_CHIPS; chp++) {
	        	for(int i=0; i<DIMENSION; i++) {
	            	for(int j=0; j<DIMENSION; j++) {
	         	     	chip = chips.selectChip(chp);
	         	     	currMove = new Move(i, j, chip.x(), chip.y());
	         	     	if(isValidMove(player, currMove)) {
	         	     		validList.insertBack(currMove);
	         	     	}
	            	}
	        	}	
	        }
        }
        else {
        	for(int i=0; i<DIMENSION; i++) {
	            for(int j=0; j<DIMENSION; j++) {
	              	currMove = new Move(i, j);
	        	     	if(isValidMove(player, currMove)) {
	      	     		validList.insertBack(currMove);  
	           		}
	       		}
        	}
        }
        return validList;
    }

    /**
     * isValidSpace takes in an x and y, representing a space on the board, and an integer representing
     * the player's color and returns whether it is valid to move a players piece to that space.
     **/

    boolean isValidMove(int player, Move m) {
    	Chip chip;

    	if(m.moveKind == Move.ADD) {
    		if(getChipList(player).getChipsLeft() == 0){
    			return false;
    		}
    	}
    	if(m.moveKind == Move.STEP) {
    		if((m.x1 == m.x2 && m.y1 == m.y2) ||
    		   (getChip(m.x2,m.y2)==null || getChip(m.x2, m.y2).player()!=player)) {
    			return false;
    		}
 			chip = getChip(m.x2, m.y2);
    		removeChip(m.x2,m.y2);
    		if(!isValidSpace(m.x1, m.y1, player)) {
    			placeChip(chip, m.x2, m.y2);
    			return false;
    		}
    		else {
    			placeChip(chip, m.x2, m.y2);
    			return true;
    		}
    	}
    	if(!isValidSpace(m.x1, m.y1, player)) {
    		return false;
    	}

    	return true;
    }
    
    boolean isValidSpace(int x, int y, int player) {
        if((x<0 || x>=DIMENSION || y<0 || y>=DIMENSION) || 
           hasChip(x,y) ||
           (player==0 && (x==0 || x==7)) ||
           (player==1 && (y==0 || y==7))
           ) {
            return false;
        }
        int numberChipsIn3x3=0;
        for(int i=-1; i<=1; i++) {
            for(int j=-1; j<=1; j++) {
                if(x+i<0 || x+i>=DIMENSION || y+j<0 || y+j>=DIMENSION) {
                	continue;
                }
                Chip chip = getChip(x+i,y+j);
                if(chip != null && chip.player()==player) {
                    numberChipsIn3x3++;
                    if(adjacentToChip(player, x+i, y+j)) {
                 	   return false;
                	}
                }
                if(i==0 && j==0) {
                	numberChipsIn3x3++;
                }
            }
        }
        if(numberChipsIn3x3>2){
            return false;
        }
        return true;
    }

    /**
     * adjacentToChip takes in an integer reprenting a player's color and returns whether that chip
     * is next to another of that player's chip.
     **/

    boolean adjacentToChip(int player, int x, int y) {
        for(int i=-1; i<=1; i++) {
            for(int j=-1; j<=1; j++) {
                if(x+i<0 || x+i>=DIMENSION || y+j<0 || y+j>=DIMENSION) {
                	continue;
                }
                Chip chip = getChip(x+i,y+j);
                if((chip != null && chip.player()==player) && (i!=0 || j!=0)) {
                    return true;
                }
            }
        }
        return false;
    }


    public GameBoard(ChipList player1, ChipList player2) {
        this.player1 = player1;
        this.player2 = player2;
        board = new Chip[8][8];
    }

    /**
     * Checks to see if a location on the board has a Chip.
     * Returns true if there is a chip, false if there is not.
     */
    public boolean hasChip(int x, int y) {
        return board[y][x] != null;
    }

    public void doMove(int player, Move m) {
        ChipList chips = getChipList(player);
        Chip chip;
        if(m.moveKind == Move.ADD) {
            chip = chips.selectChip(10-chips.getChipsLeft());
            placeChip(chip, m.x1, m.y1);
        }
        else if(m.moveKind == Move.STEP) {
			chip = getChip(m.x2, m.y2);
			removeChip(m.x2, m.y2);
			placeChip(chip, m.x1, m.y1);
        }
        return;
        }

    /** 
     *  Places Chip c at x, y of the board. It should be called by doMove()
     *  and copy().
     */
    public void placeChip(Chip c, int x, int y) {
        c.set(x, y);
        board[y][x] = c;
    }

    public int evaluate(int player, Move m) {
    	//initializing the return variable
    	int score = 0;
    	//copy Gameboard
	    GameBoard gbNew = this.copy();
    	//initializing important fields
    	int opponent = Math.abs(player-1);
    	int chipsOnBoardPlayer = getChipList(player).getChipsLeft();
    	int totalBlocks = GameBoard.DIMENSION * GameBoard.DIMENSION;//64 blocks
    	//the move is performed; player or color seem to be the same
    	gbNew.doMove(player, m);
    	//check if there's a network, if either player wins return corresponding score
    	if(gbNew.hasNetwork(player)) {
    			return MAXIMUM_SCORE;
    	}
    	if(gbNew.hasNetwork(opponent)) {
    			return -MAXIMUM_SCORE;	
    	}
    	//penalizing for chips next to each other
    	if(this.adjacentToChip(player, m.x1, m.y1)) {
  			    score -= 10;
    	}    	
    	//initializing other important fields
    	int chipsInStartPlayer = gbNew.chipsInStart(player);
    	int chipsInGoalPlayer = gbNew.chipsInGoal(player);
    	
    	//Indeed rewarding a chip in start and goal
    	//makes the strategy try to put a chip there.
    	if(Math.abs(chipsInStartPlayer-2) == 1) {
    			score -= 5;
    	}
    	if(Math.abs(chipsInStartPlayer-2) == 2) {
    			score -= 10;
    	}
    	if(Math.abs(chipsInGoalPlayer-2) == 1) {
    			score -= 5;
    	}
    	if(Math.abs(chipsInGoalPlayer-2) == 2) {
    			score -= 10;
    	}
    	//evaluate player's score and return score
   		if(gbNew.missingChips(player) == gbNew.missingChips(opponent)){
    		score += chipsOnBoardPlayer;    
    	}else{
      		score += gbNew.missingChips(player);
    	}
    	//initializing last variable to store the value of the score from strategy
    	int strategyScore = gbNew.strategyInGame(player);
    	//here i add the strategy points to the score
    	score += strategyScore;
    	return score;
  	}
    
    public int chipsInStart(int player) {
    	Chip chip = getChipList(player).front();
    	int total = 0;
    	while(chip.isValid()) {
    		if(inStart(chip) && (chip.x()!=0 && chip.y()!=0)) {
    			total++;
    		} 
    		chip = chip.next();
    	}	
    	return total;
    }

    public int chipsInGoal(int player) {
    	Chip chip = getChipList(player).front();
    	int total = 0;
    	while(chip.isValid()) {
    		if(inGoal(chip)) {
    			total++;
    		} 
    		chip = chip.next();
    	}	
    	return total;
    }

    public int strategyInGame(int player) {
    //initializing return value
    int addToScore = 0;
    //initializing important fields
    int opponent = Math.abs(player-1);
    int chipsOnBoardPlayer = getChipList(player).getChipsLeft();
    int chipsOnBoardOpponent = getChipList(opponent).getChipsLeft();
    int neededChipsPlayer = missingChips(player);
    int neededChipsOpponent = missingChips(opponent);
    //int networkBlocksPlayer = this.networkBlocksAvailable(player);
    //int networkBlocksOpponent = this.networkBlocksAvailable(opponent);
    //checking if there is a winner, if THE GAME IS FINISHED
    //THE GAME IS UNFINISHED
    //im considering that the implementation of strategy can be easier if
    //the field scorePlayer(the output of evaluate) is initialized out from evaluate function, 
    //so that the evaluate function can reset its value but the
    //other StrategyInGame can have. Or maybe having a function to get the Score
    //of the given player. The function can be called getScore(int player)
        
	//Here the game is unfinished 
	//and PLAYER HAS A BETTER SCORE
           if(neededChipsPlayer > neededChipsOpponent){
              //if the player has a better score than the opponent
              //and the opponent needs more than 4 chips to complete
              //a network
              if(neededChipsOpponent > 4){
                  //check who has more network blocks available
                  if(chipsOnBoardPlayer > chipsOnBoardOpponent){
                      //if the player has more network blocks available
                      //expand (neutral strategy)
                      addToScore = 10;
                  }else if(chipsOnBoardPlayer == chipsOnBoardOpponent){
                      //if player and opponent have equal number of blocks available
                      //expand (neutral strategy)
                      addToScore = 9;
                  }else{
                      //if opponent has more network blocks available 
                      //complete network as soon as possible
                      //minimum 6 chips (offensive strategy)
                      addToScore = 8;
                  }
              }else if(neededChipsOpponent == 4){
                  //check who has more network blocks available
                  if(chipsOnBoardPlayer > chipsOnBoardOpponent){
                      //if the player has more network blocks available
                      //complete network as soon as possible
                      //minimum 6 chips (offensive strategy)
                      addToScore = 7;
                  }else if(chipsOnBoardPlayer == chipsOnBoardOpponent){
                      //if player and opponent have equal number of blocks available
                      //complete network as soon as possible
                      //minimum 6 chips (offensive strategy)
                      addToScore = 6;
                  }else{
                      //if opponent has more network blocks available 
                      //block the opponent(defensive strategy)
                      addToScore = 5;
                  }
              }else if(neededChipsOpponent == 3){
                  //check who has more network blocks available
                  if(chipsOnBoardPlayer > chipsOnBoardOpponent){
                      //if the player has more network blocks available
                      //block the opponent(defensive strategy)
                      addToScore = 4;
                  }else if(chipsOnBoardPlayer == chipsOnBoardOpponent){
                      //if player and opponent have equal number of blocks available
                      //expand (neutral strategy), or maybe block the opponent??
                      addToScore = 3;
                  }else{
                      //if opponent has more network blocks available 
                      //block the opponent(defensive strategy)
                      addToScore = 2;
                  }
              }else {
              //else if opponent is missing 2 chips to complete his network
              //it means that player only needs 1 more chip to complete his network
              //because in this case player has a better score than opponent
              //So, we let minmax handle it
                  addToScore = -2;
              }
            }else if(neededChipsPlayer == neededChipsOpponent){
                //in case that there is a tie in scores and the game is unfinished 
                //(very odd situation because evaluate tries to be precise when
                //calculating the score)
                if(neededChipsPlayer >= 4){
                    //expand (neutral strategy)
                    addToScore = 3;
                }else if(neededChipsPlayer == 3){
                    //complete the network as soon as possible
                    //minimum 6 chips (offensive strategy)
                    addToScore = 2;
                }else if(neededChipsPlayer == 2){
                	//block the opponent (defensive strategy)
                	addToScore = 1;
                }else{
                	//if the player only needs one more chip 
                	//to complete a network, let minmax handle it
                	addToScore = -3;
                }
            }else {
              //if the player has a better score than the opponent
              //and the opponent needs more than 4 chips to complete
              //a network
              if(neededChipsOpponent > 4){
                  //check who has more network blocks available
                  if(chipsOnBoardPlayer > chipsOnBoardOpponent){
                      //if the player has more network blocks available
                      //expand (neutral strategy)
                      addToScore = 1;
                  }else if(chipsOnBoardPlayer == chipsOnBoardOpponent){
                      //if player and opponent have equal number of blocks available
                      //expand (neutral strategy)
                      addToScore = -1;
                  }else{
                      //if opponent has more network blocks available 
                      //complete network as soon as possible
                      //minimum 6 chips (offensive strategy)
                      addToScore = -2;
                  }
              } else if(neededChipsOpponent == 4){
                  //check who has more network blocks
                  if(chipsOnBoardPlayer > chipsOnBoardOpponent){
                      //if the player has more network blocks available
                      //complete network as soon as possible
                      //minimum 6 chips (offensive strategy)
                      addToScore = -1;
                  }else if(chipsOnBoardPlayer == chipsOnBoardOpponent){
                      //if player and opponent have equal number of blocks available
                      //expand(neutral strategy)
                      addToScore = -2;
                  }else{
                      //if opponent has more network blocks available 
                      //complete network as soon as possible
                      //minimum 6 chips (offensive strategy)
                      addToScore = -3;
                  }
              } else if(neededChipsOpponent == 3 || neededChipsOpponent == 2){
                      //block the opponent(defensive strategy)
                      addToScore = -3;
              }else{
              //else if opponent is missing 2 chips to complete his network
              //it means that player only needs 1 more chip to complete his network
              //because in this case player has a better score than opponent
              //So, we let minmax handle it
                      addToScore = -4;
              }
           }
        return addToScore;
    }

     public int missingChips(int player) {
        int count = 0;
        Chip chip = new Chip(player, 0, 0, new ChipList());
        GameBoard gb = this.copy();
        for (int j = 0; j < DIMENSION; j++) {
            for (int i = 0; i < DIMENSION; i++) {
                if (!gb.hasChip(i, j)) {
                    gb.placeChip(chip, i, j);
                    if (gb.hasNetwork(player)) {
                        count++;
                    }
                    gb.removeChip(i, j);
                }
            }
        }
        return count;
    }


    /**
     *  Removes a Chip from the board (resets it).
     *  If there is no chip in the spot, do nothing.
     *  It calls the Chip's reset method so the Chip does not reference that spot anymore.
     */
    public void removeChip(int x, int y) {
        if (hasChip(x, y)) {
            board[y][x].reset();
            board[y][x] = null;
        }
    }

    /**
     *  Returns the chip at coordinate (x, y) of the Gameboard.
     *  If There is no Chip it returns null.
     */
    public Chip getChip(int x, int y) {
        if (hasChip(x, y)) {
            return board[y][x];
        } else {
            return null;
        }
    }


    /**
     *  Creates a copy of the Gameboard and all the chips that are on it.
     *  It should not change the original Gameboard.
     */

    public void copy(GameBoard g) {
        Chip c;
        for (int j = 0; j < board.length; j++) {
            for (int i = 0; i < board.length; i++) {
                if (g.hasChip(i, j)) {
                    c = (g.getChip(i, j)).copy();
                    this.placeChip(c, i, j);
                }
            }
        }
    }

    public GameBoard copy() {
		GameBoard copy = new GameBoard(new ChipList(player1.player()), new ChipList(player2.player()));
		Chip player1chip = player1.front();
		Chip copy1;
		Chip player2chip = player2.front();
		Chip copy2;
		while(player1chip.isValid()) {
			copy1 = new Chip(player1.player(), 0, 0, copy.player1);
			copy2 = new Chip(player2.player(), 0, 0, copy.player2);
			if(player1chip.x()!=0 || player1chip.y()!=0) {
				copy.placeChip(copy1, player1chip.x(), player1chip.y());
			}
			if(player2chip.x()!=0 || player2chip.y()!=0) {
				copy.placeChip(copy2, player2chip.x(), player2chip.y());
			}
			copy.player1.insertBack(copy1);
			copy.player2.insertBack(copy2);
			player1chip = player1chip.next();
			player2chip = player2chip.next();
		}
		
		if(player1.getChipsLeft()!=copy.player1.getChipsLeft() || player2.getChipsLeft()!=copy.player2.getChipsLeft()) {
			System.out.println("FALSE");
		}
		
		return copy;
    }


    /**
     *  Helper function for findConnections() and hasNetwork().
     *  Returns the Chips in the starting goal area of the player.
     *  White's start goals = board[0][n] for any n greater than 0 and less than 7.
     *  Black's start goals = board[n][0] for any n greater than 0 and less than 7.
     */
    public ChipList startGoal(int player) {
        ChipList lst = new ChipList();
        Chip c;
        if (player == 0) {
            for (int i = 1; i < board.length - 1; i++) {
                if (hasChip(i, 0)) {
                    c = getChip(i, 0);
                    lst.insertFront(c.copy());
                }
            }
        } else {
            for (int j = 1; j < board.length - 1; j++) {
                if (hasChip(0, j)) {
                    c = getChip(0, j);
                    lst.insertFront(c.copy());
                }
            }
        }
        return lst;
    }

    /**
     *  Helper function for findConnections() and hasNetwork().
     *  Returns whether a Chip is in the starting goal area or not.
     */
    public boolean inStart(Chip c) {
        int x = c.x();
        int y = c.y();
        int player = c.player();
        if (player == 0) {
            return y == 0;
        } else {
            return x == 0;
        }
    }

    /**
     * Helper function for findConnections() and hasNetwork().
     * Checks if a chip is in the opposite goal.
     * Black is 0, and goals are N --> S ends of the board.
     * White is 1, and goals are W --> E ends of the board.
     */
    public boolean inGoal(Chip c) {
        int x = c.x();
        int y = c.y();
        int player = c.player();
        if (player == 0) {
            return x != 0 && x != 7 && y == 7;
        } else {
            return x == 7 && y != 0 && y != 7;
        }
    }

    /**  
     *  findConnections takes in a Chip c and looks for all connected chips of the same color.
     *  It assigns each connected chip a direction and returns connected chips in a ChipList.
     *
     *   --- --- ---         -------------- -------------- --------------   
     *  | 1 | 2 | 3 |       | x - 1, y - 1 |   x, y - 1   | x + 1, y - 1 |
     *   --- --- ---         -------------- -------------- --------------
     *  | 4 | @ | 5 |   ==  |   x - 1, y   | Chip c (x,y) |   x + 1, y   |
     *   --- --- ---         -------------- -------------- --------------
     *  | 6 | 7 | 8 |       | x - 1, y + 1 |   x, y + 1   | x + 1, y + 1 |
     *   --- --- ---         -------------- -------------- --------------
     *
     */
    public ChipList findConnections(Chip c) {
        Chip n;
        ChipList lst = new ChipList();
        int x = c.x();
        int y = c.y();
        int player = c.player();
        int x0 = x - 1;
        int y0 = y - 1;
        while (x0 > 0 && y0 > 0) {
            if (hasChip(x0, y0)) {
                if (getChip(x0, y0).player() == player) {
                    n = getChip(x0, y0).copy();
                    n.setDirection(1);
                    lst.insertFront(n);
                }
                break;
            }
            x0--;
            y0--;
        }
        int y1 = y - 1;
        while (y1 >= 0) {
            if (hasChip(x, y1)) {
                if (getChip(x, y1).player() == player) {
                    n = getChip(x, y1).copy();
                    n.setDirection(2);
                    lst.insertFront(n);
                }
                break;
            }
            y1--;
        }
        int x2 = x + 1;
        int y2 = y - 1;
        while (x2 < board.length && y2 >= 0) {
            if (hasChip(x2, y2)) {
                if (getChip(x2, y2).player() == player) {
                    n = getChip(x2, y2).copy();
                    n.setDirection(3);
                    lst.insertFront(n);
                }
                break;
            }
            x2++;
            y2--;
        }
        int x3 = x - 1;
        while (x3 >= 0) {
            if (hasChip(x3, y)) {
                if (getChip(x3, y).player() == player) {
                    n = getChip(x3, y).copy();
                    n.setDirection(4);
                    lst.insertFront(n);
                }
                break;
            }
            x3--;
        }
        int x4 = x + 1;
        while (x4 < board.length) {
            if (hasChip(x4, y)) {
                if (getChip(x4, y).player() == player) {
                    n = getChip(x4, y).copy();
                    n.setDirection(5);
                    lst.insertFront(n);
                }
                break;
            }
            x4++;
        }
        int x5 = x - 1;
        int y5 = y + 1;
        while (x5 >= 0 && y5 < board.length) {
            if (hasChip(x5, y5)) {
                if (getChip(x5, y5).player() == player) {
                    n = getChip(x5, y5).copy();
                    n.setDirection(6);
                    lst.insertFront(n);
                }
                break;
            }
            x5--;
            y5++;
        }
        int y6 = y + 1;
        while (y6 < board.length) {
            if (hasChip(x, y6)) {
                if (getChip(x, y6).player() == player) {
                    n = getChip(x, y6).copy();
                    n.setDirection(7);
                    lst.insertFront(n);
                }
                break;
            }
            y6++;
        }
        int x7 = x + 1;
        int y7 = y + 1;
        while (x7 < board.length && y7 < board.length) {
            if (hasChip(x7, y7)) {
                if (getChip(x7, y7).player() == player) {
                    n = getChip(x7, y7).copy();
                    n.setDirection(8);
                    lst.insertFront(n);
                }
                break;
            }
            x7++;
            y7++;
        }
        return lst;
    }

    /**
     *  Checks to see if a player has a network on the Gameboard.
     *  @param player is the player passed in.
     */ 
    public boolean hasNetwork(int player) {
        return networkHelper(player, null, null);
    }

    /**
     *  Helper function for hasNetwork
     *  @param player is the player passed in.
     *  @param c is the chip it searches for a connection on.
     *  @param count is the number of chips in the network built so far.
     *  @param lst is the list of Chips in the network so far
     */
 
     private boolean networkHelper(int player, Chip c, ChipList lst) {
        boolean network = false;
        if (c == null) {
            ChipList strt = startGoal(player);
            if (strt.isEmpty()) {
                return false;
            } else {
                Chip curr = strt.front();
                while (curr.isValid()) {
                    ChipList current = new ChipList();
                    network = networkHelper(player, curr, current);
                    if (network == true) {
                        return network;
                    }
                    curr = curr.next();
                }
            }
        } else {
            if (inGoal(c)) {
                if (lst.length() + 1 >= 6) {
                    return true;
                }
                return false;
            }
            ChipList connections = findConnections(c);
            if (connections.isEmpty()) {
                return false;
            }
            ChipList nwlst = lst.copy();
            nwlst.insertFront(c.copy());
            Chip cur = connections.front();
            while (cur.isValid()) {
                if (cur.direction() != c.direction() && !lst.inList(cur) && !inStart(cur)) {
                    network = networkHelper(player, cur, nwlst);
                    if (network == true) {
                        return network;
                    }
                }
                cur = cur.next();
            }
        }
        return network;
    }


    public String toString() {
        String divide = "\n" + "---------------------------------" + "\n";
        String s = "" + divide;
        for (int j = 0; j < board.length; j++) {
            s = s + "|";
            for (int i = 0; i < board.length; i++) {
                if ((i == 0 && j == 0) || (i == 0 && j == 7) || (i == 7 && j == 0) || (i == 7 && j == 7)) {
                    s = s + " X |";
                } else if (board[j][i] == null) {
                    s = s + "   |";
                } else {
                    s = s + " " + board[j][i].player() + " |";
                }
            }
            s = s + divide;
        }
        return s;
    }

    public static void main (String[] args) {
        GameBoard game = new GameBoard();
        ChipList black = new ChipList();
        Chip c20 = new Chip(0, 2, 0);
        black.insertFront(c20);
        Chip c25 = new Chip(0, 2, 5);
        black.insertFront(c25);
        Chip c35 = new Chip(0, 3, 5);
        black.insertFront(c35);
        Chip c13 = new Chip(0, 1, 3);
        black.insertFront(c13);
        Chip c33 = new Chip(0, 3, 3);
        black.insertFront(c33);
        Chip c55 = new Chip(0, 5, 5);
        black.insertFront(c55);
        Chip c57 = new Chip(0, 5, 7);
        black.insertFront(c57);

        game.placeChip(c20, 2, 0);
        game.placeChip(c25, 2, 5);
        game.placeChip(c35, 3, 5);
        game.placeChip(c13, 1, 3);
        game.placeChip(c33, 3, 3);
        game.placeChip(c55, 5, 5);
        game.placeChip(c57, 5, 7);
        System.out.println(black + "size: " + black.length());
        System.out.println(game);

        ChipList connect35 = game.findConnections(c35);
        System.out.println(connect35);

        ChipList blackstart = game.startGoal(0);
        System.out.println(blackstart);

        System.out.println(game.findConnections(c20));

        GameBoard gb = new GameBoard();
        gb.copy(game);
        System.out.println(gb);

        System.out.println(game.hasNetwork(0));

        gb.removeChip(2, 0);
        gb.removeChip(2, 5);
        gb.removeChip(1, 3);

        Chip c60 = new Chip(0, 6, 0);
        Chip c65 = new Chip(0, 6, 5);

        gb.placeChip(c60, 6, 0);
        gb.placeChip(c65, 6, 5);

        System.out.println(gb);
        System.out.println(game);

        System.out.println(gb.hasNetwork(0));

        Chip w62 = new Chip(1, 6, 2);
        gb.placeChip(w62, 6, 2);

        System.out.println(gb);
        System.out.println(gb.hasNetwork(0));

    }

}/* GameBoard.java */

package player;

import player.list.*;


public class GameBoard {

    private Chip[][] board;
    private ChipList player1;
    private ChipList player2;
    private static final int DIMENSION = 8;
    static final int MAXIMUM_SCORE = 200;

    /**
     * Gameboard invariants:
     *  1) Board size will not change.
     *  2) board[0][0], board[0][7], board[7][0], board[7][7] should always point to null.
     *       - Methods that alter the location of a chip or place a chip should check for this.
     *  3) For any Chip c on (i, j) of the Gameboard, c.x() == i and c.y() == j.
     *  4) White goals should only have white chips and black goals should only have black chips.
     */

    public GameBoard() {
        board = new Chip[8][8];
    }

    ChipList getChipList(int player) {
        if(player1.player()==player) {
            return player1;
        }
        return player2;
    }

    /**
     * By validMoves takes an integer representing the player's color and returns a list of moves
     * that represent all possible moves for the current gameboard for that player.
     **/

    MoveList validMoves(int player) {
        MoveList validList = new MoveList();
        Move currMove;
        Chip chip;
        ChipList chips = getChipList(player);
        if(chips.getChipsLeft()==0) {
	        for(int chp=0; chp<MachinePlayer.NUM_CHIPS; chp++) {
	        	for(int i=0; i<DIMENSION; i++) {
	            	for(int j=0; j<DIMENSION; j++) {
	         	     	chip = chips.selectChip(chp);
	         	     	currMove = new Move(i, j, chip.x(), chip.y());
	         	     	if(isValidMove(player, currMove)) {
	         	     		validList.insertBack(currMove);
	         	     	}
	            	}
	        	}	
	        }
        }
        else {
        	for(int i=0; i<DIMENSION; i++) {
	            for(int j=0; j<DIMENSION; j++) {
	              	currMove = new Move(i, j);
	        	     	if(isValidMove(player, currMove)) {
	      	     		validList.insertBack(currMove);  
	           		}
	       		}
        	}
        }
        return validList;
    }

    /**
     * isValidSpace takes in an x and y, representing a space on the board, and an integer representing
     * the player's color and returns whether it is valid to move a players piece to that space.
     **/

    boolean isValidMove(int player, Move m) {
    	Chip chip;

    	if(m.moveKind == Move.ADD) {
    		if(getChipList(player).getChipsLeft() == 0){
    			return false;
    		}
    	}
    	if(m.moveKind == Move.STEP) {
    		if((m.x1 == m.x2 && m.y1 == m.y2) ||
    		   (getChip(m.x2,m.y2)==null || getChip(m.x2, m.y2).player()!=player)) {
    			return false;
    		}
 			chip = getChip(m.x2, m.y2);
    		removeChip(m.x2,m.y2);
    		if(!isValidSpace(m.x1, m.y1, player)) {
    			placeChip(chip, m.x2, m.y2);
    			return false;
    		}
    		else {
    			placeChip(chip, m.x2, m.y2);
    			return true;
    		}
    	}
    	if(!isValidSpace(m.x1, m.y1, player)) {
    		return false;
    	}

    	return true;
    }
    
    boolean isValidSpace(int x, int y, int player) {
        if((x<0 || x>=DIMENSION || y<0 || y>=DIMENSION) || 
           hasChip(x,y) ||
           (player==0 && (x==0 || x==7)) ||
           (player==1 && (y==0 || y==7))
           ) {
            return false;
        }
        int numberChipsIn3x3=0;
        for(int i=-1; i<=1; i++) {
            for(int j=-1; j<=1; j++) {
                if(x+i<0 || x+i>=DIMENSION || y+j<0 || y+j>=DIMENSION) {
                	continue;
                }
                Chip chip = getChip(x+i,y+j);
                if(chip != null && chip.player()==player) {
                    numberChipsIn3x3++;
                    if(adjacentToChip(player, x+i, y+j)) {
                 	   return false;
                	}
                }
                if(i==0 && j==0) {
                	numberChipsIn3x3++;
                }
            }
        }
        if(numberChipsIn3x3>2){
            return false;
        }
        return true;
    }

    /**
     * adjacentToChip takes in an integer reprenting a player's color and returns whether that chip
     * is next to another of that player's chip.
     **/

    boolean adjacentToChip(int player, int x, int y) {
        for(int i=-1; i<=1; i++) {
            for(int j=-1; j<=1; j++) {
                if(x+i<0 || x+i>=DIMENSION || y+j<0 || y+j>=DIMENSION) {
                	continue;
                }
                Chip chip = getChip(x+i,y+j);
                if((chip != null && chip.player()==player) && (i!=0 || j!=0)) {
                    return true;
                }
            }
        }
        return false;
    }


    public GameBoard(ChipList player1, ChipList player2) {
        this.player1 = player1;
        this.player2 = player2;
        board = new Chip[8][8];
    }

    /**
     * Checks to see if a location on the board has a Chip.
     * Returns true if there is a chip, false if there is not.
     */
    public boolean hasChip(int x, int y) {
        return board[y][x] != null;
    }

    public void doMove(int player, Move m) {
        ChipList chips = getChipList(player);
        Chip chip;
        if(m.moveKind == Move.ADD) {
            chip = chips.selectChip(10-chips.getChipsLeft());
            placeChip(chip, m.x1, m.y1);
        }
        else if(m.moveKind == Move.STEP) {
			chip = getChip(m.x2, m.y2);
			removeChip(m.x2, m.y2);
			placeChip(chip, m.x1, m.y1);
        }
        return;
        }

    /** 
     *  Places Chip c at x, y of the board. It should be called by doMove()
     *  and copy().
     */
    public void placeChip(Chip c, int x, int y) {
        c.set(x, y);
        board[y][x] = c;
    }

    public int evaluate(int player, Move m) {
    	//initializing the return variable
    	int score = 0;
    	//copy Gameboard
	    GameBoard gbNew = this.copy();
    	//initializing important fields
    	int opponent = Math.abs(player-1);
    	int chipsOnBoardPlayer = getChipList(player).getChipsLeft();
    	int totalBlocks = GameBoard.DIMENSION * GameBoard.DIMENSION;//64 blocks
    	//the move is performed; player or color seem to be the same
    	gbNew.doMove(player, m);
    	//check if there's a network, if either player wins return corresponding score
    	if(gbNew.hasNetwork(player)) {
    			return MAXIMUM_SCORE;
    	}
    	if(gbNew.hasNetwork(opponent)) {
    			return -MAXIMUM_SCORE;	
    	}
    	//penalizing for chips next to each other
    	if(this.adjacentToChip(player, m.x1, m.y1)) {
  			    score -= 10;
    	}    	
    	//initializing other important fields
    	int chipsInStartPlayer = gbNew.chipsInStart(player);
    	int chipsInGoalPlayer = gbNew.chipsInGoal(player);
    	
    	//Indeed rewarding a chip in start and goal
    	//makes the strategy try to put a chip there.
    	if(Math.abs(chipsInStartPlayer-2) == 1) {
    			score -= 5;
    	}
    	if(Math.abs(chipsInStartPlayer-2) == 2) {
    			score -= 10;
    	}
    	if(Math.abs(chipsInGoalPlayer-2) == 1) {
    			score -= 5;
    	}
    	if(Math.abs(chipsInGoalPlayer-2) == 2) {
    			score -= 10;
    	}
    	//evaluate player's score and return score
   		if(gbNew.missingChips(player) == gbNew.missingChips(opponent)){
    		score += chipsOnBoardPlayer;    
    	}else{
      		score += gbNew.missingChips(player);
    	}
    	//initializing last variable to store the value of the score from strategy
    	int strategyScore = gbNew.strategyInGame(player);
    	//here i add the strategy points to the score
    	score += strategyScore;
    	return score;
  	}
    
    public int chipsInStart(int player) {
    	Chip chip = getChipList(player).front();
    	int total = 0;
    	while(chip.isValid()) {
    		if(inStart(chip) && (chip.x()!=0 && chip.y()!=0)) {
    			total++;
    		} 
    		chip = chip.next();
    	}	
    	return total;
    }

    public int chipsInGoal(int player) {
    	Chip chip = getChipList(player).front();
    	int total = 0;
    	while(chip.isValid()) {
    		if(inGoal(chip)) {
    			total++;
    		} 
    		chip = chip.next();
    	}	
    	return total;
    }

    public int strategyInGame(int player) {
    //initializing return value
    int addToScore = 0;
    //initializing important fields
    int opponent = Math.abs(player-1);
    int chipsOnBoardPlayer = getChipList(player).getChipsLeft();
    int chipsOnBoardOpponent = getChipList(opponent).getChipsLeft();
    int neededChipsPlayer = missingChips(player);
    int neededChipsOpponent = missingChips(opponent);
    //int networkBlocksPlayer = this.networkBlocksAvailable(player);
    //int networkBlocksOpponent = this.networkBlocksAvailable(opponent);
    //checking if there is a winner, if THE GAME IS FINISHED
    //THE GAME IS UNFINISHED
    //im considering that the implementation of strategy can be easier if
    //the field scorePlayer(the output of evaluate) is initialized out from evaluate function, 
    //so that the evaluate function can reset its value but the
    //other StrategyInGame can have. Or maybe having a function to get the Score
    //of the given player. The function can be called getScore(int player)
        
	//Here the game is unfinished 
	//and PLAYER HAS A BETTER SCORE
           if(neededChipsPlayer > neededChipsOpponent){
              //if the player has a better score than the opponent
              //and the opponent needs more than 4 chips to complete
              //a network
              if(neededChipsOpponent > 4){
                  //check who has more network blocks available
                  if(chipsOnBoardPlayer > chipsOnBoardOpponent){
                      //if the player has more network blocks available
                      //expand (neutral strategy)
                      addToScore = 10;
                  }else if(chipsOnBoardPlayer == chipsOnBoardOpponent){
                      //if player and opponent have equal number of blocks available
                      //expand (neutral strategy)
                      addToScore = 9;
                  }else{
                      //if opponent has more network blocks available 
                      //complete network as soon as possible
                      //minimum 6 chips (offensive strategy)
                      addToScore = 8;
                  }
              }else if(neededChipsOpponent == 4){
                  //check who has more network blocks available
                  if(chipsOnBoardPlayer > chipsOnBoardOpponent){
                      //if the player has more network blocks available
                      //complete network as soon as possible
                      //minimum 6 chips (offensive strategy)
                      addToScore = 7;
                  }else if(chipsOnBoardPlayer == chipsOnBoardOpponent){
                      //if player and opponent have equal number of blocks available
                      //complete network as soon as possible
                      //minimum 6 chips (offensive strategy)
                      addToScore = 6;
                  }else{
                      //if opponent has more network blocks available 
                      //block the opponent(defensive strategy)
                      addToScore = 5;
                  }
              }else if(neededChipsOpponent == 3){
                  //check who has more network blocks available
                  if(chipsOnBoardPlayer > chipsOnBoardOpponent){
                      //if the player has more network blocks available
                      //block the opponent(defensive strategy)
                      addToScore = 4;
                  }else if(chipsOnBoardPlayer == chipsOnBoardOpponent){
                      //if player and opponent have equal number of blocks available
                      //expand (neutral strategy), or maybe block the opponent??
                      addToScore = 3;
                  }else{
                      //if opponent has more network blocks available 
                      //block the opponent(defensive strategy)
                      addToScore = 2;
                  }
              }else {
              //else if opponent is missing 2 chips to complete his network
              //it means that player only needs 1 more chip to complete his network
              //because in this case player has a better score than opponent
              //So, we let minmax handle it
                  addToScore = -2;
              }
            }else if(neededChipsPlayer == neededChipsOpponent){
                //in case that there is a tie in scores and the game is unfinished 
                //(very odd situation because evaluate tries to be precise when
                //calculating the score)
                if(neededChipsPlayer >= 4){
                    //expand (neutral strategy)
                    addToScore = 3;
                }else if(neededChipsPlayer == 3){
                    //complete the network as soon as possible
                    //minimum 6 chips (offensive strategy)
                    addToScore = 2;
                }else if(neededChipsPlayer == 2){
                	//block the opponent (defensive strategy)
                	addToScore = 1;
                }else{
                	//if the player only needs one more chip 
                	//to complete a network, let minmax handle it
                	addToScore = -3;
                }
            }else {
              //if the player has a better score than the opponent
              //and the opponent needs more than 4 chips to complete
              //a network
              if(neededChipsOpponent > 4){
                  //check who has more network blocks available
                  if(chipsOnBoardPlayer > chipsOnBoardOpponent){
                      //if the player has more network blocks available
                      //expand (neutral strategy)
                      addToScore = 1;
                  }else if(chipsOnBoardPlayer == chipsOnBoardOpponent){
                      //if player and opponent have equal number of blocks available
                      //expand (neutral strategy)
                      addToScore = -1;
                  }else{
                      //if opponent has more network blocks available 
                      //complete network as soon as possible
                      //minimum 6 chips (offensive strategy)
                      addToScore = -2;
                  }
              } else if(neededChipsOpponent == 4){
                  //check who has more network blocks
                  if(chipsOnBoardPlayer > chipsOnBoardOpponent){
                      //if the player has more network blocks available
                      //complete network as soon as possible
                      //minimum 6 chips (offensive strategy)
                      addToScore = -1;
                  }else if(chipsOnBoardPlayer == chipsOnBoardOpponent){
                      //if player and opponent have equal number of blocks available
                      //expand(neutral strategy)
                      addToScore = -2;
                  }else{
                      //if opponent has more network blocks available 
                      //complete network as soon as possible
                      //minimum 6 chips (offensive strategy)
                      addToScore = -3;
                  }
              } else if(neededChipsOpponent == 3 || neededChipsOpponent == 2){
                      //block the opponent(defensive strategy)
                      addToScore = -3;
              }else{
              //else if opponent is missing 2 chips to complete his network
              //it means that player only needs 1 more chip to complete his network
              //because in this case player has a better score than opponent
              //So, we let minmax handle it
                      addToScore = -4;
              }
           }
        return addToScore;
    }

     public int missingChips(int player) {
        int count = 0;
        Chip chip = new Chip(player, 0, 0, new ChipList());
        GameBoard gb = this.copy();
        for (int j = 0; j < DIMENSION; j++) {
            for (int i = 0; i < DIMENSION; i++) {
                if (!gb.hasChip(i, j)) {
                    gb.placeChip(chip, i, j);
                    if (gb.hasNetwork(player)) {
                        count++;
                    }
                    gb.removeChip(i, j);
                }
            }
        }
        return count;
    }


    /**
     *  Removes a Chip from the board (resets it).
     *  If there is no chip in the spot, do nothing.
     *  It calls the Chip's reset method so the Chip does not reference that spot anymore.
     */
    public void removeChip(int x, int y) {
        if (hasChip(x, y)) {
            board[y][x].reset();
            board[y][x] = null;
        }
    }

    /**
     *  Returns the chip at coordinate (x, y) of the Gameboard.
     *  If There is no Chip it returns null.
     */
    public Chip getChip(int x, int y) {
        if (hasChip(x, y)) {
            return board[y][x];
        } else {
            return null;
        }
    }


    /**
     *  Creates a copy of the Gameboard and all the chips that are on it.
     *  It should not change the original Gameboard.
     */

    public void copy(GameBoard g) {
        Chip c;
        for (int j = 0; j < board.length; j++) {
            for (int i = 0; i < board.length; i++) {
                if (g.hasChip(i, j)) {
                    c = (g.getChip(i, j)).copy();
                    this.placeChip(c, i, j);
                }
            }
        }
    }

    public GameBoard copy() {
		GameBoard copy = new GameBoard(new ChipList(player1.player()), new ChipList(player2.player()));
		Chip player1chip = player1.front();
		Chip copy1;
		Chip player2chip = player2.front();
		Chip copy2;
		while(player1chip.isValid()) {
			copy1 = new Chip(player1.player(), 0, 0, copy.player1);
			copy2 = new Chip(player2.player(), 0, 0, copy.player2);
			if(player1chip.x()!=0 || player1chip.y()!=0) {
				copy.placeChip(copy1, player1chip.x(), player1chip.y());
			}
			if(player2chip.x()!=0 || player2chip.y()!=0) {
				copy.placeChip(copy2, player2chip.x(), player2chip.y());
			}
			copy.player1.insertBack(copy1);
			copy.player2.insertBack(copy2);
			player1chip = player1chip.next();
			player2chip = player2chip.next();
		}
		
		if(player1.getChipsLeft()!=copy.player1.getChipsLeft() || player2.getChipsLeft()!=copy.player2.getChipsLeft()) {
			System.out.println("FALSE");
		}
		
		return copy;
    }


    /**
     *  Helper function for findConnections() and hasNetwork().
     *  Returns the Chips in the starting goal area of the player.
     *  White's start goals = board[0][n] for any n greater than 0 and less than 7.
     *  Black's start goals = board[n][0] for any n greater than 0 and less than 7.
     */
    public ChipList startGoal(int player) {
        ChipList lst = new ChipList();
        Chip c;
        if (player == 0) {
            for (int i = 1; i < board.length - 1; i++) {
                if (hasChip(i, 0)) {
                    c = getChip(i, 0);
                    lst.insertFront(c.copy());
                }
            }
        } else {
            for (int j = 1; j < board.length - 1; j++) {
                if (hasChip(0, j)) {
                    c = getChip(0, j);
                    lst.insertFront(c.copy());
                }
            }
        }
        return lst;
    }

    /**
     *  Helper function for findConnections() and hasNetwork().
     *  Returns whether a Chip is in the starting goal area or not.
     */
    public boolean inStart(Chip c) {
        int x = c.x();
        int y = c.y();
        int player = c.player();
        if (player == 0) {
            return y == 0;
        } else {
            return x == 0;
        }
    }

    /**
     * Helper function for findConnections() and hasNetwork().
     * Checks if a chip is in the opposite goal.
     * Black is 0, and goals are N --> S ends of the board.
     * White is 1, and goals are W --> E ends of the board.
     */
    public boolean inGoal(Chip c) {
        int x = c.x();
        int y = c.y();
        int player = c.player();
        if (player == 0) {
            return x != 0 && x != 7 && y == 7;
        } else {
            return x == 7 && y != 0 && y != 7;
        }
    }

    /**  
     *  findConnections takes in a Chip c and looks for all connected chips of the same color.
     *  It assigns each connected chip a direction and returns connected chips in a ChipList.
     *
     *   --- --- ---         -------------- -------------- --------------   
     *  | 1 | 2 | 3 |       | x - 1, y - 1 |   x, y - 1   | x + 1, y - 1 |
     *   --- --- ---         -------------- -------------- --------------
     *  | 4 | @ | 5 |   ==  |   x - 1, y   | Chip c (x,y) |   x + 1, y   |
     *   --- --- ---         -------------- -------------- --------------
     *  | 6 | 7 | 8 |       | x - 1, y + 1 |   x, y + 1   | x + 1, y + 1 |
     *   --- --- ---         -------------- -------------- --------------
     *
     */
    public ChipList findConnections(Chip c) {
        Chip n;
        ChipList lst = new ChipList();
        int x = c.x();
        int y = c.y();
        int player = c.player();
        int x0 = x - 1;
        int y0 = y - 1;
        while (x0 > 0 && y0 > 0) {
            if (hasChip(x0, y0)) {
                if (getChip(x0, y0).player() == player) {
                    n = getChip(x0, y0).copy();
                    n.setDirection(1);
                    lst.insertFront(n);
                }
                break;
            }
            x0--;
            y0--;
        }
        int y1 = y - 1;
        while (y1 >= 0) {
            if (hasChip(x, y1)) {
                if (getChip(x, y1).player() == player) {
                    n = getChip(x, y1).copy();
                    n.setDirection(2);
                    lst.insertFront(n);
                }
                break;
            }
            y1--;
        }
        int x2 = x + 1;
        int y2 = y - 1;
        while (x2 < board.length && y2 >= 0) {
            if (hasChip(x2, y2)) {
                if (getChip(x2, y2).player() == player) {
                    n = getChip(x2, y2).copy();
                    n.setDirection(3);
                    lst.insertFront(n);
                }
                break;
            }
            x2++;
            y2--;
        }
        int x3 = x - 1;
        while (x3 >= 0) {
            if (hasChip(x3, y)) {
                if (getChip(x3, y).player() == player) {
                    n = getChip(x3, y).copy();
                    n.setDirection(4);
                    lst.insertFront(n);
                }
                break;
            }
            x3--;
        }
        int x4 = x + 1;
        while (x4 < board.length) {
            if (hasChip(x4, y)) {
                if (getChip(x4, y).player() == player) {
                    n = getChip(x4, y).copy();
                    n.setDirection(5);
                    lst.insertFront(n);
                }
                break;
            }
            x4++;
        }
        int x5 = x - 1;
        int y5 = y + 1;
        while (x5 >= 0 && y5 < board.length) {
            if (hasChip(x5, y5)) {
                if (getChip(x5, y5).player() == player) {
                    n = getChip(x5, y5).copy();
                    n.setDirection(6);
                    lst.insertFront(n);
                }
                break;
            }
            x5--;
            y5++;
        }
        int y6 = y + 1;
        while (y6 < board.length) {
            if (hasChip(x, y6)) {
                if (getChip(x, y6).player() == player) {
                    n = getChip(x, y6).copy();
                    n.setDirection(7);
                    lst.insertFront(n);
                }
                break;
            }
            y6++;
        }
        int x7 = x + 1;
        int y7 = y + 1;
        while (x7 < board.length && y7 < board.length) {
            if (hasChip(x7, y7)) {
                if (getChip(x7, y7).player() == player) {
                    n = getChip(x7, y7).copy();
                    n.setDirection(8);
                    lst.insertFront(n);
                }
                break;
            }
            x7++;
            y7++;
        }
        return lst;
    }

    /**
     *  Checks to see if a player has a network on the Gameboard.
     *  @param player is the player passed in.
     */ 
    public boolean hasNetwork(int player) {
        return networkHelper(player, null, null);
    }

    /**
     *  Helper function for hasNetwork
     *  @param player is the player passed in.
     *  @param c is the chip it searches for a connection on.
     *  @param count is the number of chips in the network built so far.
     *  @param lst is the list of Chips in the network so far
     */
 
     private boolean networkHelper(int player, Chip c, ChipList lst) {
        boolean network = false;
        if (c == null) {
            ChipList strt = startGoal(player);
            if (strt.isEmpty()) {
                return false;
            } else {
                Chip curr = strt.front();
                while (curr.isValid()) {
                    ChipList current = new ChipList();
                    network = networkHelper(player, curr, current);
                    if (network == true) {
                        return network;
                    }
                    curr = curr.next();
                }
            }
        } else {
            if (inGoal(c)) {
                if (lst.length() + 1 >= 6) {
                    return true;
                }
                return false;
            }
            ChipList connections = findConnections(c);
            if (connections.isEmpty()) {
                return false;
            }
            ChipList nwlst = lst.copy();
            nwlst.insertFront(c.copy());
            Chip cur = connections.front();
            while (cur.isValid()) {
                if (cur.direction() != c.direction() && !lst.inList(cur) && !inStart(cur)) {
                    network = networkHelper(player, cur, nwlst);
                    if (network == true) {
                        return network;
                    }
                }
                cur = cur.next();
            }
        }
        return network;
    }


    public String toString() {
        String divide = "\n" + "---------------------------------" + "\n";
        String s = "" + divide;
        for (int j = 0; j < board.length; j++) {
            s = s + "|";
            for (int i = 0; i < board.length; i++) {
                if ((i == 0 && j == 0) || (i == 0 && j == 7) || (i == 7 && j == 0) || (i == 7 && j == 7)) {
                    s = s + " X |";
                } else if (board[j][i] == null) {
                    s = s + "   |";
                } else {
                    s = s + " " + board[j][i].player() + " |";
                }
            }
            s = s + divide;
        }
        return s;
    }

    public static void main (String[] args) {
        GameBoard game = new GameBoard();
        ChipList black = new ChipList();
        Chip c20 = new Chip(0, 2, 0);
        black.insertFront(c20);
        Chip c25 = new Chip(0, 2, 5);
        black.insertFront(c25);
        Chip c35 = new Chip(0, 3, 5);
        black.insertFront(c35);
        Chip c13 = new Chip(0, 1, 3);
        black.insertFront(c13);
        Chip c33 = new Chip(0, 3, 3);
        black.insertFront(c33);
        Chip c55 = new Chip(0, 5, 5);
        black.insertFront(c55);
        Chip c57 = new Chip(0, 5, 7);
        black.insertFront(c57);

        game.placeChip(c20, 2, 0);
        game.placeChip(c25, 2, 5);
        game.placeChip(c35, 3, 5);
        game.placeChip(c13, 1, 3);
        game.placeChip(c33, 3, 3);
        game.placeChip(c55, 5, 5);
        game.placeChip(c57, 5, 7);
        System.out.println(black + "size: " + black.length());
        System.out.println(game);

        ChipList connect35 = game.findConnections(c35);
        System.out.println(connect35);

        ChipList blackstart = game.startGoal(0);
        System.out.println(blackstart);

        System.out.println(game.findConnections(c20));

        GameBoard gb = new GameBoard();
        gb.copy(game);
        System.out.println(gb);

        System.out.println(game.hasNetwork(0));

        gb.removeChip(2, 0);
        gb.removeChip(2, 5);
        gb.removeChip(1, 3);

        Chip c60 = new Chip(0, 6, 0);
        Chip c65 = new Chip(0, 6, 5);

        gb.placeChip(c60, 6, 0);
        gb.placeChip(c65, 6, 5);

        System.out.println(gb);
        System.out.println(game);

        System.out.println(gb.hasNetwork(0));

        Chip w62 = new Chip(1, 6, 2);
        gb.placeChip(w62, 6, 2);

        System.out.println(gb);
        System.out.println(gb.hasNetwork(0));

    }

}
