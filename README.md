# Game AI Repository

This repository contains three different AI-driven game projects: **Tic-Tac-Toe**, **Connect4**, and **Sudoku Solver**. Each project demonstrates an AI-based solution to a classic game, utilizing different algorithms for decision-making and solving.

## Projects

### 1. **Tic-Tac-Toe AI**
- **Description**: A fully functional Tic-Tac-Toe game featuring an AI opponent that uses the **Minimax algorithm** for optimal play.
- **Key Features**:
  - AI opponent with optimal play using Minimax
  - Human vs. AI gameplay
  - Graphical interface for easy play
- **Algorithm**: The AI uses a Minimax algorithm to evaluate all possible game states and make the best decision to maximize its chances of winning.

### 2. **Connect4 AI**
- **Description**: A Connect4 game with an AI opponent implemented using the **Minimax algorithm**, similar to the Tic-Tac-Toe AI.
- **Key Features**:
  - AI opponent with optimal play using Minimax
  - Human vs. AI gameplay
  - Graphical interface for easy play
- **Algorithm**: The Minimax AI in Connect4 is nearly identical to the one in Tic-Tac-Toe, designed to make optimal moves based on future game states.

### 3. **Sudoku Solver AI**
- **Description**: A Sudoku puzzle solver where the AI uses a **backtracking algorithm** to solve the puzzle optimally.
- **Key Features**:
  - User inputs an initial Sudoku puzzle (with some pre-filled cells)
  - AI solves the puzzle using backtracking
  - Solution is displayed once the puzzle is solved
- **Algorithm**: The AI uses backtracking to explore potential number placements for the puzzle, backtracking whenever it encounters a conflict until the puzzle is solved.

## Shared Components

### Minimax AI (Tic-Tac-Toe and Connect4)
The **Minimax agents** for both Tic-Tac-Toe and Connect4 are nearly the same. Both games use the same core decision-making system, evaluating all potential moves and selecting the optimal one based on the Minimax algorithm. The implementation is generalized to work for both games, making it easy to adapt or extend the AI for other similar turn-based games.

## Credits
- **UI and game frameworks**: Provided by my professor, Dr. Arisoa Randrianasolo
- **Minimax AI implementations**: Developed by myself, Wes Orr
- **Backtracking AI implementation (Sudoku)**: Developed by myself, Wes Orr

## License
This project is for educational purposes. Feel free to use and modify it, but please provide credit where applicable.

## How to Run

### Tic-Tac-Toe AI & Connect4 AI
1. Clone this repository:
   ```bash
   git clone https://github.com/yourusername/game-ai-repo.git
