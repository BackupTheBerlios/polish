/**
 * 
 */
package de.enough.sudoku;

/**
 * This class provides all neccessary functions to generate, prepare and solve
 * sudoku boards.
 * 
 * @author Simon Schmitt
 *
 */
public class SudokuBoardUtil {
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
	
	
	
}
