/**
 * 	Sudoku Master is a j2me-polish using sudoku game for mobilephones 
 * 
 *  This particular class handels the interface and user interaction.
 * 
 * @author Simon Schmitt
 *
 */
package de.enough.sudoku;

//import java.lang.ref.Reference;

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

//import de.enough.polish.ui.Gauge;
import javax.microedition.lcdui.Gauge;
import de.enough.polish.ui.Screen;
import de.enough.polish.ui.UiAccess;
import de.enough.polish.util.HashMap;
import de.enough.polish.util.Locale;

/*
 * Färbung/aktuelles update
 */
public class Sudoku extends MIDlet implements javax.microedition.lcdui.CommandListener, ItemStateListener {
	private Display display;
	private List mainMenu;
	private List gameSelList;
	//private List gameModeList;
	private Form aboutForm;
	private Form settingstForm;
	private Form highScoreForm;
	private Form helpForm;
	private Form waitForm;
	
	// TODO cange into GamgeForm
	private Form gameForm;
	
	private Command exit;
	private Command cmdBack;
	private Command cmdStartGame;
	
	private Command gameQuit, gamePause, gameHint, gameSolve, gameNew, gameReset,gameContinue;
	
	private Gauge genSudokuGauge;
	
	private int[][] nativeBoard=new int[9][9];
	private int[][] userBoard=new int[9][9];
	private boolean[][] wrongBoard=new boolean[9][9];
	private int[][] solvedBoard=new int[9][9];
	private int hintCount;
	private boolean autoSolved;
	

	
	
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
	
	// useful for drawing animations/ detecting changes.... 
	/*private byte[][] lastBoardState=new byte[9][9];
	private static byte SUDOKUCELLWRONG=1;
	private static byte SUDOKUCELLCONFLICT=2;
	private static byte SUDOKUCELLNATIVE=3;
	private static byte SUDOKUCELLUSER=4;*/
	
	// game difficulty
	private int gameLevel;
	
	private static int GAME_LEVEL_EASY=0;
	private static int GAME_LEVEL_MEDIUM=1;
	private static int GAME_LEVEL_HARD=2;
	private static int GAME_LEVEL_MASTER=3;

	private static int[] HINTS = {1000,5,2,0};
	private static int[] EMPTY_CELLS = {35,40,45,50};
	private static boolean[] HIGHLIGHTING = {true,true,false,false};
	
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
			
			this.cmdBack = new Command( Locale.get( "cmd.Back" ), Command.BACK, 2 );
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
		} else if (cmd == this.cmdBack){
			// return to menu
			this.display.setCurrent(this.mainMenu);
		}/*else if (cmd==this.cmdStartGame){
			
		}*/
		
		//check the list
		if (cmd==List.SELECT_COMMAND && screen==this.mainMenu){
			switch (((List) screen).getSelectedIndex()) {
			case 0:
				// ask for dificulty level
				showGameSelection();
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
		}
		
		// check the inGame menu
		if (screen==this.gameForm){
			if (cmd==this.gameQuit){
				// back to the menu
				this.display.setCurrent(this.gameSelList);
			} else if (cmd==this.gamePause){
				// TODO black screen/or about ??
			} else if (cmd==this.gameHint){
				// solve the selected cell
				showHint((Screen) screen);
			} else if (cmd==this.gameSolve){
				// solve and disable highscore entry
				solveCurrentBoard();
			} else if (cmd==this.gameReset){
				fillBoard(this.nativeBoard);
			} else if (cmd==this.gameNew){
				showGameSelection();
			}
		}
		
		if (cmd==List.SELECT_COMMAND && screen==this.gameSelList){
			// get the level
			// TODO ?? legal?
			gameLevel=((List) screen).getSelectedIndex();
			
			// launch the Game
			startGame();
		}
	}
	/**
	 * Handles the Game/Board interaction
	 */
	public void itemStateChanged(Item item){
		
		// catch changes in the Board/TextFields (currentBoard)
		// TODO: replace by ui.access.getSelItem()...
		Index index= (Index)this.hmIndexCells.get(item);
		if (index!=null){
			
			int value=this.txtCells[index.row][index.col].getValue();
			
			// TODO: there is a problem if the user enters the number and the focus is on the right

			if (value==0 /*|| value.equals("0")*/) {
				this.userBoard[index.row][index.col]=0;
			} else {
				this.userBoard[index.row][index.col]= value;
				/*if (this.userBoard[index.row][index.col]==0){
					this.txtCells[index.row][index.col].setString("");			
				}*/
			}
			
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
//		 TODO read the settins
		
		// load the screen
		if (this.gameSelList==null){
			
			this.gameSelList = new List( Locale.get("title.gameSel") , List.IMPLICIT );
			
			//this.mainMenu = new List( "Sudoku Master");
			
			this.gameSelList.append( Locale.get( "gameSel.easy"), null);
			this.gameSelList.append( Locale.get( "gameSel.medium"), null);
			this.gameSelList.append( Locale.get( "gameSel.hard"), null);
			this.gameSelList.append( Locale.get( "gameSel.master"), null);
			
			this.gameSelList.addCommand( this.cmdBack );
			//this.gameSelList.addCommand( this.cmdStartGame );
			this.gameSelList.setCommandListener( this );
		}
		// show the form
		this.display.setCurrent( this.gameSelList );
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
			
			this.highScoreForm.addCommand( this.cmdBack );
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
			
			this.settingstForm.addCommand( this.cmdBack );
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
			
			this.aboutForm = new Form( Locale.get("title.About") );
			this.aboutForm.append( Locale.get("about.text"));
			
			this.aboutForm.addCommand( this.cmdBack );
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
			
			this.helpForm.addCommand( this.cmdBack );
			this.helpForm.setCommandListener( this );
		}
		// show the aboutscreen
		this.display.setCurrent( this.helpForm );
	}
	
	private void showPleaseWait(){
		// load the please wait screen
		if (this.waitForm==null){
			this.waitForm=new Form (Locale.get("title.wait"));
			//this.waitForm.append(Locale.get("wait.description"));
			
			this.genSudokuGauge=new Gauge(Locale.get("wait.description"), false,Gauge.INDEFINITE,Gauge.CONTINUOUS_RUNNING); 
			
			this.waitForm.append(this.genSudokuGauge); 
			
			//this.waitForm =
			//this.waitForm.setLabel(Locale.get("wait.description"));
		}
		// show the screen
		this.display.setCurrent(this.waitForm);

		// TODO start the planned GameForm-Thread, which handles the rest (generation and drawing ...)
	}
	/**
	 * intialize if neccessary and ask the user about his prefered game
	 * 	TODO: save the last used state
	 */
	private void startGame() {
		// TODO show "please wait..."
		showPleaseWait();
		
		// TODO start the planned GameForm-Thread, which handles the rest (generation and drawing ...)
		
		// load the screen
		if (this.gameForm==null){
			
			//#style .sudokuContainer
			this.gameForm = new Form( /*Locale.get("title.StartGame")*/ "Sudoku Master" );
			//this.gameSelForm.append( "" );
			
			this.txtCells=new SudokuTextField[9][9];
			Index index;
			
			for (int i = 0; i < 9; i++) {
				for (int j = 0; j < 9; j++) {
					this.txtCells[i][j]= new SudokuTextField( 1, 9 ); //new TextField(null,"0"/*+i+","+j*/,2,TextField.NUMERIC | TextField.NON_PREDICTIVE);
					index=new Index(i,j);
					this.hmIndexCells.put(this.txtCells[i][j], index);
					//#style .sudokuCell
					this.gameForm.append(this.txtCells[i][j]);
				}
			}
			
			this.gameQuit = new Command( Locale.get( "cmd.gameQuit" ), Command.EXIT, 2 );
			this.gamePause = new Command( Locale.get( "cmd.gamePause" ), Command.SCREEN, 2 );
			this.gameHint = new Command( Locale.get( "cmd.gameHint" ), Command.SCREEN, 2 );
			this.gameSolve = new Command( Locale.get( "cmd.gameSolve" ), Command.SCREEN, 2 );
			this.gameReset = new Command( Locale.get( "cmd.gameReset" ), Command.SCREEN, 2 );
			this.gameNew = new Command( Locale.get( "cmd.gameNew" ), Command.SCREEN, 2 );
			this.gameContinue = new Command( Locale.get( "cmd.gameContinue" ), Command.SCREEN, 2 );
			
			this.gameForm.addCommand(this.gameQuit);
			this.gameForm.addCommand(this.gamePause);
			this.gameForm.addCommand(this.gameHint);
			this.gameForm.addCommand(this.gameSolve);
			this.gameForm.addCommand(this.gameReset);
			this.gameForm.addCommand(this.gameNew);
			this.gameForm.addCommand(this.gameContinue);
			
			this.gameForm.setCommandListener( this );
			this.gameForm.setItemStateListener(this);
		}
		
		// TODO: generate the Board
		int board[][]=new int[9][9];
		
		
		SudokuBoardUtil.generateSudokuBoard(board, EMPTY_CELLS[this.gameLevel] );
		//this.hintCount=HINTS[gameLevel]; will be set in fillBoard
		
		// fill the board
		fillBoard(board);
		
		// show the game
		this.display.setCurrent( this.gameForm );
	}
	/**
	 * This function fills the numbers into a the TextFields array
	 *  They become visible when the board is shown.
	 *  
	 *  It also solves the board such that solvedBoard[][] is properly defined.
	 */
	private void fillBoard(int[][] board){
		// solve the board (it has to be valid!)
	    SudokuBoardUtil.copyBoard(board,this.solvedBoard);
	    SudokuBoardUtil.solveBoard(this.solvedBoard, SudokuBoardUtil.SM_SOLVE);
	    
	    this.hintCount=HINTS[this.gameLevel];
	    this.autoSolved=false;
		
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				SudokuTextField currentCell = this.txtCells[i][j];
				if (board[i][j]!=0){
					// enter the native number and lock the textfield
					currentCell.setString(""+ board[i][j]);
					currentCell.setConstraints(TextField.UNEDITABLE);
					//#style sudokuCellNative
					UiAccess.setStyle( currentCell );
					this.userBoard[i][j]= board[i][j];
				} else {
					// delete the contend of the textfield
					currentCell.setConstraints(TextField.NUMERIC);
					currentCell.setString("");
					//#style sudokuCellEmpty
					UiAccess.setStyle( currentCell );
					this.userBoard[i][j]=0;
				}
				this.nativeBoard[i][j]=board[i][j];
				this.wrongBoard[i][j]=false;
			}
		}
		// TODO set the cursor to a certain position
	}
	/**
	 * These functions will syncronise the current state and the display, which
	 * means that all cells become the value specified by userBoard and the 
	 * higlighting specified by wrongBoard and nativeBoard
	 *
	 */
	private void refreshBoard(){
		refreshBoard(false);
	}
	private void refreshBoard(boolean refreshData){
		//check if the user  wants highlighting/help 
		if (HIGHLIGHTING[this.gameLevel]){
			SudokuBoardUtil.checkUserBoard(this.userBoard, this.wrongBoard);
		}

		// set the color
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				if (refreshData){
					this.txtCells[i][j].setString(""+ this.userBoard[i][j]);
				}
				
				// TODO verlassen des Focus setzt den Style immer auf emptyCell bzw. das zu letzt ausgewählte
				// so sind z.B. ehemal gefüllte leere Felder blau(userCell) und von anfang an leere Felder gelb (emptyCell)
				
				// TODO use SUDOKUCELLWRONG etc... for starting animations 
				
				// find out which color should be assigned and look if it has it jet before
				if (this.wrongBoard[i][j]){
					
					if (this.nativeBoard[i][j]==0){
						// the use made a mistake
						
						/*if (this.lastBoardState[i][j]!=SUDOKUCELLWRONG){
							this.lastBoardState[i][j]=SUDOKUCELLWRONG;*/
							
							//#style sudokuCellWrong
							UiAccess.setStyle( this.txtCells[i][j] );
							//System.out.println("sudokuCellWrong");
						//}
						
					} else{
						// the cell is a native cell and there was a confilict detected 
						
						/*if (this.lastBoardState[i][j]!=SUDOKUCELLCONFLICT){
							this.lastBoardState[i][j]=SUDOKUCELLCONFLICT;*/
							
							//#style sudokuCellConflict
							UiAccess.setStyle( this.txtCells[i][j] );
							//System.out.println("sudokuCellConflict");
						//}
					}
					
				} else {
					if (this.nativeBoard[i][j]!=0){
						// a normal native cell
						/*if (this.lastBoardState[i][j]!=SUDOKUCELLNATIVE){
							this.lastBoardState[i][j]=SUDOKUCELLNATIVE;*/
							
							//#style sudokuCellNative
							UiAccess.setStyle( this.txtCells[i][j] );
							//System.out.println("sudokuCellNative");
						//}
					} else if (this.userBoard[i][j]==0){
						//#style sudokuCellEmpty
						UiAccess.setStyle( this.txtCells[i][j] );
					}	else if (this.userBoard[i][j]!=0) {
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
	/**
	 * This function checks whether a hint is alowed and possible. If it is so the 
	 * hint is given. It looks for the TextField in screen.
	 */
	private void showHint(Screen screen){
		int index= UiAccess.getFocusedIndex(screen);
		
		// check if a hint is legal due to the users settings
		if (this.hintCount==0){
			if (HINTS[this.gameLevel]==0){
				// TODO: tell the user that hints are not able in this mode
				System.out.println(" keine Hints in diesem mode");
			}
			return;
		}
		
		// look also at autoSolved
		if (this.autoSolved){
			return;
		}
		
		if (this.nativeBoard[index/9][index%9]==0){
			this.txtCells[index/9][index%9].setValue(this.solvedBoard[index/9][index%9]);
			//TODO hint modus oder vielleicht in native, damit der user es nicht ändern kann
			this.userBoard[index/9][index%9]=this.solvedBoard[index/9][index%9];
			this.hintCount--;
			refreshBoard();
		} else{
			// there will be no hint, but maybe a message...
		}
	}
	private void solveCurrentBoard(){
		// solve colorate and show the board
		SudokuBoardUtil.copyBoard(this.solvedBoard, this.userBoard);
		//SudokuBoardUtil.checkUserBoard(this.userBoard, this.wrongBoard);
		refreshBoard(true);
		
		this.autoSolved=true;
	}
	
}
