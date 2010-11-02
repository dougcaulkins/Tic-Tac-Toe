package Sandbox;

/*
 * Copyright 2010 Douglas B. Caulkins
 * GNU General Public License verbiage?
 */
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
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
 * 
 * @author Douglas B. Caulkins
 */
public class TicTacToe {
	static private final int ROWCOUNT = 3;
	static private final int COLCOUNT = 3;
	static private final Dimension DIMBUTTON = new Dimension(50, 50);
	
	/* I'll model an unselected cell as having a NULL state */
	private enum CellState {NULL, X, O};

	/* The two players: the user or this, the computer */
	private enum Player {USER, COMPUTER};
	
	/* This models the cells and their relationship to each other */
	private final Cell[][] arrCell;
	
	/* The current player */
	private Player currentPlayer;
	
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
			}
		return pnlTicTacToe;
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