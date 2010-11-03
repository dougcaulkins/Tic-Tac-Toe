package Sandbox;

/*
 * Copyright 2010 Douglas B. Caulkins
 * GNU General Public License verbiage?
 */
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 * This application plays a game of tic-tac-toe with the user and never looses.
 * 
 * Given the uncertainly of my schedule at Turner and the possibility of some very long, late days, 
 * I have only a limited amount of time to complete this test. I'll use Java and Swing since they are the
 * tools with which I'm most comfortable. If I have time, I may attempt to port the code to Python.
 * 
 * I'll use the model-view-controller design pattern. The model is the state of the nine tic-tac-toe
 * cells. The view is how I render those cells. The controller will respond to user input, determine
 * when a game is done, etc.
 * 
 * I'll work on the view first, an 3x3 array of buttons. This is not pretty, but it is functional. If
 * I have time later I may decide to improve the rendering.
 * 
 * Improvements
 * - Center the frame on the screen
 * - Friendly dialog - "Would you care to play a game of tic-tac-toe" "Do you want the first move" "You win" 
 *    "I win" "Would you care to play again?"
 * - A help explaining the rules of the game?
 * - Render the background of a winning row or diagonal in a different color
 * - X goes first
 * - Short circuit dialog if the user clicks on a cell?
 * - Unit tests
 * - It would be amusing and doable to pit the computer against itself as a unit test
 * 
 * @author Douglas B. Caulkins
 */
public class TicTacToe {
	static private final int ROWCOUNT = 3;
	static private final int COLCOUNT = 3;
	static private final Dimension DIMBUTTON = new Dimension(75, 75);
	static private final int MAXMOVES = 9;
	
	static private final String PLAYAGAME = "Would you care to play a game of tic-tac-toe?";
	static private final String PLAYFIRST = "Do you want the first move?";
	static private final String YOUAREX = "You are \"X\"";
	static private final String YOUAREO = "You are \"O\"";
	static private final String IWIN = "I win!";
	static private final String DRAW = "It's a draw.";
	static private final String PLAYAGAIN = "Would you care to play again?";
	
	/* I use a simple state machine to model the game state relationships. */
	private enum GameState {START, FIRSTCHOICE, INPROGRESS, DONE, AGAIN};
	
	/* I'll model an unselected cell as having a NULL state */
	private enum CellState {NULL, X, O};

	/* The two players: the user or this, the computer */
	private enum Player {USER, COMPUTER};
	
	/* This models the cells and their relationship to each other */
	private final CellButton[][] arrCell;
	
	private final Triplet[] arrTriplet;
	
	/* This current state of the game.  */
	private GameState stateGame = GameState.START;
	
	/* The current player */
	private Player currentPlayer;
	private CellState computerMarker;
	private CellState userMarker;

	/* The buttons and text line used to converse with the user */
	private final JButton btnYes;
	private final JButton btnNo;
	private final JButton btnOkay;
	private final JTextField txtLine;
	
	private int moveCount;
	
	private boolean xwon = false;
	private boolean owon = false;
	
	private final Color origBackground;
	
	/**
	 * Constructor
	 */
	private TicTacToe() {
		/* Initialize the array of cells */
		arrCell = new CellButton[ROWCOUNT][COLCOUNT];
		for (int i = 0; i < ROWCOUNT; i++)
			for (int j = 0; j < COLCOUNT; j++) {
				arrCell[i][j] = new CellButton();
			}
		origBackground = arrCell[0][0].getBackground();
		arrTriplet = new Triplet[8];
		/* Rows */
		arrTriplet[0] = new Triplet(arrCell[0][0], arrCell[0][1], arrCell[0][2]);
		arrTriplet[1] = new Triplet(arrCell[1][0], arrCell[1][1], arrCell[1][2]);
		arrTriplet[2] = new Triplet(arrCell[2][0], arrCell[2][1], arrCell[2][2]);
		/* Columns */
		arrTriplet[3] = new Triplet(arrCell[0][0], arrCell[1][0], arrCell[2][0]);
		arrTriplet[4] = new Triplet(arrCell[0][1], arrCell[1][1], arrCell[2][1]);
		arrTriplet[5] = new Triplet(arrCell[0][2], arrCell[1][2], arrCell[2][2]);
		/* Diagonals */
		arrTriplet[6] = new Triplet(arrCell[0][0], arrCell[1][1], arrCell[2][2]);
		arrTriplet[7] = new Triplet(arrCell[0][2], arrCell[1][1], arrCell[2][0]);
		
		/* Initialize the components used to converse with the user */
		btnYes = new JButton("Yes");
		btnNo = new JButton("No");
		btnOkay = new JButton("Okay");
		txtLine = new JTextField(PLAYAGAME);
		btnYes.addActionListener(new YesActionListener());
		btnNo.addActionListener(new NoActionListener());
		btnOkay.addActionListener(new OkayActionListener());
		
		moveCount = 0;
	}
	
	/**
	 * This panel displays the conversation with the user 
	 * @return the conversation panel
	 */
	private JPanel getConversationPanel() {
		final JPanel pnlConversation = new JPanel();
		final JPanel pnlButton = new JPanel();
		txtLine.setEditable(false);
		pnlButton.add(btnYes);
		pnlButton.add(btnNo);
		pnlButton.add(btnOkay);
		btnOkay.setVisible(false);
		pnlConversation.setLayout(new BorderLayout());
		pnlConversation.add(txtLine, BorderLayout.CENTER);
		pnlConversation.add(pnlButton, BorderLayout.SOUTH);
		
		return pnlConversation;
	}
	
	/**
	 * Create a panel containing the tic-tac-toe cells
	 * @return the panel that renders the game
	 */
	private JPanel getTicTacToePanel() {
		final JPanel pnlTicTacToe = new JPanel();
		/* I'll use a grid layout because it is so easy */
		final GridLayout cellgrid = new GridLayout(ROWCOUNT, COLCOUNT);
		pnlTicTacToe.setLayout(cellgrid);
		/* Populate the panel with nine buttons */
		for (int i = 0; i < ROWCOUNT; i++)
			for (int j = 0; j < COLCOUNT; j++) {
				pnlTicTacToe.add(arrCell[i][j]);
			}
		return pnlTicTacToe;
	}
	
	/**
	 * The computer calculates what cell to mark
	 */
	private void takeTurn() {
		CellButton btnCell = null;
		/* If this is the first move, just always take the upper left hand corner */
		if (moveCount == 0) {
			btnCell = arrCell[0][0];
		}
		/* Else look for any winning move and make that move */
		if (btnCell == null) {
			for (Triplet triple : arrTriplet) {
				btnCell = triple.getEmptyCell(computerMarker);
				if (btnCell != null) {
					break;
				}
			}
		}
		/* Else look for any blocking move and make that move */
		if (btnCell == null) {
			for (Triplet triple : arrTriplet) {
				btnCell = triple.getEmptyCell(userMarker);
				if (btnCell != null) {
					break;
				}
			}
		}
		/* Else (for now) just go to a random cell */
		if (btnCell == null) {
			out:
			for (int i = 0; i < ROWCOUNT; i++)
				for (int j = 0; j < COLCOUNT; j++) {
					if (arrCell[i][j].getState() == CellState.NULL) {
						btnCell = arrCell[i][j];
						break out;
					}
				}
		}		
		
		if (btnCell != null) {
			btnCell.doClick();
		}
	}
	

	/* Enable and clear the cell buttons */
	private void enableCellButtons() {
		for (int i = 0; i < ROWCOUNT; i++)
			for (int j = 0; j < COLCOUNT; j++) {
				arrCell[i][j].clear();
			}
	}

	/* Disable the cell buttons */
	private void disableCellButtons() {
		for (int i = 0; i < ROWCOUNT; i++)
			for (int j = 0; j < COLCOUNT; j++) {
				arrCell[i][j].setEnabled(false);
			}
	}
	
	/*
	 * This class is a view of three cells that, if all the same, indicate a win
	 */
	private class Triplet {
		private CellButton[] arrCell;
		
		private Triplet(CellButton cell1, CellButton cell2, CellButton cell3) {
			arrCell = new CellButton[ROWCOUNT];
			arrCell[0] = cell1;
			arrCell[1] = cell2;
			arrCell[2] = cell3;
		}
		
		/**
		 * Determine if O has won
		 * @return true if O has won
		 */
		private boolean isOWin() {
			return isWin(CellState.O);
		}
		
		/**
		 * Determine if X has won
		 * @return true if X has won
		 */
		private boolean isXWin() {
			return isWin(CellState.X);
		}
		
		/**
		 * Determine if the player of this type has won
		 * @param stateCell the type
		 * @return true if the player has won
		 */
		private boolean isWin(CellState stateCell) {
			int count = 0;
			boolean win = false;
			for (CellButton cell: arrCell) {
				if (cell.getState() == stateCell) {
					count++;
				}
			}
			win = (count == ROWCOUNT);
			if (win) {
				for (CellButton cell: arrCell) {
					cell.setBackground(Color.GREEN);
				}				
			}
			
			return win;
		}
		
		private CellButton getEmptyCell(CellState stateCell) {
			int count = 0;
			CellButton nullCell = null;
			for (CellButton cell: arrCell) {
				if (cell.getState() == stateCell) {
					count++;
				}
				if (cell.getState() == CellState.NULL) {
					nullCell = cell;
				}
			}
			if (count != 2) {
				nullCell = null;
			}
			return nullCell;
		}
	}

	private class YesActionListener implements ActionListener {
		/**
		 * Perform this action if the user clicked the Yes button
		 */
		@Override
		public void actionPerformed(ActionEvent arg0) {
			switch (stateGame) {
			/* Set up to ask the user to start first or not */
			case START:
			case AGAIN:
				txtLine.setText(PLAYFIRST);
				stateGame = GameState.FIRSTCHOICE;
				xwon = false;
				owon = false;
				break;
			/* The user has the first move */
			case FIRSTCHOICE:
				txtLine.setText(YOUAREX);
				stateGame = GameState.INPROGRESS;
				btnYes.setVisible(false);
				btnNo.setVisible(false);
				btnOkay.setVisible(true);
				btnOkay.setEnabled(false);
				/* No moves have been made yet */
				moveCount = 0;
				userMarker = CellState.X;
				computerMarker = CellState.O;
				currentPlayer = Player.USER;
				enableCellButtons();
				break;
			}
		}
	}

	private class NoActionListener implements ActionListener {
		/**
		 * Perform this action if the user clicked the No button
		 */
		@Override
		public void actionPerformed(ActionEvent arg0) {
			switch (stateGame) {
			/* The user doesn't want to play any more */
			case START:
			case AGAIN:
				/* I should replace this with a more graceful exit */
				System.exit(0);
				break;
			/* The computer has the first move */
			case FIRSTCHOICE: 
				txtLine.setText(YOUAREO);
				stateGame = GameState.INPROGRESS;
				btnYes.setVisible(false);
				btnNo.setVisible(false);
				btnOkay.setVisible(true);
				btnOkay.setEnabled(false);
				/* No moves have been made yet */
				moveCount = 0;
				userMarker = CellState.O;
				computerMarker = CellState.X;
				enableCellButtons();
				currentPlayer = Player.COMPUTER;
				takeTurn();
				break;
			}
		}
	}

	private class OkayActionListener implements ActionListener {
		/**
		 * Perform this actions if the user clicked the Okay button
		 */
		@Override
		public void actionPerformed(ActionEvent arg0) {
			switch (stateGame) {
			case DONE: 
				txtLine.setText(PLAYAGAIN);
				btnYes.setVisible(true);
				btnNo.setVisible(true);
				btnOkay.setVisible(false);
				stateGame = GameState.AGAIN;
				break;
			}
		}
	}

	private class CellButton extends JButton
	{
		private CellState stateCell;
		
		/**
		 * The constructor
		 */
		private CellButton() {
			super();
			
			stateCell = CellState.NULL;
			/* Make the button a nice square */
			setPreferredSize(DIMBUTTON);
			setFont(new Font("sansserif", Font.BOLD, 50));
			setEnabled(false);
			/* Listen for a button press */
			addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					/* !!! revisit when the player to move first is a choice */
					if (TicTacToe.this.currentPlayer == Player.USER) {
						stateCell = userMarker;
						CellButton.this.setText(userMarker.name());
					} else {
						stateCell = computerMarker;
						CellButton.this.setText(computerMarker.name());
					}
					/* Disable the button so it can't be clicked on again */
					CellButton.this.setEnabled(false);
					
					for (Triplet triple : arrTriplet) {
						if (triple.isXWin()) {
							xwon = true;
							stateGame = GameState.DONE;
							txtLine.setText(IWIN);
							btnOkay.setEnabled(true);
							disableCellButtons();
						} else if (triple.isOWin()) {
							owon = true;
							stateGame = GameState.DONE;
							txtLine.setText(IWIN);
							btnOkay.setEnabled(true);
							disableCellButtons();
						}
					}

					/* Count the number of moves */
					moveCount++;
					if (moveCount >= MAXMOVES) {
						/* All the cells are filled - game over */
						stateGame = GameState.DONE;
						txtLine.setText(DRAW);
						btnOkay.setEnabled(true);
					}
					if (TicTacToe.this.currentPlayer == Player.USER) {
						TicTacToe.this.currentPlayer = Player.COMPUTER;
//						SwingUtilities.invokeLater(new Runnable() 
//						{
//							@Override
//							public void run() {
								takeTurn();								
//							}							
//						});
					} else {
						TicTacToe.this.currentPlayer = Player.USER;
					}
				}});
		}		
		
		private void setState(CellState newState) {
			stateCell = newState;
		}
		
		private CellState getState() {
			return stateCell;
		}
		
		private void clear() {
			setEnabled(true);
			setText("");
			setState(CellState.NULL);
			setBackground(origBackground);

		}
	}

	/**
	 * Display the tic-tac-toe playing board
	 */
	static void createAndShowGUI() {
		final JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("Tic-Tac-Toe");
		final TicTacToe game = new TicTacToe();
		final JPanel pnlRenderedGame = game.getTicTacToePanel();
		frame.add(pnlRenderedGame);
		final JPanel pnlConversation = game.getConversationPanel();
		frame.add(pnlConversation, BorderLayout.SOUTH);
		frame.pack();
		frame.setVisible(true);
	}
	
	/**
	 * Application entry point
	 * 
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() 
		{
			@Override
			public void run() 
			{        
				createAndShowGUI();
			}
		});
	}
}