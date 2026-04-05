import random
import copy

class TicTacToePlayer:
    # object representation for an AI Tic Tac Toe player.
    board = [[' ' for _ in range(3)] for _ in range(3)]
    pieces = ['X', 'O']

    def __init__(self):
        # Initializes an AI Tic Tac Toe player object
        self.my_piece = random.choice(self.pieces)
        self.opp = self.pieces[0] if self.my_piece == self.pieces[1] else self.pieces[1]

    # select a (row,col) space for the next move
    def make_move(self, state):
        best_value = float("-inf")
        best_state = None

        # get the best state of the board
        for successor in self.succ(state, self.my_piece):
            current_value = self.min_value(float("-inf"), float("inf"), successor, 1)
            if current_value > best_value:
                best_value = current_value
                best_state = successor

        move = []

        # Guard against None (can happen if board is already full)
        if best_state is None:
            return move

        # get the best move
        for i in range(3):
            for j in range(3):
                # if the space is empty and does not already exist in the board state
                if state[i][j] == ' ' and state[i][j] != best_state[i][j]:
                    # add it to the best move
                    (row, col) = (i, j)
                    move.insert(0, (row, col))
        return move

    def succ(self, state, piece):
        # Returns a list of all possible successor states from board state
        successors = []

        for row in range(3):
            for col in range(3):
                state_copy = copy.deepcopy(state)  # copy of the board state so that it does not change the original during searching
                if state_copy[row][col] == ' ':     # if the space is empty
                    state_copy[row][col] = piece    # add it to the board
                    successors.append(state_copy)   # add copied board state as a successor

        return successors

# minimax algorithm
    def max_value(self, a, b, state, depth):
        # return the maximum value of the state (+1)
        # check whether the state is a terminal state
        if self.game_value(state) != 0:
            return self.game_value(state)

        successors = self.succ(state, self.my_piece)
        if not successors:  # board is full, it's a draw
            return 0

        for successor in successors:
            a = max(a, self.min_value(a, b, successor, depth + 1))  # get the maximum alpha
            # alpha pruning
            if a >= b:
                return b

        return a

    def min_value(self, a, b, state, depth):
        # return the minimum value of the state (-1)
        # check whether the state is a terminal state
        if self.game_value(state) != 0:
            return self.game_value(state)

        successors = self.succ(state, self.opp)
        if not successors:  # board is full, it's a draw
            return 0

        for successor in successors:
            b = min(b, self.max_value(a, b, successor, depth + 1))  # get the minimum beta
            # beta pruning
            if a >= b:
                return a

        return b

# Game-related methods
    def opponent_move(self, move):
        if self.board[move[0][0]][move[0][1]] != ' ':  # validate the player's move to make sure the space is empty and ready to be filled
            raise Exception("Illegal move detected")
        self.place_piece(move, self.opp)

    def place_piece(self, move, piece):  # place the piece at the location on the board
        self.board[move[0][0]][move[0][1]] = piece

    def print_board(self):
        for row in range(len(self.board)):
            line = str(row + 1) + " "  # row
            for cell in self.board[row]:
                line += cell + " "     # spaces to be filled
            print(line)
        print("  1 2 3 \n")            # column

    # check the current state of the board whether there is a winning condition
    def game_value(self, state):
        # 1 if AI wins
        # -1 if player wins

        # Check rows '-'
        for row in range(3):
            if state[row][0] != ' ' and state[row][0] == state[row][1] == state[row][2]:
                return 1 if state[row][0] == self.my_piece else -1

        # Check columns '|'
        for col in range(3):
            if state[0][col] != ' ' and state[0][col] == state[1][col] == state[2][col]:
                return 1 if state[0][col] == self.my_piece else -1

        # Check diagonal '\'
        if state[0][0] != ' ' and state[0][0] == state[1][1] == state[2][2]:
            return 1 if state[0][0] == self.my_piece else -1

        # Check diagonal '/'
        if state[0][2] != ' ' and state[0][2] == state[1][1] == state[2][0]:
            return 1 if state[0][2] == self.my_piece else -1

        return 0  # no winner

# Sample gameplay loop for testing purposes
def main():
    print("Welcome to Tic Tac Toe Game!")
    ai = TicTacToePlayer()
    turn = 0
    total_pieces = 0  # keep track of the number of pieces on the board

    # while there is no winning condition and the board is not full
    while ai.game_value(ai.board) == 0 and total_pieces < 9:
        # if it is the ai's turn
        if ai.my_piece == ai.pieces[turn]:
            ai.print_board()                        # display the current state of the board
            move = ai.make_move(ai.board)           # call the make_move method to evaluate the best move
            ai.place_piece(move, ai.my_piece)       # call the place piece method to make the move
            print(f"{ai.my_piece} moved at {move[0]}")  # display the move taken
            total_pieces += 1                       # increment the total number of pieces
        # if it is the player's turn
        else:
            move_made = False                       # if the player hasn't made their move
            ai.print_board()                        # display the current state of the board
            print(f"{ai.opp}'s turn")
            while not move_made:
                player_move = input("Move (e.g. row,column): ")  # ask player for their move
                try:
                    row, col = map(int, player_move.split(','))
                except ValueError:
                    print("Invalid format. Use row,column (e.g. 2,3)")
                    continue
                if str(row) not in "123" or str(col) not in "123":  # validate input
                    print("Row and column must be between 1 and 3.")
                    continue
                try:
                    ai.opponent_move([(row - 1, col - 1)])  # make the move
                    move_made = True                         # set move_made to true to stop looping
                except Exception as e:
                    print(e)
            total_pieces += 1  # increment the total number of pieces
        turn += 1
        turn %= 2

    ai.print_board()
    if ai.game_value(ai.board) == 1:
        print("AI wins! Game over.")
    elif ai.game_value(ai.board) == -1:
        print("You win! Game over.")
    else:
        print("It's a draw! Game over.")

if __name__ == "__main__":
    main()