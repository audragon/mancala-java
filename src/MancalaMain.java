import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.Timer;


public class MancalaMain extends JFrame {

	final Color CPU_BLUE = new Color(0, 0, 0);
	final Color PLAYER_BLUE = new Color(16, 16, 178);
	final Color GREEN = new Color (47, 142, 63);
	// How fast the animations play
	final int PLAYER_TURN_TIME = 750;
	final int CPU_TURN_TIME = 1250;

	static MancalaMain thisFrame;
	
	JLabel[] bins = new JLabel[14];
	JPanel[] binPanels = new JPanel[14];

	JLabel outputLabel = new JLabel("<html><span style=\"color:rgb(16, 16, 178);\">Your Turn</span></html>");
	JLabel scoreLabel = new JLabel("<html><span style=\"color:rgb(16,16,178);\">Player wins: " + 0 + ",</span> <span style=\"color:rgb(0, 0, 0);\">Computer wins: " + 0 +"</span></html>");
	int playerScore = 0;
	int computerScore = 0;
	
	int freeTurnCount = 1;

	JPanel clickedPanel;
	String turn = "player";
	
	Timer[] timers = new Timer[5];

	//test variable
	static boolean freeTurn;

	public static void main(String[] args) {
		freeTurn = false;
		thisFrame = new MancalaMain();
	}

	
	/**
	 * Constructor method for the game.
	 */
	public MancalaMain() {
		setTitle("Mancala");
		setSize(1000, 700);
		setExtendedState(JFrame.MAXIMIZED_BOTH); 
		setFocusable(true);

		//Center the frame on the screen
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);

		for(int i = 0; i < bins.length; i++) {
			if(i != 6 && i != 13) {
				bins[i] = new JLabel("4");
			} else {
				bins[i] = new JLabel("0");
			}
		}
				
		addComponentsToFrame();

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		thisFrame = this;
		
		
		boolean playerTurn = ((int)(Math.random() * 2) == 1) ? true: false;
		if (!playerTurn) {
			JOptionPane.showMessageDialog(this, "The computer will go first.");
			outputLabel.setText("<html><span style=\"color:rgb(0, 0, 0);\">Computer Turn</span></html>");
			turn = "computer";
			cpuTurn();
		} else {
			JOptionPane.showMessageDialog(this, "You will go first.");
			turn = "player";
		}

	}

	
	/**
	 * Helper method to build the game.
	 */
	public void addComponentsToFrame() {

		/* Main panel that holds everything */
		JPanel main = new JPanel(new BorderLayout());

		/* Set up the title */
		GridBagConstraints gbc = new GridBagConstraints();
		JPanel titlePanel = new JPanel(new GridBagLayout());
		titlePanel.setPreferredSize(new Dimension(1000, 100));
		JLabel titleLabel = new JLabel("Mancala");
		titleLabel.setFont(new Font("Arial", 0, 75));
		titlePanel.add(titleLabel, gbc);
		titlePanel.setBackground(Color.WHITE);
		main.add(titlePanel, BorderLayout.NORTH);

		/* Set up the mancala display */
		JPanel boardPanel = new JPanel(new BorderLayout());

		GridLayout binLayout = new GridLayout(2,6);
		binLayout.setHgap(0);
		binLayout.setVgap(0);

		// Set up the boxes that represent the mancala bins
		JPanel binPanel = new JPanel(binLayout);

		binPanels = new JPanel[14];
		int[] JPanelOrder = {12, 11, 10, 9, 8, 7, 0, 1, 2, 3, 4, 5};
		for(int i = 0; i < bins.length; i++) {
			if (i > 6)
				bins[i].setForeground(CPU_BLUE);
			else
				bins[i].setForeground(PLAYER_BLUE);
			bins[i].setFont(new Font("Arial", 0, 45));
			JPanel panel = new JPanel(new GridBagLayout());
			panel.add(bins[i], gbc);
			panel.setBackground(Color.WHITE);
			panel.setBorder(BorderFactory.createLineBorder(Color.black, 5));
			panel.setName(Integer.toString(i));
			if(i < 6) {
				panel.addMouseListener(new PlayerBinListener());
			}
			else {
				panel.addMouseListener(new TopLabelAndMancalaListener());
				if(i == 6 || i == 13) {
					panel.setPreferredSize(new Dimension(125, 400));
					if (i == 13)
						panel.setBorder(BorderFactory.createMatteBorder(10, 10, 10, 0, Color.BLACK));
					else
						panel.setBorder(BorderFactory.createMatteBorder(10, 0, 10, 10, Color.BLACK));
				}
			}
			binPanels[i] = panel;
		}
		
		for(int i = 0; i < JPanelOrder.length; i++) {
			binPanel.add(binPanels[JPanelOrder[i]]);
		}
		
		boardPanel.add(binPanel, BorderLayout.CENTER);
		boardPanel.add(binPanels[13], BorderLayout.WEST);
		boardPanel.add(binPanels[6], BorderLayout.EAST);


		binPanel.setBackground(Color.BLACK);
		binPanel.setBorder(BorderFactory.createLineBorder(Color.black, 5));

		boardPanel.setBorder(BorderFactory.createLineBorder(Color.black));
		main.add(boardPanel);

		/* Add bottom panel */
		JPanel mainBottom = new JPanel(new BorderLayout());
		mainBottom.setPreferredSize(new Dimension(1000, 150));
		// Add the buttons and output label
		JPanel buttonAndOutputPanel = new JPanel(new BorderLayout());
		outputLabel.setFont(new Font("Arial", 0, 30));
		JPanel outputPanel = new JPanel(new GridBagLayout());
		outputPanel.setBackground(Color.WHITE);
		outputPanel.add(outputLabel, gbc);
		buttonAndOutputPanel.add(outputPanel, BorderLayout.CENTER);
		JButton restartButton = new JButton("Restart");
		restartButton.setFont(new Font("Arial", 0, 20));
		restartButton.setPreferredSize(new Dimension(125, 75));
		restartButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				for (int i = 0; i < timers.length; i++) {
					if (timers[i] != null)
						timers[i].stop();
				}
				
				for(int i = 0; i < bins.length; i++) {
					if(i == 6 || i == 13) {
						bins[i].setText("0");
					}
					else {
						bins[i].setText("4");
					}
					
					if (i < 7)
						bins[i].setForeground(PLAYER_BLUE);
					else
						bins[i].setForeground(CPU_BLUE);
					
					bins[i].setBackground(Color.WHITE);
				}
				
				freeTurn = false;

				boolean playerTurn = ((int)(Math.random() * 2) == 1) ? true: false;
				if (!playerTurn) {
					JOptionPane.showMessageDialog(thisFrame, "The computer will go first.");
					outputLabel.setText("<html><span style=\"color:rgb(0, 0, 0);\">Computer Turn</span></html>");
					turn = "computer";
					cpuTurn();
				} else {
					JOptionPane.showMessageDialog(thisFrame, "You will go first.");
					outputLabel.setText("<html><span style=\"color:rgb(16, 16, 178);\">Your Turn</span></html>");
					turn = "player";
				}



			}

		});
		JButton quitButton = new JButton("Quit");
		quitButton.setPreferredSize(new Dimension(125, 75));
		quitButton.setFont(new Font("Arial", 0, 20));
		// Make quit button work
		quitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {

				String[] options = {"Yes", "No"};
				int n = JOptionPane.showOptionDialog(thisFrame,
						"Are you sure you want to quit?",
						"Comfirm Quit",
						JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE,
						null,     //do not use a custom Icon
						options,  //the titles of buttons
						options[0]);
				if (n == 0)
					thisFrame.dispose();
			}

		});
		
		JButton howButton = new JButton("Help");
		howButton.setPreferredSize(new Dimension(125, 75));
		howButton.setFont(new Font("Arial", 0, 20));
		// Make quit button work
		howButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JPanel helpPanel = new JPanel(new BorderLayout());
				JLabel titleLabel = new JLabel("How To Play");
				titleLabel.setFont(new Font("Arial", 0, 40));
				GridBagConstraints gbc = new GridBagConstraints();
				
				
				JPanel titlePanel = new JPanel(new GridBagLayout());
				titlePanel.add(titleLabel, gbc);
				helpPanel.add(titlePanel, BorderLayout.NORTH);
				
				String instructions = "Layout:\nThe Mancala board is made up of two rows of six bins.\nThere are 4 stones in each bin. There is also a special \nbin called a mancala on each side of the board. Your\nside of the board is the bottom of the screen, and your\nmancala is the one on your right."
						+ "\n\nGoal:\nThe goal of the game is to have the most stones in\nyour mancala at the end of the game."
						+ "\n\nRules:\nEach turn, you will select a bin and remove all stones\nfrom it. Then, moving counter-clockwise around the\nboard, you will place one stone in each bin until you\nrun out of stones. You will skip your opponent's\nmancala, as well as the bin the stones originally came\nout of, if you make it that far."
						+ " If the last stone you place\nlands in your mancala, you get an extra turn. If the\nlast stone you place lands in an empty bin on your side,\nyou take that stone and any stones in the bin across\nfrom it, and place them all into your mancala."
						+ "\n\nEnding the game: When there are no stones on either\nside of the board, the game ends. The player that still\nhas stones on their side takes them all and puts them\ninto their own mancala. The stones in each mancala\nare counted, and the player with more stones wins.";
				
				
				JTextArea instructionsArea = new JTextArea(instructions);
				instructionsArea.setFont(new Font("Arial", 0 ,20));
				JScrollPane instructionsPane = new JScrollPane(instructionsArea);
				instructionsPane.setPreferredSize(new Dimension(5, 10));
				
				helpPanel.add(instructionsPane, BorderLayout.CENTER);
				
				helpPanel.setPreferredSize(new Dimension(520, 500));
				JOptionPane.showMessageDialog(null, helpPanel, "Instructions", JOptionPane.PLAIN_MESSAGE);
				
			}

		});
		
		JPanel howPanel = new JPanel(new GridLayout(2,1));
		howPanel.add(howButton);
		howPanel.add(Box.createRigidArea(new Dimension(125, 75)));
		howPanel.setBackground(Color.WHITE);
		
		JPanel quitRestartPanel = new JPanel(new GridLayout(2,1));
		quitRestartPanel.setBackground(Color.WHITE);
		quitRestartPanel.add(restartButton);
		quitRestartPanel.add(quitButton);
		
		mainBottom.add(buttonAndOutputPanel, BorderLayout.NORTH);
		mainBottom.add(quitRestartPanel, BorderLayout.EAST);
		mainBottom.add(howPanel, BorderLayout.WEST);
//		buttonAndOutputPanel.add(restartButton, BorderLayout.WEST);
//		buttonAndOutputPanel.add(quitButton, BorderLayout.EAST);
//		mainBottom.add(buttonAndOutputPanel, BorderLayout.NORTH);

		// Add the score label
		scoreLabel.setFont(new Font("Arial", 0, 30));
		JPanel scorePanel = new JPanel(new GridBagLayout());
		scorePanel.add(scoreLabel, gbc);
		scorePanel.setBackground(Color.WHITE);
		mainBottom.add(scorePanel, BorderLayout.CENTER);

		main.add(mainBottom, BorderLayout.SOUTH);

		add(main);
	}

	/** 
	 * Accessor method for the number of stones in each bin.
	 * @param binNumber Which bin is being accessed.
	 * @return stones in that bin.
	 */
	public int getBinStones(int binNumber) {
		return Integer.parseInt(bins[binNumber].getText());
	}
	
	/**
	 * Sets the bin's text shown.
	 * @param binNumber bin that will be changed.
	 * @param text number that bin will be changed to.
	 */
	public void setBinText(int binNumber, int text) {
		bins[binNumber].setText(text + "");
	}

	/**
	 * Returns the bin based on its corresponding location in the bins array.
	 * @param num index of bin.
	 * @return corresponding JLabel bin.
	 */
	public JLabel getLabelFromNum(int num) {
		return bins[num];
	}

	/**
	 * Executes a move based on the player's bin choice.
	 * @param chosenPile bin that the player has chosen.
	 * @return whether or not a free turn has been earned.
	 */
	public boolean playerTurn(int chosenPile) {
		if (turn.equals("player")) {
		
		freeTurn = false;
		turn = "animation";
		ArrayList<Integer> binToChange = new ArrayList<Integer>();
		ArrayList<Integer> newValue = new ArrayList<Integer>();

		int moves = getBinStones(chosenPile); // how many stones will be dispersed.
		int boardIndex = chosenPile + 1; // where to start putting the chosen stones.
		binToChange.add(chosenPile);
		newValue.add(0);

		// keep going until all stones are dispersed.
		while(moves != 0) {
			if(boardIndex == 13) boardIndex = 0; //don't put stones in opponent's pile
			if(boardIndex == chosenPile) boardIndex++;
			moves--;

			if (!binToChange.contains(boardIndex)) {
				binToChange.add(boardIndex);
				newValue.add(getBinStones(boardIndex) + 1);
			} else {
				newValue.add(newValue.get(binToChange.lastIndexOf(boardIndex)) + 1);
				binToChange.add(boardIndex);
			}

			// check if stones are to be captured if last move.
			if(moves == 0) {
				// calculate opposite side and check if stones are in the pile.
				int oppositePile = 12 - boardIndex;
				boolean oppositeChanged = false;
				if (binToChange.contains(oppositePile)) {
					oppositeChanged = true;
				}

				if(getBinStones(boardIndex) == 0 && boardIndex < 6 && (getBinStones(oppositePile) != 0 || oppositeChanged)) {
					// add captured stones to player side.
					if (!binToChange.contains(6)) {
						binToChange.add(6);
						newValue.add(getBinStones(6) + getBinStones(oppositePile) + 1);
					} else {
						newValue.add(newValue.get(binToChange.lastIndexOf(6)) + newValue.get(binToChange.lastIndexOf(oppositePile)) + newValue.get(binToChange.lastIndexOf(boardIndex)));
						binToChange.add(6);
					}

					if (!binToChange.contains(boardIndex)) {
						binToChange.add(binToChange.size()-1, boardIndex);
						newValue.add(newValue.size()-1, 0);
					} else {
						newValue.add(newValue.size()-1, 0);
						binToChange.add(binToChange.size()-1, boardIndex);
					}

					if (!binToChange.contains(oppositePile)) {
						binToChange.add(binToChange.size()-1, oppositePile);
						newValue.add(newValue.size()-1, 0);
					} else {
						newValue.add(newValue.size()-1, 0);
						binToChange.add(binToChange.size()-1, oppositePile);
					}

				}

				// calculate if player earns free turn.
				if(boardIndex == 6) freeTurn = true;
			}
			boardIndex++;
		}


		timers[0] = new Timer(PLAYER_TURN_TIME, new ActionListener() {
			ArrayList<Integer> binToChange2 = binToChange;
			ArrayList<Integer> newValue2 = newValue;
			int i = 0;
			
			JLabel binLabel;
			JPanel binPanel;

			@Override
			public void actionPerformed(ActionEvent e) {
				turn = "animation";
				if (i == binToChange2.size() - 3 && newValue2.get(newValue2.size()-2) == 0) {
					outputLabel.setText("<html><span style=\"color:rgb(16, 16, 178);\">You got a capture!</span></html>");
				}
				resetColors();
				if (i < binToChange.size()) {
					
					binLabel = bins[binToChange2.get(i)];
					binPanel = binPanels[binToChange2.get(i)];

					if (newValue2.get(i) == 0) {
//						binLabel.setForeground(Color.RED);
						binPanel.setBackground(Color.RED);
					} else {
//						binLabel.setForeground(GREEN);
						binPanel.setBackground(GREEN);
					}


					binLabel.setText(newValue2.get(i) + "");

				}

				i++;
				if (i == binToChange.size()+1) {

					cleanupGame();
					if (!turn.equals("game end")) {

						if (freeTurn) {
							outputLabel.setForeground(PLAYER_BLUE);
							if (freeTurnCount == 1) {
								outputLabel.setText("You earned a free turn!");
							} else {
								outputLabel.setText("You earned a free turn! (#" + freeTurnCount + ")");
							}
							freeTurnCount++;
							turn = "player";
						} else { 
							outputLabel.setForeground(Color.BLACK);
							outputLabel.setText("<html><span style=\"color:rgb(0, 0, 0);\">Computer Turn</span></html>");
							turn = "computer";
							freeTurnCount = 1;
							cpuTurn();
						}

					}
					((Timer)e.getSource()).stop();
				}
			}

		});

		timers[0].start();
		
		if (!freeTurn)
			turn = "computer";
		else
			turn = "player";
		
		}
		
		return freeTurn;
	}

	
	
	/**
	 * Calculates the best move possible for the CPU.
	 * @param boardstate the current state of every bin at the beginning of the turn.
	 * @return A key value pair where is key is the best bin to move and the value is the amount of points that move is worth.
	 */
	private AbstractMap.SimpleEntry<Integer, Integer> calcBestMove (int[] boardstate) {
		int highestValue = 0;
		int highestValueBin = -1;
		boolean bestIsFreeTurn = false;
		
		for(int i = 7; i < 13; i++) {
			int currentValue = 0;
			// check if the move would result in a free turn
			if((boardstate[i] + i) == 13 || ((boardstate[i] + i) - 13) % 12 == 0) {
				int stonesLeft = boardstate[i];
				int currentBin = i;
				
				// create a new boardstate representing the board after the free turn move
				int[] newState = new int[14];
				System.arraycopy(boardstate, 0, newState, 0, boardstate.length);
				
				newState[i] = 0;
				while(stonesLeft > 0) {
					currentBin++;
					if(currentBin == i || currentBin == 6) currentBin++;
					
					if(currentBin > 13) {
						currentBin %= 14;
					}
					newState[currentBin]++;
					stonesLeft--;
				}
				// recursively call this method to determine the best move(s) after the free move and add its value(s)
				currentValue = 1 + calcBestMove(newState).getValue();
			}
			
			// if this results in a high value of stones added to the bin, remember this move
			if(currentValue > highestValue) {
				highestValue = currentValue;
				highestValueBin = i;
				bestIsFreeTurn  = true;
			}
		}
		
		// next, check the values of captures available.
		for(int i = 7; i < 13; i++) {
			int currentValue = 0;
			int landingBin = boardstate[i] + i;
			if(boardstate[i] != 0 && landingBin >= 7 && landingBin <= 12 && boardstate[landingBin] == 0 && boardstate[12 - landingBin] > 0) {
				currentValue = 1 + boardstate[12 - landingBin];
			}
			else if(boardstate[i] != 0 && landingBin >= 20 && landingBin <= 24 && landingBin - 13 < i) { // check if bin will lap the board and land back on same side
				landingBin -= 13;
				if(boardstate[landingBin] == 0) {
					currentValue = 2 + boardstate[12 - landingBin]; // an extra is added to this value to account for the extra stone put in bin during lap
				}
			}
			
			// if the capture will result in a high number of stones taken and is more valuable than previous moves, record it
			if (currentValue > highestValue) {
				highestValue = currentValue;
				highestValueBin = i;
				bestIsFreeTurn = false;
			}
		}
		
		
		// Check if defending from a capture saves many stones
		// NOTE: if a move is already found that puts the CPU at or over the winning number of stones (25),
		// then this check will be ignored. better to clench victory instead.
		// Also, will not check if a free turn is the current best move, because it is best to defend after a free turn.
		if(!bestIsFreeTurn && (highestValue == 0 || boardstate[13] + highestValue <= 25)) {
			AbstractMap.SimpleEntry<Integer, Integer> playerCapture = playerCapturePossible(boardstate);
			int captureBin = playerCapture.getKey();
			int captureAmount = playerCapture.getValue();
			// if defense potential is found and (if the defense potential has a higher value than current best move, or not defending would cause player to win)
			if(captureBin != -1 && captureAmount > 2 && (captureAmount > highestValue || captureAmount + 1 + boardstate[6] >= 25)) {
				boolean defenseFound = false;
				// check for defenses that put a stone in CPU mancala and also put a stone in the captureable bin.
				for(int i = 12; i >= 7; i--) {
					
					// create a new boardstate representing the board after the free turn move
					int stonesLeft = boardstate[i];
					int currentBin = i;
					
					int[] newState = new int[14];
					System.arraycopy(boardstate, 0, newState, 0, boardstate.length);
					
					newState[i] = 0;
					while(stonesLeft > 0) {
						currentBin++;
						if(currentBin == i || currentBin == 6) currentBin++;
						
						if(currentBin > 13) {
							currentBin %= 14;
						}
						newState[currentBin]++;
						stonesLeft--;
					}
					if(playerCapturePossible(newState).getKey() == -1) {
						highestValue = 1 + captureAmount;
						highestValueBin = i;
						defenseFound = true;
						break;
					}
				}
				
				// if can't defend by lapping the board, just move the pile that needs defending
				if(!defenseFound) {
					highestValue = captureAmount;
					highestValueBin = 12 - captureBin;
				}
			}
		}
		// if no free turns or captures or defenses available, move bin closest to mancala
		if(highestValueBin == -1) {
			for(int i = 12; i >= 7; i--) {
				if(boardstate[i] != 0) {
					highestValueBin = i;
					break;
				}
			}
		}
		
		AbstractMap.SimpleEntry<Integer, Integer> highest = new AbstractMap.SimpleEntry<Integer, Integer>(highestValueBin, highestValue);
		return highest;
	}
	
	/**
	 * Method that checks if the player could potentially capture stones next turn. Calculates their best move.
	 * @param boardstate the current state of every bin at the beginning of the turn.
	 * @return A key value pair where the key is the opponent's move that can capture CPU stones and the value is how many stones. -1 key indicates not captures available.
	 */
	private AbstractMap.SimpleEntry<Integer, Integer> playerCapturePossible (int[] boardstate) {
		
		int highestValue = 0;
		int highestValueBin = -1;
		// next, check the values of captures available.
		for(int i = 0; i < 6; i++) {
			int currentValue = 0;
			int landingBin = boardstate[i] + i;
			if(boardstate[i] != 0 && landingBin >= 0 && landingBin <= 5 && boardstate[landingBin] == 0) {
				currentValue = 1 + boardstate[12 - landingBin];
			}
			else if(boardstate[i] != 0 && landingBin >= 13 && landingBin <= 17 && landingBin - 13 < i) { // check if bin will lap the board and land back on same side
				landingBin -= 13;
				if(boardstate[landingBin] == 0) {
					currentValue = 2 + boardstate[12 - landingBin]; // an extra is added to this value to account for the extra stone put in bin during lap
				}
			}
			
			// if the capture will result in a high number of stones taken and is more valuable than previous moves, record it
			if (currentValue > highestValue) {
				highestValue = currentValue;
				highestValueBin = landingBin;
			}
		}
		
		return new AbstractMap.SimpleEntry<Integer, Integer>(highestValueBin, highestValue);
	}
	
	/**
	 * Method that determines and executes a move on the CPU's turn.
	 * @return whether the CPU has earned a free turn or not.
	 */
	public boolean cpuTurn() {
		if (turn.equals("computer")) {
		
		freeTurn = false;
		turn = "animation";
		
		// create an int array of the current boardstate.
		int[] boardstate = new int[14];
		for(int i = 0; i < 14; i++) {
			boardstate[i] = getBinStones(i);
		}
		
		// call the method to calculate the best move
		int chosenPile = calcBestMove(boardstate).getKey();
		
		ArrayList<Integer> binToChange = new ArrayList<Integer>();
		ArrayList<Integer> newValue = new ArrayList<Integer>();

		
		int moves = getBinStones(chosenPile); // how many stones will be dispersed.
		int boardIndex = chosenPile + 1; // where to start putting the chosen stones.

		binToChange.add(chosenPile);
		newValue.add(0);

		// keep going until all stones are dispersed.
		while(moves != 0) {
			if(boardIndex == 6 || boardIndex == chosenPile) {
				boardIndex++; //don't put stones in opponent's pile
			}
			moves--;

			if (!binToChange.contains(boardIndex)) {
				binToChange.add(boardIndex);
				newValue.add(getBinStones(boardIndex) + 1);
			} else {
				newValue.add(newValue.get(binToChange.lastIndexOf(boardIndex)) + 1);
				binToChange.add(boardIndex);
			}

			// check if stones are to be captured if last move.
			if(moves == 0) {

				// calculate opposite side and check if stones are in the pile.
				int oppositePile = 12 - boardIndex;

				boolean oppositeChanged = false;
				if (binToChange.contains(oppositePile)) {
					oppositeChanged = true;
				}

				if(getBinStones(boardIndex) == 0 && boardIndex > 6 && boardIndex < 13 && (getBinStones(oppositePile) != 0 || oppositeChanged)) {


					// add captured stones to player side.

					if (!binToChange.contains(13)) {
						binToChange.add(13);
						newValue.add(getBinStones(13) + getBinStones(oppositePile) + 1);
					} else {
						newValue.add(newValue.get(binToChange.lastIndexOf(13)) + newValue.get(binToChange.lastIndexOf(oppositePile)) + newValue.get(binToChange.lastIndexOf(boardIndex)));
						binToChange.add(13);
					}

					if (!binToChange.contains(boardIndex)) {
						binToChange.add(binToChange.size()-1, boardIndex);
						newValue.add(newValue.size()-1, 0);
					} else {
						newValue.add(newValue.size()-1, 0);
						binToChange.add(binToChange.size()-1, boardIndex);
					}

					if (!binToChange.contains(oppositePile)) {
						binToChange.add(binToChange.size()-1, oppositePile);
						newValue.add(newValue.size()-1, 0);
					} else {
						newValue.add(newValue.size()-1, 0);
						binToChange.add(binToChange.size()-1, oppositePile);
					}

				}

				// calculate if player earns free turn.
				if(boardIndex == 13) freeTurn = true;
			}
			boardIndex = (boardIndex == 13) ? 0 : boardIndex + 1;
		}

		timers[1] = new Timer(CPU_TURN_TIME, new ActionListener() {

			ArrayList<Integer> binToChange2 = binToChange;
			ArrayList<Integer> newValue2 = newValue;
			int i = 0;


			JLabel binLabel;
			JPanel binPanel;

			@Override
			public void actionPerformed(ActionEvent e) {
				if (i == binToChange2.size() - 3 && newValue2.get(newValue2.size()-2) == 0) {
					outputLabel.setText("<html><span style=\"color:rgb(0, 0, 0);\">Computer got a capture!</span></html>");
				}
				resetColors();
				if (i < binToChange.size()) {
					
					binLabel = bins[binToChange2.get(i)];
					binPanel = binPanels[binToChange2.get(i)];

					if (newValue2.get(i) == 0) {
//						binLabel.setForeground(Color.RED);
						binPanel.setBackground(Color.RED);
					} else {
//						binLabel.setForeground(GREEN);
						binPanel.setBackground(GREEN);
					}
					if (i == 1)
						bins[binToChange2.get(0)].setText(newValue2.get(0) + "");

					if (i != 0)
						binLabel.setText(newValue2.get(i) + "");

				}

				i++;
				if (i == binToChange.size()+1) {

					cleanupGame();
					if (!turn.equals("game end")) {

						if (freeTurn) {
							outputLabel.setForeground(CPU_BLUE);
							if (freeTurnCount == 1) {
								outputLabel.setText("Computer earned a free turn!");
							} else {
								outputLabel.setText("Computer earned a free turn! (#" + freeTurnCount + ")");
							}
							freeTurnCount++;
							turn = "computer";
							cpuTurn();
						} else {
							outputLabel.setForeground(Color.BLACK);
							freeTurnCount = 1;
							turn = "player";
							outputLabel.setText("<html><span style=\"color:rgb(16, 16, 178);\">Your Turn</span></html>");
						}
					}

					((Timer)e.getSource()).stop();
				}
			}
		});

		timers[1].start();

		}
		
		return freeTurn;
	}

	/**
	 * Helper method to clean up the current game and start a new one.
	 */
	private void cleanupGame() {
		int topTotal = 0;
		int botTotal = 0;

		ArrayList<JLabel> nonEmpty = new ArrayList<JLabel>();

		for (int i = 0; i < 6; i++) {
			botTotal += getBinStones(i);
			if (getBinStones(i) != 0) {
				nonEmpty.add(getLabelFromNum(i));
			}
		}

		for (int i = 7; i < 13; i++) {
			topTotal += getBinStones(i);
			if (getBinStones(i) != 0) {
				nonEmpty.add(getLabelFromNum(i));
			}
		}

		if (botTotal == 0) {
			bins[13].setText((Integer.parseInt(bins[13].getText()) + topTotal) + "");
			setBinText(7, 0);
			setBinText(8, 0);
			setBinText(9, 0);
			setBinText(10, 0);
			setBinText(11, 0);
			setBinText(12, 0);
			bins[13].setBackground(GREEN);
		} else if (topTotal == 0) {
			bins[6].setText((Integer.parseInt(bins[6].getText()) + botTotal) + "");
			setBinText(0, 0);
			setBinText(1, 0);
			setBinText(2, 0);
			setBinText(3, 0);
			setBinText(4, 0);
			setBinText(5, 0);
			bins[6].setBackground(GREEN);
		}

		if (botTotal == 0 || topTotal == 0) {
			for (int i = 0; i < nonEmpty.size(); i++) {
				nonEmpty.get(i).setBackground(Color.RED);
			}

			turn = "game end";
			if (Integer.parseInt(bins[13].getText()) > Integer.parseInt(bins[6].getText())) {
				outputLabel.setText("Computer wins!");
				computerScore++;
			} else if (Integer.parseInt(bins[13].getText()) < Integer.parseInt(bins[6].getText())) {
				outputLabel.setText("<html><b>You win!</b></html>");
				playerScore++;
			} else {
				outputLabel.setText("Tie!");
			}
			scoreLabel.setText("<html><span style=\"color:rgb(16,16,178);\">Player wins: " + playerScore + ",</span> <span style=\"color:rgb(0, 0, 0);\">Computer wins: " + computerScore +"</span></html>");

			timers[2] = new Timer(1000, new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					for (int i = 0; i < 13; i++) {
						if (i < 7)
							bins[i].setForeground(PLAYER_BLUE);
						else
							bins[i].setForeground(CPU_BLUE);
					}

				}

			});

			timers[2].setRepeats(false);
			timers[2].start();
		}
	}

	/**
	 * Helper method for resetting player colors.
	 */
	private void resetColors() {
		
		for(int i = 0; i < bins.length; i++) {
			if (i < 7)
				bins[i].setForeground(PLAYER_BLUE);
			else
				bins[i].setForeground(CPU_BLUE);
		}
		
		for(int i = 0; i < binPanels.length; i++) {
			binPanels[i].setBackground(Color.WHITE);
		}
	}

	/**
	 * Event listener for the player's bins.
	 */
	class PlayerBinListener implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {

		}

		@Override
		public void mousePressed(MouseEvent e) {

			if (turn.equals("player") && !turn.equals("game end")) {
				JPanel source = (JPanel)e.getSource();
				int sourceStones = thisFrame.getBinStones(Integer.parseInt(source.getName()));
				clickedPanel = source;
				if (sourceStones != 0) {
					playerTurn(Integer.parseInt(source.getName()));
				} else if (!turn.equals("animation")) {
					turn = "animation";
					outputLabel.setText("Invalid move");
					clickedPanel.setBackground(Color.RED);
					timers[3] = new Timer(1000, new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent arg0) {
							outputLabel.setText("<html><span style=\"color:rgb(16, 16, 178);\">Your Turn</span></html>");
							clickedPanel.setBackground(Color.WHITE);
							turn = "player";
						}

					});
					timers[3].setRepeats(false);
					timers[3].start();
				}
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {

		}

		@Override
		public void mouseEntered(MouseEvent e) {
			setCursor(new Cursor(Cursor.HAND_CURSOR));
		}

		@Override
		public void mouseExited(MouseEvent e) {
			setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		}

	}

	/**
	 * Event listener for the CPU's bins and the mancalas.
	 */
	class TopLabelAndMancalaListener implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent arg0) {

		}

		@Override
		public void mouseEntered(MouseEvent arg0) {

		}

		@Override
		public void mouseExited(MouseEvent arg0) {

		}

		@Override
		public void mousePressed(MouseEvent arg0) {
			if (turn.equals("player")) {
				turn = "animation";
				outputLabel.setText("Invalid move");

				JPanel source = (JPanel)arg0.getSource();
				clickedPanel = source;
				source.setBackground(Color.RED);

				timers[4] = new Timer(1000, new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent arg0) {
						outputLabel.setText("<html><span style=\"color:rgb(16, 16, 178);\">Your Turn</span></html>");
						turn = "player";
						clickedPanel.setBackground(Color.WHITE);
					}

				});
				timers[4].setRepeats(false);
				timers[4].start();
			}

		}

		@Override
		public void mouseReleased(MouseEvent arg0) {

		}

	}
}
