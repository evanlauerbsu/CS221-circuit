import java.awt.Point;
import java.util.ArrayList;
import java.io.FileNotFoundException;

/**
 * Search for shortest paths between start and end points on a circuit board
 * as read from an input file using either a stack or queue as the underlying
 * search state storage structure and displaying output to the console or to
 * a GUI according to options specified via command-line arguments.
 * 
 * @author mvail, elauer
 */
public class CircuitTracer {
	private final boolean SUPPORTS_GUI = false;
	private CircuitBoard board;
	private Storage<TraceState> stateStore;
	private ArrayList<TraceState> bestPaths;

	/** Launch the program. 
	 * 
	 * @param args three required arguments:
	 *  first arg: -s for stack or -q for queue
	 *  second arg: -c for console output or -g for GUI output
	 *  third arg: input file name 
	 */
	public static void main(String[] args) {
		new CircuitTracer(args); //create this with args
	}

	/** Print instructions for running CircuitTracer from the command line. */
	private void printUsage() {
		//TODO: print out clear usage instructions when there are problems with
		// any command line args
		System.out.println("Usage: $ java CircuitTracer <-s | -q> <-c | -g> filename");
		System.out.println("       where -s uses a stack internally");
		System.out.println("             -q uses a queue internally");
		System.out.println("             -c is console output");
		System.out.println("             -g is GUI output " + (SUPPORTS_GUI ? "" : "NOTE: do not use, UNSUPPORTED"));
		if (SUPPORTS_GUI) {
		}
	}

	/** 
	 * Set up the CircuitBoard and all other components based on command
	 * line arguments.
	 * 
	 * @param args command line arguments passed through from main()
	 */
	public CircuitTracer(String[] args) {
		//TODO: parse and validate command line args - first validation provided
		if (args.length != 3) {
			printUsage();
			return; //exit the constructor immediately
		}

		switch (args[0]) {
			case "-s":
				stateStore = new Storage<TraceState>(Storage.DataStructure.stack);
				break;
			case "-q":
				stateStore = new Storage<TraceState>(Storage.DataStructure.queue);
				break;
			default:
				printUsage();
				return;
		}

		try {
			board = new CircuitBoard(args[2]);
		} catch (FileNotFoundException e) {
			System.out.println(e);
			return;
		} catch (InvalidFileFormatException e) {
			System.out.println(e);
			return;
		}

		switch (args[1]) {
			case "-c":
				search();
				System.out.print(resultToString());
				break;
			case "-g":
				if (SUPPORTS_GUI) {
					search();
				} else {
					System.out.println("Unsupported Option: -g");
					System.out.println();
					printUsage();
				}
				break;
			default:
				printUsage();
				return;
		}

		//TODO: initialize the Storage to use either a stack or queue
		//TODO: read in the CircuitBoard from the given file
		//TODO: run the search for best paths
		//TODO: output results to console or GUI, according to specified choice
	}

	/**
	 * Search the board for the best tracer paths. Uses a brute force algorithm.
	 */
	private void search() {
		bestPaths = new ArrayList<TraceState>();

		Point start = board.getStartingPoint();
		int row = (int) start.getX();
		int col = (int) start.getY();
		if (board.isOpen(row + 1, col)) {
			stateStore.store(new TraceState(board, row + 1, col));
		}
		if (board.isOpen(row - 1, col)) {
			stateStore.store(new TraceState(board, row - 1, col));
		}
		if (board.isOpen(row, col + 1)) {
			stateStore.store(new TraceState(board, row, col + 1));
		}
		if (board.isOpen(row, col - 1)) {
			stateStore.store(new TraceState(board, row, col - 1));
		}

		while (!stateStore.isEmpty()) {
			TraceState currentTrace = stateStore.retrieve();
			if (currentTrace.isSolution()) {
				if (bestPaths.isEmpty() || bestPaths.get(0).pathLength() == currentTrace.pathLength()) {
					bestPaths.add(currentTrace);
				} else if (bestPaths.get(0).pathLength() > currentTrace.pathLength()) {
					bestPaths.clear();
					bestPaths.add(currentTrace);
				}
			} else {
				row = currentTrace.getRow();
				col = currentTrace.getCol();
				if (currentTrace.isOpen(row + 1, col)) {
					stateStore.store(new TraceState(currentTrace, row + 1, col));
				}
				if (currentTrace.isOpen(row - 1, col)) {
					stateStore.store(new TraceState(currentTrace, row - 1, col));
				}
				if (currentTrace.isOpen(row, col + 1)) {
					stateStore.store(new TraceState(currentTrace, row, col + 1));
				}
				if (currentTrace.isOpen(row, col - 1)) {
					stateStore.store(new TraceState(currentTrace, row, col - 1));
				}
			}
		}
	}

	private String resultToString() {
		String str = "";
		for (TraceState path : bestPaths) {
			str += path.toString();
			str += "\n";
		}
		return str;
	}
} // class CircuitTracer
