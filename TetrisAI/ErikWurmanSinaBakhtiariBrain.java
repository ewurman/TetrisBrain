/*
Features to add (maybe):

    surface area instead of roughness
    It's good if a block is touching either wall or the floor





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

import java.lang.Math;

public class ErikWurmanSinaBakhtiariBrain implements Brain {
    /*
    Given a piece and a board, returns a move object that represents
    the best play for that piece, or returns null if no play is possible.
    See the Brain interface for details.
    */

    private double mh;
    private double tw;
    private double h; 
    private double r; 
    private double ah;


    public ErikWurmanSinaBakhtiariBrain(){

        mh = .4;
        tw = -3.4;
        h = 0.5;
        r = 4.4;
        ah = 23.8;
    }


    public ErikWurmanSinaBakhtiariBrain(double maxHeight, double touchingWall, double holes, double roughness, double aggregateHeight){

        mh = maxHeight;
        tw = touchingWall;
        h = holes;
        r = roughness;
        ah = aggregateHeight;
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
    Counts the number of pieces touching both the walls.
    */
    public int touchingWall(Board board){
        final int width = board.getWidth();
        final int max = board.getMaxHeight();
        int touching = 0;
        for (int col = 0; col<width; col++){
            if (col == 0 || col == (width-1)){
                for (int i=0; i<max; i++){
                    if (board.getGrid(col, i)){
                        touching++;
                    }
                }
            }
        }
        return touching;
    }

    /*
    Sums the total of the absolute values of adjacent columns
    */
    public int roughness(Board board){
        final int width = board.getWidth();
        int roughness = 0;
        for (int col = 0; col<width-1; col++){
            int curr = board.getColumnHeight(col);
            int next = board.getColumnHeight(col+1);
            roughness += Math.abs(curr-next);
        }
        return roughness;
    }

    /*
    Counts the total number of holes on the board
    */
    public int holes(Board board){
        final int width = board.getWidth();
        int holes = 0;
        for (int col=0; col<width; col++){
            int row = board.getColumnHeight(col) - 2;
            while (row>=0){
                if (!board.getGrid(col,row)){
                    holes += 1; //maybe make this 2
                }
                row--;
            }
        }
        return holes;
    }

    /*
    Total height of all the columns on the board
    */
    public int aggregateHeight(Board board){
        final int width = board.getWidth();
        int height = 0;
        for (int col=0; col<width; col++){
            height += board.getColumnHeight(col);
        }
        return height;
    }


    public double rateBoard(Board board) {
        final int width = board.getWidth();

        final int max = board.getMaxHeight();        
        int holes = holes(board);
        int roughness = roughness(board);
        //int roughness = surfaceArea(board);
        int height = aggregateHeight(board);
        int touching = touchingWall(board);

        //int holes = countHolesByEriksDefinition(board);
        //int blockades = BlockadesBySinasDefinition(board);
        //int blocks = countBlocks(board);
        //double avgHeight = ((double)sumHeight)/width;
        //int heightRange = heightRange(board);
      
        // Add up the counts to make an overall score
        return (mh*max + tw*touching + h*holes + r*roughness + ah*height); 
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


public int surfaceArea(Board board){
        // Calculate the surface area of touching the top open area
        // right now doesn't recurse into caves below itself.
        final int width = board.getWidth();
        int sa = 0;
        for (int col = 0; col<width; col++){
            int curr_y = board.getColumnHeight(col)-1;
            // stop when less than left and right heights

            if (col == 0) {
                int next = board.getColumnHeight(col+1);
                sa += Math.max(curr-next, 0) + 1; // one is straight up
            }
            else if (col == width - 1) {
                int last = board.getColumnHeight(col-1);
                sa += Math.max(curr-last, 0) + 1; // one is straight up
            }
            else {
                int next = board.getColumnHeight(col+1);
                int last = board.getColumnHeight(col-1);

                sa += Math.max(Math.max(curr-next, curr-last), 0) + 1;
            }
        }
        return sa;
    }





/*
    public int surfaceArea(Board board){
        // Calculate the surface area of touching the top open area
        // right now doesn't recurse into caves below itself.
        final int width = board.getWidth();
        int x = 0;
        int y = board.getColumnHeight(x);
        int sa = 0;
        while (x != width - 1 && y != board.getColumnHeight(width - 1)){
            if (!board.getGrid(x,y)){
                y--;
                continue;
            }
            if (y==0){
                x++;
                y = board.getColumnHeight(x);
                continue;
            }
            int next_x = x;
            int next_y = y;
            int up_y = y+1;
            int right_x = x+1;
            int left_x = x-1;
            int down_y = y-1;
            boolean totally_enclosed = true;
            //getGrid outside board always true
            if (!board.getGrid(x,up_y)){
                sa++;
                totally_enclosed = false;
            }
            if(!board.getGrid(right_x,y)){
                sa++;
                totally_enclosed = false;
            }
            if(!board.getGrid(left_x,y)){
                sa++;
                totally_enclosed = false;
            }
            //Now move down or start on next column
            if (!totally_enclosed){
                next_y--;
            }
            else {
                next_x++;
                next_y = board.getColumnHeight(next_x);
            }
            x = next_x;
            y = next_y;

        }
        return sa;
    }
*/













// Old features we decided not to use
//---------------------------------------------------------------------------------------------------------------------------------------





















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

    /*
    Blockade: a hole that is covered above

    Idea: at each column, see if there's any holes below the max block

    Returns: number of columns with a blockade (not all blockades)

    Improvements: 
    */
    public int blockadesBySinasDefinition(Board board){
        final int width = board.getWidth();
        int blockades = 0; //total number of blockades
        for (int col = 0; col<width; col++){
            boolean blockadeFlag = false; // boolean flag if a blockade is found in a column
            int y = board.getColumnHeight(col);

            if (y>0){ //ensure that you're not saying that an empty column has a blockade 
                while(y>=0){
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
        return blockades;
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
                    holes++; // This space is empty and less than the height of this column, count doubly
                    if (y < leftColHeight - 1){
                        holes++; //count another if at same height or less than left
                    }
                    if (y < rightColHeight - 1){
                        holes++; //count another if in a hole to the right
                    }
                }
                y--;
            }
        }
        return holes;
    }



}
