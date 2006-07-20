/**
 * 
 */
package de.enough.sudoku;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.ItemStateListener;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.TextField;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import de.enough.polish.ui.UiAccess;
import de.enough.polish.util.HashMap;
import de.enough.polish.util.Locale;

/**
 * 	Sudoku Master is a j2me-polish using sudoku game for mobilephones 
 * 
 *  This particular class handels the interface and user interaction.
 * 
 * @author Simon Schmitt
 *
 */
/*
 * Färbung/aktuelles update
 */
public class Sudoku extends MIDlet implements javax.microedition.lcdui.CommandListener, ItemStateListener {
	private Display display;
	private List mainMenu;
	private Form gameSelForm;
	private Form aboutForm;
	private Form settingstForm;
	private Form highScoreForm;
	private Form helpForm;
	
	private Form gameForm;
	
	private Command exit;
	private Command cmdReturn;
	private Command cmdStartGame;
	
	private int[][] nativeBoard=new int[9][9];
	private int[][] userBoard=new int[9][9];
	private boolean[][] wrongBoard=new boolean[9][9];
	private byte[][] lastBoardState=new byte[9][9];
	
	SudokuTextField txtCells[][];
	HashMap hmIndexCells=new HashMap(9*9);
	
	private class Index extends Object{
		public int row,col;
		Index(){
			//
		}
		Index(int row, int col){
			this.row=row;
			this.col=col;
		}
	}
	
	private static byte SUDOKUCELLWRONG=1;
	private static byte SUDOKUCELLCONFLICT=2;
	private static byte SUDOKUCELLNATIVE=3;
	private static byte SUDOKUCELLUSER=4;
	
	int[][] testBoard={
	/*{0,0,5,9,2,4,8,0,0},//http://www.orangeminds.com/sudoku/sudoku.asp?board=1#
	{0,6,4,0,8,0,1,9,0}, //450ms
	{0,8,0,0,7,0,0,4,0},
	{0,9,0,8,5,6,0,7,0},
	{0,1,6,2,0,7,9,5,0},
	{0,0,8,0,0,0,2,0,0},
	{0,0,3,7,0,8,6,0,0},
	{0,0,0,3,0,5,0,0,0},
	{0,0,0,1,0,2,0,0,0}
	};*/
		{1,3,5,9,2,4,8,6,7},
		{7,6,0,5,8,3,1,9,2},
		{2,8,9,6,7,1,5,4,3},
		{3,9,2,8,5,6,4,7,1},
		{4,1,6,2,3,7,9,5,8},
		{5,7,8,4,1,9,2,3,6},
		{9,2,3,7,4,8,6,1,5},
		{8,4,1,3,6,5,7,2,9},
		{6,5,7,1,9,2,3,8,0}
		};
	
	/* (non-Javadoc)
	 * @see javax.microedition.midlet.MIDlet#destroyApp(boolean)
	 */
	protected void destroyApp(boolean arg0) throws MIDletStateChangeException {
		//
	}

	/* (non-Javadoc)
	 * @see javax.microedition.midlet.MIDlet#pauseApp()
	 */
	protected void pauseApp() {
		//
	}

	/* (non-Javadoc)
	 * @see javax.microedition.midlet.MIDlet#startApp()
	 */
	protected void startApp() throws MIDletStateChangeException {

		if (this.mainMenu==null){
			this.mainMenu = new List( "Sudoku Master", List.IMPLICIT );
			
			this.mainMenu.append( Locale.get( "menu.StartGame"), null);
			this.mainMenu.append( Locale.get( "menu.Highscore"), null);
			this.mainMenu.append( Locale.get( "menu.Settings"), null);
			this.mainMenu.append( Locale.get( "menu.About"), null);
			this.mainMenu.append( Locale.get( "menu.Help"), null);
			this.mainMenu.append( Locale.get( "menu.Exit"), null);
			
			this.mainMenu.setCommandListener(this);	
			Display.getDisplay(this).setCurrent(this.mainMenu);
			
			this.cmdReturn = new Command( Locale.get( "cmd.Back" ), Command.BACK, 2 );
			this.cmdStartGame = new Command( Locale.get( "cmd.Start" ), Command.OK, 1 );
			
//			 TODO read the settins/highscore etc...
		}
		this.display = Display.getDisplay( this );
		this.display.setCurrent( this.mainMenu );
	}
	/**
	 * Handles the menu
	 */
	public void commandAction (Command cmd, Displayable screen){
		// check the commands
		if (cmd == this.exit) {
			notifyDestroyed();
		} else if (cmd == this.cmdReturn){
			// return to menu
			this.display.setCurrent(this.mainMenu);
		}else if (cmd==this.cmdStartGame){
			// TODO read the settins
			// launch the Game
			startGame();
		}
		
		//check the list
		if (cmd==List.SELECT_COMMAND && screen==this.mainMenu){
			switch (((List) screen).getSelectedIndex()) {
			case 0:
				// TODO: ask for dificulty level
				showGameSelection();
				//this.gameScreen.start();
				//this.display.setCurrent( this.gameScreen );
				break;
			case 1: 
				// show the highscore
				showHighscore();
				break;
			case 2:
				// settings
				showSettings();
				break;
			case 3: 
				// about
				showAbout();
				break;
			case 4:
				// help
				showHelp();
				break;
			case 5:
				//exit
				// TODO ask before
				notifyDestroyed();
				break;
			}
			
			System.out.println("   - something happened");
		}
	}
	/**
	 * Handles the Game/Board interaction
	 */
	public void itemStateChanged(Item item){
		
		// catch changes in the Board/TextFields (currentBoard)
		Index index= (Index)this.hmIndexCells.get(item);
		if (index!=null){
			
			String value=this.txtCells[index.row][index.col].getString();
			
			// TODO: there is a problem if the user enters the number and the focus is on the right
			if (value.length()>1){
				// cut the beginning off
				value= value.substring(value.length()-1, value.length());
				this.txtCells[index.row][index.col].setString(value);
			} 
			
			if (value.length()==0) {
				this.userBoard[index.row][index.col]=0;
			} else {
				this.userBoard[index.row][index.col]= Integer.parseInt(value);
			}
				
			
			// define the new colors
			SudokuBoardUtil.checkUserBoard(this.userBoard, this.wrongBoard);
			// set the colors
			refreshBoard();
			//System.out.println("empty: "+SudokuBoardUtil.countEmptyCells(this.userBoard));
			
			// check if the board is finished
			if (SudokuBoardUtil.countEmptyCells(this.userBoard)==0) {
				if (SudokuBoardUtil.validSudokuBoard(this.userBoard)){
					System.out.println(" das Board wurde gelöst");
					// TODO improve the design
				} else{
					System.out.println(" leider enthält ihr Board noch Fehler");
				}
			}
		}
		
	}
	
	
	/**
	 * intialize if neccessary and ask the user about his prefered game
	 * 	TODO: save the last used state
	 */
	private void showGameSelection() {
		// load the screen
		if (this.gameSelForm==null){
			
			this.gameSelForm = new Form( Locale.get("title.StartGame") );
			//this.gameSelForm.append( "" );
			
			this.gameSelForm.addCommand( this.cmdReturn );
			this.gameSelForm.addCommand( this.cmdStartGame );
			this.gameSelForm.setCommandListener( this );
		}
		// show the form
		this.display.setCurrent( this.gameSelForm );
	}
	
	/**
	 * intialize if neccessary and show the high score 
	 * 	TODO: load the highscore
	 */
	private void showHighscore() {
		// load the screen
		if (this.highScoreForm==null){			
			
			this.highScoreForm = new Form( Locale.get("title.Highscore") );
			this.highScoreForm.append( "all winners" );
			
			this.highScoreForm.addCommand( this.cmdReturn );
			this.highScoreForm.setCommandListener( this );
		}
		// show the form
		this.display.setCurrent( this.highScoreForm );
	}
	
	
	/**
	 * intialize if neccessary and show the settingsScreen 
	 * 
	 *  TODO:shall contain
	 * 			- skin
	 * 			- music
	 * 			- maybe help/user support
	 * 
	 * TODO: read and save the settings
	 */
	private void showSettings() {
		// load the screen
		if (this.settingstForm==null){			
			
			this.settingstForm = new Form( Locale.get("title.Settings") );
			this.settingstForm.append( "lots of cool settings" );
			
			this.settingstForm.addCommand( this.cmdReturn );
			this.settingstForm.setCommandListener( this );
		}
		// show the Settings
		this.display.setCurrent( this.settingstForm );
	}
	
	/**
	 * intialize if neccessary and show the about information 
	 */
	private void showAbout() {
		// load the about screen
		if (this.aboutForm==null){			
			///#style aboutScreen
			this.aboutForm = new Form( Locale.get("title.About") );
			//#style .aboutText, focused, default
			this.aboutForm.append( Locale.get("about.text"));
			
			this.aboutForm.addCommand( this.cmdReturn );
			this.aboutForm.setCommandListener( this );
		}
		// show the aboutscreen
		this.display.setCurrent( this.aboutForm );
	}
	
	/**
	 * intialize if neccessary and show the help 
	 */
	private void showHelp() {
		// load the about screen
		if (this.helpForm==null){			
			
			this.helpForm = new Form( Locale.get("title.Help") );
			this.helpForm.append( Locale.get("help.text") );
			
			this.helpForm.addCommand( this.cmdReturn );
			this.helpForm.setCommandListener( this );
		}
		// show the aboutscreen
		this.display.setCurrent( this.helpForm );
	}
	
	/**
	 * intialize if neccessary and ask the user about his prefered game
	 * 	TODO: save the last used state
	 */
	private void startGame() {
		// load the screen
		if (this.gameForm==null){
			
			//#style .sudokuContainer
			this.gameForm = new Form( /*Locale.get("title.StartGame")*/ "Sudoku Master" );
			//this.gameSelForm.append( "" );
			
			this.txtCells=new SudokuTextField[9][9];
			Index index;
			
			for (int i = 0; i < 9; i++) {
				for (int j = 0; j < 9; j++) {
					this.txtCells[i][j]= new SudokuTextField( 0, 9 ); //new TextField(null,"0"/*+i+","+j*/,2,TextField.NUMERIC | TextField.NON_PREDICTIVE);
					index=new Index(i,j);
					this.hmIndexCells.put(this.txtCells[i][j], index);
					//#style .sudokuCell
					this.gameForm.append(this.txtCells[i][j]);
				}
			}
			
			// TODO quit, pause, hint, solve
			
			this.gameForm.setCommandListener( this );
			this.gameForm.setItemStateListener(this);
		}
		
		// TODO: generate the Board
		int board[][]=new int[9][9];
		
		board=testBoard;
		
		// fill the board
		fillBoard(board);
		
		// show the game
		this.display.setCurrent( this.gameForm );
	}
	/**
	 * This function fills the numbers into a the TextFields array
	 *  They become visible when the board is shown
	 */
	private void fillBoard(int[][] board){
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				SudokuTextField currentCell = this.txtCells[i][j];
				if (board[i][j]!=0){
					// enter the native number and lock the textfield
					currentCell.setString(""+ board[i][j]);
					currentCell.setConstraints(TextField.UNEDITABLE);
					this.userBoard[i][j]= board[i][j];
				} else {
					// delete the contend of the textfield
					currentCell.setConstraints(TextField.NUMERIC);
					currentCell.setString("");
					this.userBoard[i][j]=0;
				}
				this.nativeBoard[i][j]=board[i][j];
				this.wrongBoard[i][j]=false;
			}
		}
	}
	private void refreshBoard(){
		refreshBoard(false);
	}
	private void refreshBoard(boolean refreshData){
		// do not check first, because this depends on the users settings 
		
		// set the color
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				if (refreshData){
					this.txtCells[i][j].setString(""+ this.userBoard[i][j]);
				}
				
				// TODO verlassen des Focus setzt die Farbe imemr auf schwarz
				
				// find out which color should be assigned and look if it has it jet before
				if (this.wrongBoard[i][j]){
					
					if (this.nativeBoard[i][j]==0){
						/*if (this.lastBoardState[i][j]!=SUDOKUCELLWRONG){
							this.lastBoardState[i][j]=SUDOKUCELLWRONG;*/
							
							//#style sudokuCellWrong
							UiAccess.setStyle( this.txtCells[i][j] );
							//System.out.println("sudokuCellWrong");
						//}
						
					} else{
						/*if (this.lastBoardState[i][j]!=SUDOKUCELLCONFLICT){
							this.lastBoardState[i][j]=SUDOKUCELLCONFLICT;*/
							
							//#style sudokuCellConflict
							UiAccess.setStyle( this.txtCells[i][j] );
							//System.out.println("sudokuCellConflict");
						//}
					}
					
				} else {
					if (this.nativeBoard[i][j]!=0){
						/*if (this.lastBoardState[i][j]!=SUDOKUCELLNATIVE){
							this.lastBoardState[i][j]=SUDOKUCELLNATIVE;*/
							
							//#style sudokuCellNative
							UiAccess.setStyle( this.txtCells[i][j] );
							//System.out.println("sudokuCellNative");
						//}
					} else if (this.userBoard[i][j]==0){
						//#style .focused
						UiAccess.setStyle( this.txtCells[i][j] );
					}	else {
						/*if (this.lastBoardState[i][j]!=SUDOKUCELLUSER){
							this.lastBoardState[i][j]=SUDOKUCELLUSER;*/
							
							//#style sudokuCellUser
							UiAccess.setStyle( this.txtCells[i][j] );
							//System.out.println("sudokuCellUser");
						//}
					}
				}
			}
		}
	}
	
}
