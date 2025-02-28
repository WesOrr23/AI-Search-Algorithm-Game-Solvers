# Sudoku Solver AI

This is a Sudoku puzzle solver featuring an AI that uses a backtracking algorithm to solve the puzzle. The user provides an initial Sudoku grid, and the AI solves it.

## Features
- User can input an initial Sudoku puzzle (with some cells pre-filled)
- AI solves the puzzle using a backtracking algorithm assisted by the domain-reduction AC3 algorithm.
- Provides a solution to the Sudoku puzzle, if solvable

## Backtracking Algorithm Implementation
The AI decision-making logic is implemented in the `SudokuSolver.java` file. It uses a backtracking approach to systematically solve the puzzle by filling the grid while ensuring that all Sudoku constraints are met.

## How it Works
1. The user enters an initial Sudoku puzzle with some pre-filled numbers (empty cells can be left for the AI to fill).
2. The AI solves the puzzle by exploring potential number placements recursively, backtracking whenever it encounters a conflict.
3. Once a solution is found, the solved Sudoku grid is displayed.

## Credits
- **UI and game framework**: Provided by my professor, Dr. Arisoa Randrianasolo
- **Backtracking AI implementation**: Developed by myself, Wes Orr

## License
This project is for educational purposes. Feel free to use and modify it, but please provide credit where applicable.
