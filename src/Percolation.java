//Percolation code

import java.awt.Color;
import java.awt.event.KeyEvent;

public class Percolation {

	private boolean[][] grid;
	private int gridSize;
	private int gridSquared;
	private WeightedQuickUnionUF wquFind;
	private int virtualTop;
	private int virtualBottom;

	// Constructor
	public Percolation(int n) {
		gridSize = n;
		gridSquared = gridSize * gridSize;
		wquFind = new WeightedQuickUnionUF(gridSquared + 2);
		grid = new boolean[gridSize][gridSize];
		virtualTop = gridSquared;
		virtualBottom = gridSquared + 1;
	}

	// Method to open a site at (row, col)
	public void openSite(int row, int col) {
		if (grid[row][col]) return;  // Already open, do nothing

		grid[row][col] = true;
		int index = row * gridSize + col;

		if (row == 0) {
			wquFind.union(virtualTop, index);  // Connect to virtual top
		}

		if (row == gridSize - 1) {
			wquFind.union(index, virtualBottom);  // Connect to virtual bottom
		}

		connectAdjacentSites(row, col, index);
	}

	// Method to connect adjacent open sites
	private void connectAdjacentSites(int row, int col, int index) {
		// Connect to the left
		if (col - 1 >= 0 && grid[row][col - 1]) {
			wquFind.union(index, index - 1);
		}

		// Connect to the right
		if (col + 1 < gridSize && grid[row][col + 1]) {
			wquFind.union(index, index + 1);
		}

		// Connect to the top
		if (row - 1 >= 0 && grid[row - 1][col]) {
			wquFind.union(index, index - gridSize);
		}

		// Connect to the bottom
		if (row + 1 < gridSize && grid[row + 1][col]) {
			wquFind.union(index, index + gridSize);
		}
	}

	// Method to open all sites with a given probability
	public void openAllSites(double probability, long seed) {
		StdRandom.setSeed(seed);

		for (int row = 0; row < gridSize; row++) {
			for (int col = 0; col < gridSize; col++) {
				if (StdRandom.uniform() < probability) {
					openSite(row, col);
				}
			}
		}
	}

	// Method to check if the system percolates
	public boolean percolationCheck() {
		return wquFind.find(virtualTop) == wquFind.find(virtualBottom);
	}

	// Method to display the grid with percolation result at the bottom
	public void displayGridWithResult(int problemNumber, boolean percolates) {
		double blockSize = 0.9 / gridSize;
		double zeroPt = 0.05 + (blockSize / 2), x = zeroPt, y = zeroPt;

		for (int i = gridSize - 1; i >= 0; i--) {
			x = zeroPt;
			for (int j = 0; j < gridSize; j++) {
				if (grid[i][j]) {
					StdDraw.setPenColor(Color.PINK);
					StdDraw.filledSquare(x, y, blockSize / 2);
					StdDraw.setPenColor(Color.BLACK);
					StdDraw.square(x, y, blockSize / 2);
				} else {
					StdDraw.filledSquare(x, y, blockSize / 2);
				}
				x += blockSize;
			}
			y += blockSize;
		}

		// Display problem number and percolation result at the bottom
		StdDraw.setPenColor(Color.BLACK);
		StdDraw.text(0.5, 0.02, "Problem " + problemNumber + ": Percolates - " + percolates);
		StdDraw.show();
	}

	// Test client for 10 percolation problems
	public static void main(String[] args) {
		int gridSize = 10;
		double probability = 0.63;

		Percolation percolation = new Percolation(gridSize);

		for (int i = 1; i <= 10; i++) {
			long seed = System.currentTimeMillis() + i; // Using different seeds for each problem
			percolation.openAllSites(probability, seed);
			boolean percolates = percolation.percolationCheck();

			percolation.displayGridWithResult(i, percolates);
			StdDraw.show(0); // Displaying without pause initially

			// Waiting for user input (space, enter, or mouse click) to proceed to the next problem
			while (true) {
				if (StdDraw.hasNextKeyTyped()) {
					char key = StdDraw.nextKeyTyped();
					if (key == KeyEvent.VK_SPACE || key == KeyEvent.VK_ENTER) {
						break; // Move to the next problem
					}
				} else if (StdDraw.isMousePressed() && StdDraw.mousePressed()) {
					break; // Move to the next problem on mouse click
				}
			}

			StdDraw.clear();
			percolation = new Percolation(gridSize); // Reset for the next problem
		}
	}
}
