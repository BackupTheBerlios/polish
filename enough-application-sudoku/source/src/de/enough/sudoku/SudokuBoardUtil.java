/**
 * 
 */
package de.enough.sudoku;

import java.util.Random;

import javax.microedition.lcdui.Displayable;

/**
 * This class provides all neccessary functions to generate, prepare and solve
 * sudoku boards.
 * 
 * @author Simon Schmitt
 *
 */
public final class SudokuBoardUtil {
	/**
	 * Diese Funktion testet, ob das Board so weit wie es ausgefüllt ist glültig ist.
	 * @param Board
	 * @return
	 */
	public static boolean validSudokuBoard(int[][] Board){
		int col[][]=new int[9][10];
		int row[][]=new int[9][10];
		// count the numbers (1 to 9) in every row and col
		for (int i=0; i<9;i++) {
			for (int j = 0; j < 9; j++) {
				// the number 'Board[i][j]' was found
				if (++row[i][Board[i][j]]>1 &&Board[i][j]!=0)
					return false;
				//row[i][Board[i][j]]++;
				
				if (++col[i][Board[j][i]]>1 &&Board[j][i]!=0)
					return false;
				//col[i][Board[j][i]]++;
			}
		}
		// check the boxes
		for (int i=0; i<9; i++){
			if (!validBox(Board,i)){
				return false;
			}
		}
		return true;
	} 
	/**
	 * Diese Funktion überprüft, ob die 3x3 Box so weit wie es ausgefüllt ist gültig ist.
	 * @param Board
	 * @param nr die Nummer der Box (wird von oben liks gezählt)
	 * @return
	 */
	public static boolean validBox(int[][] Board, int nr){
		int offsetX=(nr%3)*3;
		int offsetY=(nr/3)*3;
		int cell[]=new int[10];
		
		for (int i = offsetX; i < offsetX+3; i++) {
			for (int j = offsetY; j < offsetY+3; j++) {
				if (++cell[Board[i][j]]>1 && Board[i][j]!=0){
					//System.out.println(" Fehler bei (" +(i+1) + " " +(j+1)+")");
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * This function marks all double numbers.
	 * @param Board
	 * @return
	 */
	public static void checkUserBoard(int[][] Board, boolean wrong [][]){
		int offsetX;
		int offsetY;
		// count the numbers (1 to 9) in every row and col
		for (int y=0; y<9;y++) {
			for (int x = 0; x < 9; x++) {
				wrong[y][x]=false;
				
				if ( Board[y][x]!=0){
					
					// check if Board[i][j] has a double at relativ posistion n 
					for (int n = 0; n < 9; n++) {
						// cols
						if ( Board[y][x]==Board[y][n] && x!=n){
							wrong[y][x]=true;
						}
						// rows
						if ( Board[y][x]==Board[n][x] && y!=n){
							wrong[y][x]=true;
						}
					}
					
					//Boxes
					offsetX=(x/3)*3;
					offsetY=(y/3)*3;
					for (int i = offsetY; i < offsetY+3; i++) {
						for (int j = offsetX; j < offsetX+3; j++) {
							if ( Board[y][x]==Board[i][j] && y!=i &&x!=j){
								wrong[y][x]=true;
							}
						}
					}
				}
				
			}
		}
		
	} 
	/**
	 * This function returns the number of empty cells in the given board.
	 * @param Board
	 * @return
	 */
	public static int countEmptyCells(int[][]Board){
		int ret=0;
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				if (Board[i][j]==0)
					ret++;
			}
		}
		return ret;
	}
	
	/**
	 *  This function receives a sudokuboard (int[9][9]) and computes recrusivly
	 *  the solution (if there is one). The solution is stored in Board.
	 * @param Board the SudokuBoard
	 * @param level just a debug information to know the deepth
	 * @return returns if a valid board was found
	 * 
	 * Note: The Board is modified depending on the mode! 
	 */
	public static final int SM_SOLVE=0;
	public static final int SM_QUICK_SEARCH=1;
	public static final int SM_FULL_SEARCH=2;
	
	public static int solveBoard(int[][]Board, int mode){
		
		int[] pValRow=new int[9];
		int[] pValCol=new int[9];
		int[] pValBox=new int[9];
		
		int tmp=0;
		
		int[][] pValCell=new int [9][9];
		int[] pValues;
	
		// check if the board is still ok (no doubles; 'no possibilites' is checked later )
		if (validSudokuBoard(Board)==false){
			return 0;
		}
		
		// scan the current state for the major stuff (row, col, box)
		for (int i = 0; i < 9; i++) {
			pValRow[i]=scanRow(Board,i);
			pValCol[i]=scanCol(Board,i);
			pValBox[i]=scanBox(Board,i);
		}
		// combine the information for each cell
		// TODO: we are just looking at one cell in this step of recrusion!!! -> less to prepare
		// TODO: we could otherwise look for the cell with fewest possibilities
		/*
		for (int iRow = 0; iRow < 9; iRow++) {
			for (int iCol = 0; iCol < 9; iCol++) {
				
				// compute if the cell is emty
				if (Board[iRow][iCol]==0){
					
					pValCell[iRow][iCol]=pValRow[iRow] & pValCol[iCol] & pValBox[(iRow/3)*3+iCol/3];
					//System.out.println("  cell: " + Integer.toBinaryString((pValCell[iRow][iCol]<<22)>>>22)); //pValCell[iRow][iCol] + " = " + Integer.toBinaryString(pValCell[iRow][iCol]));
					
					// exit, if there are no further possibilities
					if (( (pValCell[iRow][iCol]<<22)>>>22) ==0){ 
						//if (level<11) System.out.println(space+"<-- back no possibilities");
						return 0;
					}
				}
			}
		}
		*/
		boolean complete=true;
		
		// vary all cells and do recrusion over all free cells
		for (int iRow = 0; iRow < 9; iRow++) {
			for (int iCol = 0; iCol < 9; iCol++) {
				// find the next free cell
				if (Board[iRow][iCol]==0){
					
					// find the possible values
					pValRow[iRow]=scanRow(Board,iRow);
					pValCol[iCol]=scanCol(Board,iCol);
					pValBox[(iRow/3)*3+iCol/3]=scanBox(Board,(iRow/3)*3+iCol/3);
					pValCell[iRow][iCol]=pValRow[iRow] & pValCol[iCol] & pValBox[(iRow/3)*3+iCol/3];
					
					// exit, if there are no further possibilities
					if (( (pValCell[iRow][iCol]<<22)>>>22) ==0){ 
						return 0;
					}
					
					complete=false;
					// TODO the binary mode might be useless because there a so few binary operations
					pValues=getPossVal(pValCell[iRow][iCol]);
					// vary the possibilities
					for (int i = 0; i < 9; i++) {
						if (pValues[i]==0){ // do not go further then the last item 
							// all possibilities were wrong, we have to restore and go some steps back....
							Board[iRow][iCol]=0;
							// there were no possible choises for this cell, which means we have a problem in a lower level
							break;
						} else{

							Board[iRow][iCol]= pValues[i];
							
							//perform the recrusion
							// TODO a copy should not be neccessary because all data is always overwritten
							if (mode==SM_SOLVE) {
								if (solveBoard(Board, SM_SOLVE)==1){
									return 1;
								}
							} else {
								tmp += solveBoard(Board, mode);
								if (mode==SM_QUICK_SEARCH ){
									if (tmp>1){
										return tmp;
									}
								}
							}
						}
					}
					
					if (mode == SM_SOLVE){
						// we came here because there were no termination before (no solutions yet)
						return 0;
					} else{
						 //we have found (all) existing possibilities
						return tmp;
					}
				}
			}
		}
		
		// if everyting is filled
		if (complete && validSudokuBoard(Board))
			return tmp+1;
		
		// if we came here, because there was just nothing to vary or no variation was valid
		return tmp;
	}
	
	/**
	 * This funtion scans a row of a Board an returns all missing Numbers 
	 * @param Board
	 * @param row
	 * @return the integer represents the unused Numbers (isfree<<number) 
	 */
	private static int scanRow(int[][] Board, int row){
		int possibleNum=0;
		for (int i = 0; i < 9; i++) {
			possibleNum|=1<<Board[row][i];
		}
		return ~possibleNum;
	} 
	private static int scanCol(int[][] Board, int col){
		int possibleNum=0;
		for (int i = 0; i < 9; i++) {
			possibleNum|=1<<Board[i][col];
		}
		return ~possibleNum;
	} 
	private static int scanBox(int[][] Board, int nr){
		int offsetX=(nr%3)*3;
		int offsetY=(nr/3)*3;
		int possibleNum=0;
		
		for (int i = offsetX; i < offsetX+3; i++) {
			for (int j = offsetY; j < offsetY+3; j++) {
				possibleNum|=1<<Board[j][i];
			}
		}
		return ~possibleNum;
	}
	/**
	 * This function returns the possible Values, which are specified by
	 *  an boolean/int, in an Array. 
	 * @param possibilities all possible values are represented as boolean values with a certain shift
	 * @return
	 */
	private static int[] getPossVal(int possibilities){
		int[] ret=new int[9];
		int pos=0;
		//System.out.println(Integer.toBinaryString(possibilities));
		for (int i = 1; i < 10; i++) {
			if ( ((possibilities>>>i)&1) ==1){
				ret[pos]=i;
				pos++;
			}
		}
		return ret;
	}
	/** have a look at the name...
	 */
	public static void copyBoard(int[][] src, int[][] dest){
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				dest[i][j]=src[i][j];
			}
			
		}
	}
	
	/**
	 *  This function takes an empty bard, which will become filled and permuted. Afterwards the 
	 *  given amount of cells is deleted.
	 * @param Board
	 * @param emptyCells
	 */
	public static void generateSudokuBoard(int[][]Board, int emptyCells){
		// generate a board
		genValidSudokuBoard(Board);
		
		// permute it
		renameSudokuBoard(Board);
		shuffleSudokuBoard(Board);
		
		// free some cells
		freeCells(Board, emptyCells);
	}
	
	/**
	 * This function creates a more or less random (and filled) Sudokuboard, which
	 * is stored in Board[][]. You should rename, and shuffle the board, in order
	 * to make it more random-like.
	 * @param Board: your new (filled) Sudokuboard
	 */
	public static void genValidSudokuBoard(int [][] Board){
		Random rnd = new Random(System.currentTimeMillis());
		int x,y;
		do{
			// clear the board
			for (int i = 0; i < 9; i++) {
				for (int j = 0; j < 9; j++) {
					Board[i][j]=0;
				}
			}
			// spread 1 to 9 by random
			for (int i = 1; i < 10; i++) {
				x= Math.abs(rnd.nextInt())%9;
				y= Math.abs(rnd.nextInt())%9;
				Board[x][y]=i;
				/*if (!validSudokuBoard(Board)){
					Board[x][y]=0;
				}*/
			}
		// the solution is alway valid, but you should call solveBoard to fill it
		} while(solveBoard(Board,SM_SOLVE)==0);
	} 
	/**
	 *  This function renames the cells in a Board.
	 * @param Board
	 */
	private static void renameSudokuBoard(int [][] Board){
		Random rnd=new Random(System.currentTimeMillis());
		for (int n = 1; n < 10; n++) {
			// rename number 'n'
			swapNumbers(Board,n,1+ Math.abs(rnd.nextInt())%9);
		}
	}
	private static void swapNumbers(int[][] Board, int a, int b){
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				if (Board[i][j]==a) {
					Board[i][j]=b;
				} else if (Board[i][j]==b) {
					Board[i][j]=a;
				}
			}
		}
	}
	/**
	 * This function shuffles a given Board
	 * @param Board 
	 */
	private static void shuffleSudokuBoard(int[][]Board){
		Random rnd=new Random(System.currentTimeMillis());
		for (int i = 0; i < 30; i++) {
			swapBlocks(Board, Math.abs(rnd.nextInt())%3, Math.abs(rnd.nextInt())%3, Math.abs(rnd.nextInt()%2));
		}
	}
	/**
	 * This function swaps tow 9x3 Blocks 
	 * @param Board The SudokuBoard
	 * @param a the Nuber (0 to 2) of the first Block
	 * @param b the Nuber (0 to 2) of the second Block
	 * @param hv horizontal (0)/vertikal view (1)
	 */
	private static void swapBlocks(int[][] Board, int a, int b, int hv){
		swapLines(Board,3*a,3*b,hv);
		swapLines(Board,3*a+1,3*b+1,hv);
		swapLines(Board,3*a+2,3*b+2,hv);
	}
	private static void swapLines(int[][] Board, int a, int b, int hv){
		int tmp;
		for (int i = 0; i < 9; i++) {
			tmp=Board[a*(1-hv)+i*hv][i*(1-hv)+a*hv];
			Board[a*(1-hv)+i*hv][i*(1-hv)+a*hv]=Board[b*(1-hv)+i*hv][i*(1-hv)+b*hv];
			Board[b*(1-hv)+i*hv][i*(1-hv)+b*hv]=tmp;
		}
	}
	
	/** This function deletes a given number of cells in a given board such that the
	 * board has still a uique solution. Be aware that the time increases with the number
	 * of cells. It is recommended not to choose a number greater than 50.
	 * 
	 * @param Board
	 * @param emptyCells
	 */
	private static void freeCells(int[][]Board, int emptyCells){
		int freedCells=0;
		int x,y;
		int tmp;
		int[][] solveBoard=new int[9][9];
		int[][] saveBoard=new int[9][9];
		int undos=0;
		
		Random rnd = new Random(System.currentTimeMillis());

		if (emptyCells>50){
			// TODO we have a big speed problem
			System.out.println("sie verlangen zu viele leere Zellen (nur schwer zu leisten)");
		}
		
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				saveBoard[i][j]=Board[i][j];
			}
		}
		
		while(freedCells!=emptyCells){
			System.out.println(" empty"+freedCells);
			
			// make a new attepmt if you are stuck
			if (undos>emptyCells*emptyCells/50+3){ // we need an upper bound!
				freedCells=0;
				undos=0;
				for (int i = 0; i < 9; i++) {
					for (int j = 0; j < 9; j++) {
						Board[i][j]=saveBoard[i][j];
					}
				}
				System.out.println(" reset");
			}
			
			x= Math.abs(rnd.nextInt())%9;
			y= Math.abs(rnd.nextInt())%9;
			
			// delete a number
			if (Board[x][y]!=0) {
				tmp = Board[x][y];
				Board[x][y]=0;
				
				
				// make a savecopy for the next computation 
				for (int i = 0; i < 9; i++) {
					for (int j = 0; j < 9; j++) {
						solveBoard[i][j]=Board[i][j];
					}
				}
				
				// check if there is still an uniqe solution
				for (int i = 1; i < 11; i++) {
					// check if you can find an other solution
					if (i==10){
						freedCells++;
					} else if (i!=tmp){
						solveBoard[x][y]=i;
						if (solveBoard(solveBoard, SM_QUICK_SEARCH)!=0){
							Board[x][y]=tmp;
							undos++;
							System.out.println("back:"+undos + " free:"+freedCells);
							break;
						} 
					}
					
				}
				
			}
		}
	/*System.out.println("freedCells:");
	printBoard(Board);*/
	}

}
