package Sandbox;

/*
 * Copyright 2010 Douglas B. Caulkins
 * GNU General Public License verbiage?
 */
import java.awt.GridLayout;

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
 * @author Douglas B. Caulkins
 */
public class TicTacToe {
	static private final int ROWCOUNT = 3;
	static private final int COLCOUNT = 3;
	
	/**
	 * Empty constructor, for now
	 */
	private TicTacToe() {
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
		/* Populate the panel with nine buttons, just to see how everything looks */
		for (int i = 0; i < ROWCOUNT; i++)
			for (int j = 0; j < COLCOUNT; j++) {
				pnlTicTacToe.add(new JButton("X"));
			}
		return pnlTicTacToe;
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