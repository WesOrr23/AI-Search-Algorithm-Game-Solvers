import java.util.ArrayList;
import java.util.List;

public class minmaxag {

    // Attributes
    private int numRows;
    private int numCols;
    private int winSequenceLength;

    // Constructors
    public minmaxag(){} // Default Constructor
    public minmaxag(int numRows, int numCols, int winSequenceLength)
    {
        this.numRows = numRows;
        this.numCols = numCols;
        this.winSequenceLength = winSequenceLength;
    }

    // #region State Methods
    /// <summary>
    /// Given a current state, determines whose turn it is.
    /// </summary>
    /// <return> A single character denoting the player whose turn it is: X or Y </return>
    private char getPlayerTurn(State state)
    {
        int xCount = 0;
        int oCount = 0;
        
        // Iterate through each board position, tracking the total occurences of each player.
        for (char boardPosition : state.stringState.toCharArray())
            switch (boardPosition) 
              { case 'X' -> xCount++;
                case 'O' -> oCount++; }

        // Since 'X' always begins the game, if the totals are equal, it must be 'X's turn.
        return (xCount == oCount ? 'X' : 'O');
    }

    /// <summary>
    /// Given a state and corresponding action, determine the resultant state if that action were taken.
    /// </summary>
    /// <return> Returns the successor state </return>
    private State getSuccessorState(State state, Action action)
    {
        char[] result = state.stringState.toCharArray(); // Decompose the input state string into a character array,
        result[action.actionNo] = getPlayerTurn(state);  // Determine the player whose turn it would be, and given the input action, set the corresponding tile to that player.
        return new State(new String(result));   // Recompose the character array as a string and output as a state.
    }

    /// <summary>
    /// Determines if the input state is terminal, i.e. the game has been one by either player, or there are no more possible actions.
    /// Note: Inside the utility function, the terminalStateCheck flag is set so that it does not return a state desirability score if there is not absolute winner.
    /// </summary>
    /// <return> A boolean (True/False) if the state is terminal. </return>
    private boolean isTerminalState(State state) { return utility(state, true) != 0 || getAllActions(state).size() <= 0;}

    // Helper method for the utility method (SEE BELOW).
    private int updateCheckSum(int checkSum, char current_character)
    {
        // The return value is calculated such that sucessive characters of the same type compound the sum (either positive or negative).
        // Once a different character is identified, reset the checksum to that corresponding value.
        switch (current_character)
          { case 'X' -> checkSum = (checkSum <= 0 ? checkSum - 1 : -1);
            case 'O' -> checkSum = (checkSum >= 0 ? checkSum + 1 : 1);
            case '_' -> checkSum = 0; }
        
        return checkSum;
    }


    /// <summary>
    /// Determines the desirability of a given state, based on a consecutive-tile heuristic.
    /// </summary>
    /// <return> Returns the desirability score of the input state. </return>
    private int utility(State state) {return utility(state, false);}
    private int utility(State state, boolean terminalStateCheck)
    {
        int totalUtilityEvaluation = 0;  // Holds the desirability score for the input state.
        int checkSum = 0;   // An intermediate value that is used for each row, column, and diagonal.

        // Check each row for a winning/patterned sequence.
        for (int row = 0; row < numRows; row ++, checkSum = 0)
            for(int column = 0; column < numCols; column++)
            {   // See above helper function for explanation.
                int updatedCheckSum = updateCheckSum(checkSum, state.stringState.charAt(Coordinate.flattenCoordinates(column, row, numCols)));
                
                // If the new checksum's sign is different from the previous, if there is a pattern of 2 or more consecutive same-type tiles, add that to the desirability score.
                if (Math.signum(updatedCheckSum) != Math.signum(checkSum) && Math.abs(checkSum) != 1)
                    totalUtilityEvaluation += Math.signum(checkSum) * Math.pow(10, (Math.abs(checkSum)-1));
                checkSum = updatedCheckSum;

                // Check if there is an absolute win.
                if (Math.abs(checkSum) >= winSequenceLength) 
                    return (int) Math.signum(checkSum) * (Integer.MAX_VALUE-1); // If so, return immediate the largest possible desirability score.
            }
        
        // Check each column for a winning sequence.
        checkSum = 0;
        for (int column = 0; column < numCols; column++, checkSum = 0)
            for(int row = 0; row < numRows; row++)
            {   // See above helper function for explanation.
                int updatedCheckSum = updateCheckSum(checkSum, state.stringState.charAt(Coordinate.flattenCoordinates(column, row, numCols)));
                
                // If the new checksum's sign is different from the previous, if there is a pattern of 2 or more consecutive same-type tiles, add that to the desirability score.
                if (Math.signum(updatedCheckSum) != Math.signum(checkSum) && Math.abs(checkSum) != 1)
                    totalUtilityEvaluation += Math.signum(checkSum) * Math.pow(10, (Math.abs(checkSum)-1));
                checkSum = updatedCheckSum;

                // Check if there is an absolute win.
                if (Math.abs(checkSum) >= winSequenceLength) 
                    return (int) Math.signum(checkSum) * (Integer.MAX_VALUE-1); // If so, return immediate the largest possible desirability score.
            }

        // Check each top-left to bottom-right diagonal for a winning sequence.
        checkSum = 0;
        for (int row = 0; row < numRows; row++)
            for (int column = 0; column < numCols; column++, checkSum = 0)
                for (int diagonalStep = 0; diagonalStep + row < numRows && diagonalStep + column < numCols; diagonalStep++)
                  { // See above helper function for explanation. With each diagonal step iteration, move down and right once.
                    int updatedCheckSum = updateCheckSum(checkSum, state.stringState.charAt((row + diagonalStep) * numCols + column + diagonalStep));
                    
                    // If the new checksum's sign is different from the previous, if there is a pattern of 2 or more consecutive same-type tiles, add that to the desirability score.
                    if (Math.signum(updatedCheckSum) != Math.signum(checkSum) && Math.abs(checkSum) != 1)
                        totalUtilityEvaluation += Math.signum(checkSum) * Math.pow(10, (Math.abs(checkSum)-1));
                    checkSum = updatedCheckSum;

                    // Check if there is an absolute win.
                    if (Math.abs(checkSum) >= winSequenceLength) 
                        return (int) Math.signum(checkSum) * (Integer.MAX_VALUE-1); // If so, return immediate the largest possible desirability score.
                    }
        
        // Check the bottom-left to top-right diagonal for a winning sequence.
        checkSum = 0;
        for (int row = numRows - 1; row >= 0; row--)
            for (int column = 0; column < numCols; column++, checkSum = 0)
                for (int diagonalStep = 0; row - diagonalStep >= 0 && diagonalStep + column < numCols; diagonalStep++)
                  { // See above helper function for explanation. With each diagonal step iteration, move up and right once.
                    int updatedCheckSum = updateCheckSum(checkSum, state.stringState.charAt((row - diagonalStep) * numCols + column + diagonalStep));
                    
                    // If the new checksum's sign is different from the previous, if there is a pattern of 2 or more consecutive same-type tiles, add that to the desirability score.
                    if (Math.signum(updatedCheckSum) != Math.signum(checkSum) && Math.abs(checkSum) != 1)
                        totalUtilityEvaluation += Math.signum(checkSum) * Math.pow(10, (Math.abs(checkSum)-1));
                    checkSum = updatedCheckSum;

                    // Check if there is an absolute win.
                    if (Math.abs(checkSum) >= winSequenceLength) 
                        return (int) Math.signum(checkSum) * (Integer.MAX_VALUE-1); // If so, return immediate the largest possible desirability score.
                    }

        // If no winning sequence found & terminalStateCheck is set, return 0, which means draw or game is not over yet.
        if (terminalStateCheck)
            return 0;
        return totalUtilityEvaluation;  // Otherwise return the calculated desirability score.
    }

    // #region Action Methods
    /// <summary>
    /// Determines all of the possible actions given the input state.
    /// </summary>
    /// <return> Returns a list of actions to take. </return>
    private List<Action> getAllActions(State state)
    {
         List<Action> result = new ArrayList<>();
         
         // Iterate through each tile in the state. Any empty tile is a potential move.
         int elemNum = 0;
         for (char element : state.stringState.toCharArray())
             { if (element == '_')
                   result.add(new Action(elemNum));
               elemNum++; }
          
          // Return the list of actions.
          return result;
    }

    // #region Minimax Methods
    /// <summary>
    /// A helper method for the minimax method that is recursively called. Flip-flops between max & min appropriately to capture both the AI & opponent player's perspective.
    /// </summary>
    /// <return> Returns the best utility value for the initial potential action. </return>
    private int minimaxValue(State state, boolean minToggle, int alpha, int beta, int depth)
    {
        // Base case: After searching 7 moves ahead or reaching a terminal state, return the state's utility value.
        if (depth > 7 || isTerminalState(state))
            return utility(state);  // If cut off by depth, utility provides an estimated desirability score.
    
        // Determine all possible actions from this state.
        List<Action> possibleActions = getAllActions(state);
        
        // If in the minimizing stage, attempt to find the lowest possible value.
        if (minToggle)
          { int minValue = Integer.MAX_VALUE;   // Start with the highest possible value to ensure minimization.
            
            // Iterate over each possible action.
            for (Action action : possibleActions)
              { // Recursively evaluate the resulting state, switching to the maximizing stage.
                minValue = Math.min(minValue, minimaxValue(getSuccessorState(state, action), false, alpha, beta, depth + 1));
                
                // Alpha-beta pruning: If the value is less than or equal to alpha, prune this branch.
                if (minValue <= alpha)
                    return minValue;
                
                // Update beta to track the best minimum value found so far.
                beta = Math.min(beta, minValue); }
            
            return minValue; 
          }
        
        // Otherwise, in the maximizing stage, attempt to find the highest possible value.
        else
          { int maxValue = Integer.MIN_VALUE;   // Start with the lowest possible value to ensure maximization.
            
            // Iterate over each possible action.
            for (Action action : possibleActions)
              { // Recursively evaluate the resulting state, switching to the minimizing stage.
                maxValue = Math.max(maxValue, minimaxValue(getSuccessorState(state, action), true, alpha, beta, depth + 1));
                
                // Alpha-beta pruning: If the value is greater than or equal to beta, prune this branch.
                if (maxValue >= beta)
                    return maxValue;
                
                // Update alpha to track the best maximum value found so far.
                alpha = Math.max(alpha, maxValue); }
            
            return maxValue; 
          }
    }

    /// <summary>
    /// The primary minimax function. Given the initial state, determine the optimal action to take.
    /// </summary>
    /// <return> Returns the optimal action to take (as opposed to a utility value the helper function returns). </return>
    private Action minimax(State state)
    {
        // Determine all possible actions from this state.
        List<Action> possibleActions = getAllActions(state);

        // Create an action that will store the to-be-chosen action.
        Action choiceAction = new Action();
        
        // Iterate through each possible action:
        int maximumDesirability = Integer.MIN_VALUE;
        for (Action action : possibleActions)
          { // Recursively evaluate the resulting state of the current state and action.
            action.desirability = minimaxValue(getSuccessorState(state, action), true, Integer.MIN_VALUE, Integer.MAX_VALUE, 1);
            
            // If the value is larger than any seen thus far, set it to the maximum and its corresponding action to the choice action.
            if (action.desirability > maximumDesirability)
              { maximumDesirability = action.desirability;
                choiceAction = action; } }

        return choiceAction;
    }

    // The main method called by the referencing program.
    public int move(String stringState) { return minimax(new State(stringState)).actionNo; }

    //#region Helper Classes

    public static class State
    {
        // Attributes
        public String stringState;
        public int depth;

        // Constructors
        public State() {} // Default Constructor
        public State(String stringState){ this(stringState, 0); }   // Default depth of 0.
        public State(String stringState, int depth)
        {
            this.stringState = stringState;
            this.depth = depth;
        }

        // Methods
        public void printState(int numCols)
        {
            for (int i = 0; i < stringState.length(); i++)
              { // Print the current element.
                System.out.print(stringState.charAt(i) + " ");
                
                // Newline when at the end of the row.
                if ((i+1) % numCols == 0)
                    System.out.println(); }
        }
    }

    public static class Action
    {
        // Single attribute.
        public int actionNo;
        public int desirability;

        // Constructors
        public Action() { this(-1); } // Default constructor w/ indeterminate action.
        public Action(int actionNo)
        {
            this.actionNo = actionNo;
        }

    }

    public static class Coordinate
    {
        // Attributes
        public int x;
        public int y;

        // Constructors
        public Coordinate() {}  // Default Constructor
        public Coordinate(int x, int y)
        {
            this.x = x;
            this.y = y;
        }
        
        /// <summary>
        /// Assuming (0, 0) is the top left of the grid,
        /// return the flattened grid-tile the coordinate represents.
        /// (0, 0), (1, 0), (2, 0)         0 1 2
        /// (0, 1), (1, 1), (2, 1)    =>   3 4 5
        /// (0, 2), (1, 2), (2, 2)         6 7 8
        /// </summary>
        /// <return> Returns the integer grid-tile the coordinate represents </return>
        public static int flattenCoordinates(int x, int y, int numCols) { return y * numCols + x; }
    }
}
