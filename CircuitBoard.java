import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Represents a 2D circuit board as read from an input file.
 *  
 * @author mvail
 */
public class CircuitBoard {
	/** current contents of the board */
	private char[][] board;
	/** location of row,col for '1' */
	private Point startingPoint;
	/** location of row,col for '2' */
	private Point endingPoint;

	//constants you may find useful
	private final int ROWS; //initialized in constructor
	private final int COLS; //initialized in constructor
	private final char OPEN = 'O'; //capital 'o', an open position
	private final char CLOSED = 'X';//a blocked position
	private final char TRACE = 'T'; //part of the trace connecting 1 to 2
	private final char START = '1'; //the starting component
	private final char END = '2'; //the ending component
	private final String ALLOWED_CHARS = "OXT12"; //useful for validating with indexOf

	/** Construct a CircuitBoard from a given board input file, where the first
	 * line contains the number of rows and columns as ints and each subsequent
	 * line is one row of characters representing the contents of that position.
	 * Valid characters are as follows:
	 *  'O' an open position
	 *  'X' an occupied, unavailable position
	 *  '1' first of two components needing to be connected
	 *  '2' second of two components needing to be connected
	 *  'T' is not expected in input files - represents part of the trace
	 *   connecting components 1 and 2 in the solution
	 * 
	 * @param filename
	 * 		file containing a grid of characters
	 * @throws FileNotFoundException if Scanner cannot open or read the file
	 * @throws InvalidFileFormatException for any file formatting or content issue
	 */
	public CircuitBoard(String filename) throws FileNotFoundException {
		Scanner fileScan = new Scanner(new File(filename));

		//TODO: parse the given file to populate the char[][]
		// throw FileNotFoundException if Scanner cannot read the file
		// throw InvalidFileFormatException if any issues are encountered while parsing the file
		String[] dimensions = fileScan.nextLine().split("\\s+");
		if (dimensions.length != 2) {
			fileScan.close();
			throw new InvalidFileFormatException("Wrong number of dimensions");
		}

		ROWS = Integer.parseInt(dimensions[0]);
		COLS = Integer.parseInt(dimensions[1]);

		int rowCount = 0;
		while (fileScan.hasNext()) {
			rowCount++;
			String[] row = fileScan.nextLine().split("\\s+");

			if (row.length != COLS) {
				fileScan.close();
				throw new InvalidFileFormatException("Wrong number of columns");
			}

			int colCount = 0;
			for (String item : row) {
				int validityIndex = ALLOWED_CHARS.indexOf(item);
				// T is not allowed in grid and index of T is 2
				boolean isValidChar = validityIndex == -1 || validityIndex == 2;
				if (isValidChar) {
					fileScan.close();
					throw new InvalidFileFormatException("Invalid Char in grid");
				}

				if (!item.equals(item.substring(0, 1))) {
					fileScan.close();
					throw new InvalidFileFormatException("No whitespace separating Chars in grid");
				}

				colCount++;
				char itemChar = item.charAt(0);

				if (itemChar == START) {
					startingPoint = new Point(rowCount, colCount);
				} else if (itemChar == END) {
					endingPoint = new Point(rowCount, colCount);
				}

				board[rowCount][colCount] = itemChar;

			}
		}
		fileScan.close();

		if (rowCount != ROWS) {
			throw new InvalidFileFormatException("Wrong number of rows");
		}

		if (startingPoint == null || endingPoint == null) {
			throw new InvalidFileFormatException("Missing starting or ending point");
		}
	}

	/** Copy constructor - duplicates original board
	 * 
	 * @param original board to copy
	 */
	public CircuitBoard(CircuitBoard original) {
		board = original.getBoard();
		startingPoint = new Point(original.startingPoint);
		endingPoint = new Point(original.endingPoint);
		ROWS = original.numRows();
		COLS = original.numCols();
	}

	/** Utility method for copy constructor
	 * @return copy of board array */
	private char[][] getBoard() {
		char[][] copy = new char[board.length][board[0].length];
		for (int row = 0; row < board.length; row++) {
			for (int col = 0; col < board[row].length; col++) {
				copy[row][col] = board[row][col];
			}
		}
		return copy;
	}

	/** Return the char at board position x,y
	 * @param row row coordinate
	 * @param col col coordinate
	 * @return char at row, col
	 */
	public char charAt(int row, int col) {
		return board[row][col];
	}

	/** Return whether given board position is open
	 * @param row
	 * @param col
	 * @return true if position at (row, col) is open 
	 */
	public boolean isOpen(int row, int col) {
		if (row < 0 || row >= board.length || col < 0 || col >= board[row].length) {
			return false;
		}
		return board[row][col] == OPEN;
	}

	/** Set given position to be a 'T'
	 * @param row
	 * @param col
	 * @throws OccupiedPositionException if given position is not open
	 */
	public void makeTrace(int row, int col) {
		if (isOpen(row, col)) {
			board[row][col] = TRACE;
		} else {
			throw new OccupiedPositionException("row " + row + ", col " + col + "contains '" + board[row][col] + "'");
		}
	}

	/** @return starting Point(row,col) */
	public Point getStartingPoint() {
		return new Point(startingPoint);
	}

	/** @return ending Point(row,col) */
	public Point getEndingPoint() {
		return new Point(endingPoint);
	}

	/** @return number of rows in this CircuitBoard */
	public int numRows() {
		return ROWS;
	}

	/** @return number of columns in this CircuitBoard */
	public int numCols() {
		return COLS;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuilder str = new StringBuilder();
		for (int row = 0; row < board.length; row++) {
			for (int col = 0; col < board[row].length; col++) {
				str.append(board[row][col] + " ");
			}
			str.append("\n");
		}
		return str.toString();
	}

}// class CircuitBoard
