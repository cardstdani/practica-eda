package es.uva.eda;

import java.util.Arrays;
import com.aparapi.*;

public class CeldaSimple implements Celda {
	private boolean[][] grid;
	private boolean[][] visited;
	private int iterations = 0;

	@Override
	public void Inicializar(int n) {
		grid = new boolean[n][n];
		visited = new boolean[n][n];

		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				grid[i][j] = false;
			}
		}
	}

	@Override
	public void RayoCosmico(int i, int j) {
		if (!grid[i][j]) {
			grid[i][j] = true;
		}
		iterations++;
	}

	private boolean helper(int i, int j) {
		if (i == (grid.length - 1)) {
			return true;
		}
		visited[i][j] = true;
		int[][] nei = new int[][] { { Math.max(0, i - 1), j }, { Math.min(grid.length - 1, i + 1), j },
				{ i, Math.max(0, j - 1) }, { i, Math.min(grid[0].length - 1, j + 1) },
				{ Math.max(0, i - 1), Math.max(0, j - 1) },
				{ Math.min(grid.length - 1, i + 1), Math.min(grid[0].length - 1, j + 1) },
				{ Math.max(0, i - 1), Math.min(grid[0].length - 1, j + 1) },
				{ Math.min(grid.length - 1, i + 1), Math.max(0, j - 1) } };

		for (int[] k : nei) {
			if (!visited[k[0]][k[1]] & grid[k[0]][k[1]]) {
				if (helper(k[0], k[1])) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean Cortocircuito() {
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[0].length; j++) {
				visited[i][j] = false;
			}
		}

		for (int i = 0; i < grid[0].length; i++) {
			if (grid[0][i]) {
				if (helper(0, i)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public String toString() {
		String out = "";

		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[0].length; j++) {
				out += grid[i][j] ? "X" : ".";

				if (j < grid[0].length - 1) {
					out += " ";
				}
			}
			out += "\n";
		}

		return out;
	}
}