package Sandbox;

/*
 * Copyright 2010 Douglas B. Caulkins
 * GNU General Public License verbiage?
 */
import java.awt.BorderLayout;
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
	private final Cell[][] arrCell;
	// !!! Suggests simplification here...
	private final CellButton[][] arrButton;
	
	/* This current state of the game.  */
	private GameState state = GameState.START;
	
	/* The current player */
	private Player currentPlayer;

	/* The buttons and text line used to converse with the user */
	private final JButton btnYes;
	private final JButton btnNo;
	private final JButton btnOkay;
	private final JTextField txtLine;
	
	private int moveCount;
	
	/**
	 * Constructor
	 */
	private TicTacToe() {
		/* Initialize the array of cells */
		arrCell = new Cell[ROWCOUNT][COLCOUNT];
		for (int i = 0; i < ROWCOUNT; i++)
			for (int j = 0; j < COLCOUNT; j++) {
				arrCell[i][j] = new Cell();
			}
		arrButton = new CellButton[ROWCOUNT][COLCOUNT];
		
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
	 * @return the patter panel
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
		CellButton btnCell;
		for (int i = 0; i < ROWCOUNT; i++)
			for (int j = 0; j < COLCOUNT; j++) {
				btnCell = new CellButton(arrCell[i][j]);
				pnlTicTacToe.add(btnCell);
				arrButton[i][j] = btnCell;
			}
		return pnlTicTacToe;
	}

	private void enableCellButtons() {
		for (int i = 0; i < ROWCOUNT; i++)
			for (int j = 0; j < COLCOUNT; j++) {
				arrButton[i][j].setEnabled(true);
				arrButton[i][j].setText("");
				arrCell[i][j].setState(CellState.NULL);
			}
	}

	private class YesActionListener implements ActionListener {
		/**
		 * Perform this action if the user clicked the Yes button
		 */
		@Override
		public void actionPerformed(ActionEvent arg0) {
			switch (state) {
			/* Set up to ask the user to start first or not */
			case START:
				txtLine.setText(PLAYFIRST);
				state = GameState.FIRSTCHOICE;
				break;
			/* The user has the first move */
			case FIRSTCHOICE:
			case AGAIN:
				txtLine.setText(YOUAREX);
				state = GameState.INPROGRESS;
				btnYes.setVisible(false);
				btnNo.setVisible(false);
				btnOkay.setVisible(true);
				btnOkay.setEnabled(false);
				/* No moves have been made yet */
				moveCount = 0;
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
			switch (state) {
			/* The user doesn't want to play any more */
			case START:
			case AGAIN:
				/* I should replace this with a more graceful exit */
				System.exit(0);
				break;
			/* The computer has the first move */
			case FIRSTCHOICE: 
				txtLine.setText(YOUAREO);
				state = GameState.INPROGRESS;
				btnYes.setVisible(false);
				btnNo.setVisible(false);
				btnOkay.setVisible(true);
				btnOkay.setEnabled(false);
				/* No moves have been made yet */
				moveCount = 0;
				enableCellButtons();
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
			switch (state) {
			case DONE: 
				txtLine.setText(PLAYAGAIN);
				btnYes.setVisible(true);
				btnNo.setVisible(true);
				btnOkay.setVisible(false);
				state = GameState.AGAIN;
				break;
			}
		}
	}

	/* This class contains the state of a particular cell */
	private class Cell {
		private CellState state;

		private Cell() {	
			state = CellState.NULL;
		}
		
		private void setState(CellState newState) {
			state = newState;
		}
		
		private CellState getState() {
			return state;
		}
	}

	private class CellButton extends JButton
	{
		/* The cell associated with this particular button */
		private Cell cell;
		
		/**
		 * The constructor
		 */
		private CellButton(Cell pcell) {
			super();
			
			cell = pcell;
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
						cell.setState(CellState.X);
						CellButton.this.setText(CellState.X.name());
						TicTacToe.this.currentPlayer = Player.COMPUTER;
					} else {
						cell.setState(CellState.O);
						CellButton.this.setText(CellState.O.name());
						TicTacToe.this.currentPlayer = Player.USER;
					}
					/* Disable the button so it can't be clicked on again */
					CellButton.this.setEnabled(false);
					
					/* Count the number of moves */
					moveCount++;
					if (moveCount >= 9) {
						/* All the cells are filled - game over */
						state = GameState.DONE;
						txtLine.setText(DRAW);
						btnOkay.setEnabled(true);
					}
				}});
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