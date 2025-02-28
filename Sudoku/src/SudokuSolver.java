/*
 * Introduction to Arificial Intelligence
 * Dr. Arisoa Randrianasolo
 * Sudoku Implementation by Wes Orr
 * 02/10/2025
 */

// #region Required Libraries
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
// #endregion

public class SudokuSolver {
    
    // #region Problem Notes
    /* 
     * Constraint Satisfaction Problem
     * - Variables: Each unsolved tile (81 total tiles).
     * - Domains: {1, 2, 3, ... 8, 9}
     * - Constraints: No duplicates in rows, columns, or 3x3 boxes.
     *      * Indexing Approach:
     *          - Rows Identified By 100's place.
     *          - Columns Identified By 1's place.
     *          - 3x3 Box Identified By 10's place.
     *      860 861 862 | 873 874 875 | 886 887 888 <-- Each 3-digit # is individual index. 
     *      760 761 762 | 773 774 775 | 786 787 787
     *      ... ... ... . ... ... ... . ... ... ...
     *      000 001 002 | 013 014 015 | 026 027 028
     * Steps:
     * - Determine methodology for constraint representation
     * - Write algorithm to enforce node & arc-consistency.
     * - Write backtracking algorithm.
     *      * Optimize w/ node-picking algorithms.
     * 
     * For help understanding underlying principles of a CSP, see: https://youtube.com/watch?v=5NgNicANyqM&si=AnpDYiqBcbwV6STG&t=23100
     */
    // #endregion
    
    // #region Constant Variables
    public static final int NUM_ROWS = 9;
    public static final int NUM_COLUMNS = 9;
    // #endregion
    
    // #region Primary Method
    public String Solve(String stateString){
       
        SudokuCSP mainCSP = new SudokuCSP();                        // Instantiate the CSP, which creates a blank grid of tile objects and their constraints.
        
        State initialState = new State(stateString);                // Wrap the input into a state object (merely for conceptual abstraction).
        mainCSP.setGrid(initialState);                              // Use the values from the input state to populate the grid.
        
        mainCSP.AC_3();                                             // From the initial values, reduce the domains of unknown tiles uses the AC_3 algorithm.
        mainCSP.backTrackSearch(new TileAssignment(mainCSP.grid));  // Search for possible solutions. When one is found, the values are stored in mainCSP.grid.
        mainCSP.printGrid();                                        // Output to CL for verification (DEBUG).

        // Return the state representation.
        return mainCSP.gridToStringState();                         // Convert the CSP grid back to a 1D string representation for UI to interpret.
    }

    // #region Helper Classes
    
    // A wrapper class for a list of tiles and their current values.
    public final static class TileAssignment
    {   
        // Null Tile Assignment Object -- avoid recreating empty TileAssignments when unessecessary. 
        public static final TileAssignment NULL_ASSIGNMENT = new TileAssignment(Collections.emptyList());
        
        // Attributes
        private List<SudokuTile> tiles;

        // Constructor                                  
        public TileAssignment(List<SudokuTile> tiles) { this.tiles = tiles; }
        
        // Getter methods.
        public List<SudokuTile> getTiles() {return tiles; }

        // Method
        public boolean isComplete()
        {     
            if (this == TileAssignment.NULL_ASSIGNMENT)     // If somehow the null assignment Tile Assignment is passed through, do not attempt to iterate through its tiles.
                return false;
            
            for (SudokuTile tile : tiles)                   // Iterate through tiles...
                if (tile.tileValue == 0)                    // If any's value is zero,
                    return false;                           // then the assignment is incomplete.
            
            return true;                                    // Otherwise it is! 
        }
    }

    // A wrapper class for each of the major components of the CSP problem.
    public static class SudokuCSP
    {
        // Attributes
        List<SudokuTile> grid;                          // Holds all of the tiles (variables) of the Sudoku (CSP) problem.
        Map<SudokuTile, List<SudokuTile>> constraints;  // Each tile (a key) has an associated list of other tiles. Each "other tile" is a tile "this tile" cannot match. 
        List<SudokuCSP_Arc> allArcs;                    // Contains all pairs of related tiles (in same row, column, 3x3). Alternative representation of the above constraints.

        // Constructors
        public SudokuCSP()
        {
            // Instantiate the Grid & Constraints list.
            grid = new ArrayList<>();                                               // Used for creation of constraints, but mostly for debugging & visualization.
            constraints = new HashMap<>();                                          // Used by both AC_3 & Backtracking
            allArcs = new ArrayList<>();                                            // Used primarily by AC_3

            // Iterate over each row and column, creating a new tile, adding it to the grid
            int _3x3_No;                                                            // Keeps track of which 3x3 box we are in. 6 7 8
            for (int row = NUM_ROWS-1; row >= 0; row--)                                                                     // 3 4 5
                for (int column = 0; column < NUM_COLUMNS; column++)                                                        // 0 1 2
                  { _3x3_No = (column / 3) + (row / 3 * 3);                         // Somehow this formula works :D. Try it if you doubt it. I love integer division.
                    int newTileID = (row * 100) + (_3x3_No * 10) + (column * 1);    // Parenthesis for clarity. See indexing approach above for explanation.
                    SudokuTile newTile = new SudokuTile(newTileID);                 // Create the new tile.
                    grid.add(newTile);                                              // Add the tile to the variables list.
                    constraints.put(newTile, new ArrayList<>()); }                  // Create a constraint entry to be edited below.
            
            // For each tile in the grid, iterate over every other tile.
            for (SudokuTile tileA : grid)
              { List<SudokuTile> tileA_Constraints = constraints.get(tileA);
                for (SudokuTile tileB : grid)
                  { if (tileA.tileID == tileB.tileID)                               // Check if the "other tile" is the current tile.
                        continue;                                                   // If so, skip (because we don't want to create a constraint against itself).
                    
                    // See indexing approach in problem notes above for in-depth explanation.
                    if ((tileA.tileID / 100 % 10) == (tileB.tileID / 100 % 10) ||   // Check if rows are the same.
                        (tileA.tileID / 10 % 10) == (tileB.tileID / 10 % 10) ||     // Check if 3x3 boxes are the same.
                        (tileA.tileID % 10) == (tileB.tileID % 10))                 // Check if columns are the same.
                      { tileA_Constraints.add(tileB);                               // If any are the same, add the constraint to the map and
                        allArcs.add(new SudokuCSP_Arc(tileA, tileB)); }}}           // create a new arc representing this constraint.
        }  

        // #region CSP Primary Methods

        public void setGrid(State state) {                                                                                                                        //   Row     Indices
            for (int row = 0; row < NUM_ROWS; row++) {                                          // Iterate through the rows:                                           1       4 5 6 7
                for (int column = 0; column < NUM_COLUMNS; column++) {                          // Iterate through the columns:                                        0       0 1 2 3
                    int index = row * NUM_COLUMNS + column;                                     // This is a commonly used formula for indexing in this program: EX:   Column  0 1 2 3  NUM_COLUMNS = 4
                    char currentCharacter = state.stringState.charAt(index);                    // Retrieve the character at the current index in the state string (which is constant-time run-time).
                    if (currentCharacter != '_')                                                // If the input character is not "_", then update the grids value, otherwise leave it at 0.
                        grid.get(index).setTileValue(Integer.parseInt(currentCharacter + ""));  // IMPORTANT NOTE: setting a tileValue auto-constrains the domain (see SudokuTile class).
                }   
            }
        }

        public String gridToStringState()
        {
            String resultantStringState = "";
            for (int row = 0; row < NUM_ROWS; row++) {                          // Iterate through the rows:
                for (int column = 0; column < NUM_COLUMNS; column++) {          // Iterate through the rows:
                    int index = row * NUM_COLUMNS + column;                     // See example in setGrid method comments.
                    resultantStringState += grid.get(index).tileValue;          // Grab the appropriate value in the grid and append it to the resultant string.
                }   
            }
            return resultantStringState;
        }

        // Enforces arc consistency between two tiles in the CSP (helper method for AC_3).
        private boolean reviseDomain(SudokuTile tileA, SudokuTile tileB)
        {
            boolean tileA_DomainRevised = false;                    // Was the domain of tileA revised by this method?
            
            if (!constraints.get(tileA).contains(tileB))            // Check if there is any constraint between tileA and tileB.
                return false;                                       // If there is no constraint, no change is necessary, thus return false.
            
            Set<Integer> domainValuesToRemove = new HashSet<>();    // This is necessary to circumvent a Concurrent_Modification_Exception (removing from a set while iterating over it).
            
            // If there is a constraint, check if at least one arrangement of values between the tiles is satisfactory.
            for (Integer a : tileA.tileDomain)
            { boolean satisfactoryArrangement = false;              // Until we find two possible values between the tiles that satisfy the constraint, this remains false.
                for (Integer b : tileB.tileDomain)
                { if (!Objects.equals(a, b))                        // Are there two different values in each domain?
                    { satisfactoryArrangement = true;               // If so, YAY!, set the flag.
                        break; }}                                   // No need to keep checking for this value (a) of tileA.
                if (satisfactoryArrangement)                        // If the current value (a) satisfies the constraint,
                    continue;                                       // skip the next steps...
                
                domainValuesToRemove.add(a);                        // Otherwise add the value to another set containing the value to remove and
                tileA_DomainRevised = true; }                       // set the revised flag.
                
                // Remove the values previously marked to be removed from tileA's domain.
                for (Integer valueToRemove : domainValuesToRemove)
                    tileA.tileDomain.remove(valueToRemove);

            return tileA_DomainRevised;
        }

        // Enforces arc consistency across all tiles in the CSP.
        public boolean AC_3()
        {
            Queue<SudokuCSP_Arc> arcQueue = new LinkedList<>(allArcs);                          // Copy & convert allArcs to a queue.
            
            while(!arcQueue.isEmpty())                                                          // Iterate through all arcs.
              { SudokuCSP_Arc currentArc = arcQueue.poll();                                     // Dequeue the current arc.
                
                if (reviseDomain(currentArc.tileA, currentArc.tileB))                           // Revise the domain of the first tile in the arc, check if any revision was made.
                  { if (currentArc.tileA.tileDomain.isEmpty())                                  // If after revision, there is nothing more in tileA's domain, then the problem is unsolvable.
                        return false;                                                           
                    
                    List<SudokuTile> tileA_Neigbors = new ArrayList<>(constraints.get(currentArc.tileA));   // Retrieve the neighbors of tileA (values that share a constraint).
                    tileA_Neigbors.remove(currentArc.tileB);                                                // Disclude the other tile in the currently focused arc.
                    
                    for (SudokuTile tileC : tileA_Neigbors)                                     // Iterate through each of the neighbors, re-adding them to the queue
                        arcQueue.add(new SudokuCSP_Arc(tileC, currentArc.tileA)); }}            // because changing tileA may have reprecussions on those too.

            return true;                                                                        // AC_3 was successful in reducing domains.
        }

        // Checks if a potential new tile assignment is consistent with all the constraints on the tile being assigned.
        public boolean isValidAssignment(SudokuTile potentialTileAssignment)
        {
            List<SudokuTile> potentialTileConstraints = constraints.get(potentialTileAssignment);   // Retrieve all the constraints related to the new potentialTile that is to be assigned.
            
            for (SudokuTile tileConstraint : potentialTileConstraints)                              // Iterate through those constraints (which are essentially references to other tiles and their values)...
                if (potentialTileAssignment.tileValue == tileConstraint.tileValue)                  // If any values match, then the assignment is invalid.
                    return false;
            return true;                                                                            // Otherwise all good!
        }

        // Traverses the CSP graph and assigns values to each variable.
        public TileAssignment backTrackSearch(TileAssignment assignment)
        {            
            // Check if every tile in the assignment is set (non-zero).
            if (assignment.isComplete())
                return assignment;
            
            SudokuTile currentTile = selectUnassignedTile(assignment);                                          // Choose any unnassigned tile... this could certainly be optimized, but its affect on 9x9 Sudoku is neglible.
            
            for (Integer domainValue : domainValues(currentTile, assignment))                                   // Iterate through each of the possible values of the assignment...
              { currentTile.setTileValue(domainValue);                                                          // Try each one!
                if (isValidAssignment(currentTile))                                                             // Ensure this value is valid (doesn't violate any constraints with already assigned tiles).
                  { TileAssignment resultantAssignment = backTrackSearch(assignment);                           // If so, recursively continue searching until success,
                    if (resultantAssignment != TileAssignment.NULL_ASSIGNMENT)                                  // returning the final assignment if its not null (which is a static empty assignment list used to optimize memory usage)
                        return resultantAssignment; }                                                           //                                                  ... see the TileAssignment class attribute.
                currentTile.resetTileValue(); }                                                                 // Restore the tile if the assignment did not work.
            
            return TileAssignment.NULL_ASSIGNMENT;                                                              // Return a special Tile Assignment object that can be compared against without creating a new object every time.
        }

        // The following two functions can be optimizes for better backtracking performance, but its neglible for 9x9 Sudoku.
        public List<Integer> domainValues(SudokuTile tile, TileAssignment assignment) 
        { 
            return List.copyOf(tile.tileDomain);
        }
        public SudokuTile selectUnassignedTile(TileAssignment assignment) 
        {
            for (SudokuTile tile : assignment.tiles)
                if (tile.tileValue == 0)
                    return tile;
            
            return null;
        }
        // #endregion

        // #region Helper Methods
        public void printGrid() {
            for (int row = 0; row < NUM_ROWS; row++)                                            // Iterate through the rows:
              { if ((row-1) % 3 == 2 && row != 8) System.out.println("-".repeat(100));    // Print horizontal dividing lines.
                for (int column = 0; column < NUM_COLUMNS; column++)                            // Iterate through the columns:
                  { int index = row * NUM_COLUMNS + column;                                     // Calculate index in grid list.
                    System.out.print(String.format("%9s", grid.get(index)));             // Print each tile.
                    if (column % 3 == 2) System.out.print(" | "); }                           // Print vertical dividing lines. 
                System.out.println(); }                                                         // Print a new line after each row
        }

        public void printConstraints()
        {
            System.out.println("Constraints:");
            for (SudokuTile key : constraints.keySet())
                System.out.println(String.format("Tile %03d: %s\n", key.tileID, constraints.get(key)));
        }
    }

    //#region Helper Classes
    // A pair of Suduko tiles used by the CSP.
    public static class SudokuCSP_Arc
    {
        // Attributes
        SudokuTile tileA;
        SudokuTile tileB;

        // Constructors
        public SudokuCSP_Arc(){} // Default Constructor
        public SudokuCSP_Arc(SudokuTile tileA, SudokuTile tileB)
        {
            this.tileA = tileA;
            this.tileB = tileB;
        }

        @Override
        public String toString() { return String.format("(%s, %s)", tileA, tileB); }
    }

    // One individual tile in the suduko grid:
    public static class SudokuTile
    {
        // Class Attributes
        public static Set<Integer> initialTileDomain = new HashSet<>() {{
            add(1);
            add(2);
            add(3);
            add(4);
            add(5);
            add(6);
            add(7);
            add(8);
            add(9);
        }};
        
        // Attributes
        public int tileID;
        public int tileValue;
        public Set<Integer> tileDomain = new HashSet<>(SudokuTile.initialTileDomain);

        // Constuctors
        public SudokuTile() {} // Default Constructor
        public SudokuTile(int tileID) { this(tileID, 0); }
        public SudokuTile(int tileID, int tileValue)
        {
            this.tileID = tileID;
            this.tileValue = tileValue;
            if (tileValue != 0)
                this.tileDomain = new HashSet<>() {{ add(tileValue); }};
        }

        public SudokuTile setTileValue(int newTileValue)
        {
            this.tileValue = newTileValue;
            this.tileDomain = new HashSet<>() {{ add(newTileValue); }};     // Auto-update the domain... the only possible value for this tile is its actual value. 
            return this;                                                    // Makes it possible to chain if necessary.
        }

        public SudokuTile resetTileValue()
        {
            this.tileValue = 0;                                             // Use 0 to represent unset.
            this.tileDomain = new HashSet<>(SudokuTile.initialTileDomain);  // Reset the domain to all possible values.     ** Now realizing this is actually a flaw, but not dangerous to the program, just efficiency.
            return this;
        }

        // Object class-method overrides:
        @Override
        public boolean equals(Object obj)
        {
            if (obj instanceof SudokuTile tile)
                return this.tileID == tile.tileID;
            return false;
        }
        @Override
        public int hashCode() { return Integer.hashCode(tileID); }
        @Override
        public String toString() { return String.format("(%03d: %d)", tileID, tileValue); }
    }

    // A wrapper class for a string-represented state.
    public static class State
    {
        // Attributes
        public String stringState;

        // Constructors
        public State() {} // Default Constructor
        public State(String stringState) { this.stringState = stringState; }

        // Methods
        public void printState(int numCols)
        {
            for (int i = 0; i < stringState.length(); i++)
              { System.out.print(stringState.charAt(i) + " ");   // Print the current element.
                if ((i+1) % numCols == 0)                        // Newline when at the end of the row.
                    System.out.println(); }
        }
    }
    // #endregion
}
