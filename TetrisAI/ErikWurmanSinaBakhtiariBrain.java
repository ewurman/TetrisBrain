/*
 A simple Brain implementation.
 bestMove() iterates through all the possible x values
 and rotations to play a particular piece (there are only
 around 10-30 ways to play a piece).
 
 For each play, it uses the rateBoard() message to rate how
 good the resulting board is and it just remembers the
 play with the lowest score. Undo() is used to back-out
 each play before trying the next. To experiment with writing your own
 brain -- just subclass off LameBrain and override rateBoard().
*/

public class ErikWurmanSinaBakhtiariBrain implements Brain {
    /*
    Given a piece and a board, returns a move object that represents
    the best play for that piece, or returns null if no play is possible.
    See the Brain interface for details.
    */

    public ErikWurmanSinaBakhtiariBrain(double maxHeight, double heightRange, double holes, double roughness, double blockades){
        mh = maxHeight;
        hr = heightRange;
        h = holes;
        r = roughness;
        b = blockades;
    }

    public Brain.Move bestMove(Board board, Piece piece, int limitHeight, Brain.Move move) {
        // Allocate a move object if necessary
        if (move==null) move = new Brain.Move();
  
        double bestScore = 1e20;
        int bestX = 0;
        int bestY = 0;
        Piece bestPiece = null;
        Piece current = piece;
  
        // loop through all the rotations
        while (true) {
            final int yBound = limitHeight - current.getHeight()+1;
            final int xBound = board.getWidth() - current.getWidth()+1;
   
            // For current rotation, try all the possible columns
            for (int x = 0; x<xBound; x++) {
                int y = board.dropHeight(current, x);
                if (y<yBound) { // piece does not stick up too far
                    int result = board.place(current, x, y);
                    if (result <= Board.PLACE_ROW_FILLED) {
                        if (result == Board.PLACE_ROW_FILLED) board.clearRows();
      
                        double score = rateBoard(board);
      
                        if (score<bestScore) {
                            bestScore = score;
                            bestX = x;
                            bestY = y;
                            bestPiece = current;
                        }
                    }
     
                    board.undo(); // back out that play, loop around for the next
                }
            }
   
            current = current.nextRotation();
            if (current == piece) break; // break if back to original rotation
        }
  
        if (bestPiece == null) return(null); // could not find a play at all!
        else {
            move.x=bestX;
            move.y=bestY;
            move.piece=bestPiece;
            move.score = bestScore;
            return(move);
        }
    }
 
 
    /*
    A simple brain function.
    Given a board, produce a number that rates
    that board position -- larger numbers for worse boards.
    This version just counts the height
    and the number of "holes" in the board.
    See Tetris-Architecture.html for brain ideas.

    Here are some features that hurt the board:
    - The number of blocks in the board (score increases when you fill a row)
    - The Max height
    - The average height
    - Bumpiness
    - The difference in the range of column hights
    - The Number of Holes
      A hole that is covered is worse,
      an uncovered hole further from the adjacent column heights is worse

        Sina's Hole Definition:
            a space is a hole if it has 2+ adjacent blocks (including all 3 boundaries)
            holes count for 1 if they are adjacent to 2 blocks,
            they count for 2 if they're adjacents to 3 blocks,
            and if they are blockaded (4 or 3 or 2 adjacent) then sina wants to figure out how to handle it.

        Erik's Hole Defiintion:
            a space is a hole if any or the left column, this column, or the right column 
            has a column height as high or higher than the current space.
            Holes count for as many of these 3 columns as are as tall or taller, so
              XXX                             X  
              X_X   Would count as 3 whereas  X_X  would count as 2
              XXX                             XXX


    */
    public int Roughness(){
        final int width = board.getWidth();
        int roughness = 0;
        for (int col; col<width-1; col++){
            curr = board.getColumnHeight(col);
            next = board.getColumnHeight(col+1);
            roughness += abs(curr-next);
        }
        return roughness
    }


    /*
    Blockade: a hole that is covered above

    Idea: at each column, see if there's any holes below the max block

    Returns: number of columns with a blockade (not all blockades)

    Improvements: 
    */
    public int BlockadesBySinasDefinition(Board board){
        final int width = board.getWidth();
        int blockades = 0; //total number of blockades
        for (int col = 0; col<width; col++){
            boolean blockadeFlag = false; // boolean flag if a blockade is found in a column
            int y = board.getColumnHeight(col);

            if (y>0){ //ensure that you're not saying that an empty column has a blockade 
                while(y<=0){
                    if (!board.getGrid(col,y) && !blockadeFlag){ // if hole and false flag
                        blockadeFlag = true;
                        blockades++;
                    }
                    if (!board.getGrid(col,y) && !blockadeFlag){ // if hole and true flag
                        // nothing
                    }
                    if (board.getGrid(col,y) && blockadeFlag){ // if not hole and true flag
                        blockadeFlag = false;
                    }
                    y--;
                }
            }
        }
        return blockades
    }

    public int heightRange(Board board){
        final int width = board.getWidth();
        int maxHeight = board.getMaxHeight();
        int minHeight = board.getMaxHeight();
        for (int col = 0; col < width; col++){
            final int colHeight = board.getColumnHeight(col);
            if (colHeight < minHeight){
                minHeight = colHeight;
            }
        }
        return maxHeight - minHeight;
    }

    public int countBlocks(Board board){
        final int width = board.getWidth();
        int blocks = 0;
        for (int x = 0; x < width; x++){
            final int colHeight = board.getColumnHeight(x);
            int y = colHeight;
            while (y>=0) {
                if  (board.getGrid(x,y)) {
                    blocks++;
                }
                y--;
            }
        }
        return blocks;
    }

    public int countHolesByEriksDefinition(Board board){
        final int width = board.getWidth();
        int holes = 0;
        for (int col = 0; col < width; col++){
            //edge cases are col == 0 and col == width - 1
            final int colHeight = board.getColumnHeight(col); //this getColumnHeight(x) function returns 1+y value of the column
            int leftColHeight = 0;
            int rightColHeight = 0;
            if (col > 0){
                leftColHeight = board.getColumnHeight(col-1);
            }
            if (col < width - 1){
                rightColHeight = board.getColumnHeight(col+1);
            }

            int heighestColOfThree = Math.max(leftColHeight, Math.max(colHeight, rightColHeight));
            int y = heighestColOfThree - 1; // addr of first possible hole is the y value of the highest column.

            while (y>=0) {
                if  (!board.getGrid(col,y)) {
                    holes+=2; // This space is empty and less than the height of this column, count doubly
                    if (y <= leftColHeight){
                        holes++; //count another if at same height or less than left
                    }
                    if (y <= rightColHeight){
                        holes++; //count another if ath the same height or less than right
                    }
                }
                y--;
            }
        }
        return holes;
    }


    public double rateBoard(Board board) {
        final int width = board.getWidth();
        final int maxHeight = board.getMaxHeight();
      
        int sumHeight = 0;
        int holes = 0;
      
        
        // Count the holes, and sum up the heights
        for (int x=0; x<width; x++) {
            final int colHeight = board.getColumnHeight(x);
            sumHeight += colHeight;
       
        }
        int holes = countHolesByEriksDefinition(board);
        int blockades = BlockadesBySinasDefinition(board);
        int roughness = Roughness(board);


        int blocks = countBlocks(board);
        int heightRange = heightRange(board);
        double avgHeight = ((double)sumHeight)/width;
      
        // Add up the counts to make an overall score
        // The weights, 8, 40, etc., are just made up numbers that appear to work
        return (mh*maxHeight + hr*heightRange + h*holes + r*roughness + b*blockades); 
    }




}
