package Sandbox;

/*
 * Copyright 2010 Douglas B. Caulkins
 * GNU General Public License verbiage?
 */
import javax.swing.JFrame;
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
	
	/**
	 * Display the introductory dialog, then the dark plain with the menu.
	 */
	static void createAndShowGUI() {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("Tic-Tac-Toe");
//		frame.add(null);
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