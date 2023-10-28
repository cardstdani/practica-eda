package es.uva.eda;

public class CeldaSimple implements Celda {
	private boolean[][] grid;
	private boolean[][] visited;
	private int[][] neiUp = new int[][] { { -1, 0 }, { -1, -1 }, { -1, 1 }, { 0, -1 }, { 0, 1 } };
	private int[][] neiDown = new int[][] { { 1, 0 }, { 1, -1 }, { 1, 1 }, { 0, -1 }, { 0, 1 } };
	private boolean cortocircuito;

	@Override
	public void Inicializar(int n) {
		grid = new boolean[n][n];
		visited = new boolean[n][n];
		cortocircuito = false;

		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				grid[i][j] = false;
			}
		}
	}

	@Override
	public void RayoCosmico(int i, int j) {
		// B(x)=n^2 (1-(1 - 1/n^2)^x) Numero estimado de atomos cambiados en iteracion x
		if (!grid[i][j]) { // P(x)=(1-(1/n^2))^x probabilidad de entrar en esta condición
			grid[i][j] = true;

			for (int a = 0; a < grid.length; a++) { // O(n^2)
				for (int b = 0; b < grid[0].length; b++) {
					visited[a][b] = false;
				}
			}
			cortocircuito = helper(i, j, 0);
			for (int b = 0; b < grid[0].length; b++) {
				visited[i][b] = false;
			}
			cortocircuito = cortocircuito && helper(i, j, 1);
		}
	}

	// Complejidad T(i, j, x)=(1-P(x)) (T(i, j-1)+T(i, j+1)+T(i-1, j)+T(i-1,
	// j-1)+T(i-1, j+1)) + O(1)
	private boolean helper(int i, int j, int up) {
		visited[i][j] = true;
		if (up == 0) {
			if (i == (grid.length - 1)) {
				return true;
			}

			int newI = 0, newJ = 0;
			for (int k = 0; k < neiDown.length; k++) {
				newI = neiDown[k][0] + i;
				newJ = neiDown[k][1] + j;
				if (newI >= 0 && newJ >= 0 && newI < grid.length && newJ < grid[0].length && grid[newI][newJ]
						&& !visited[newI][newJ] && helper(newI, newJ, up)) {
					return true;
				}
			}

		} else {
			if (i == 0) {
				return true;
			}

			int newI = 0, newJ = 0;
			for (int k = 0; k < neiUp.length; k++) {
				newI = neiUp[k][0] + i;
				newJ = neiUp[k][1] + j;
				if (newI >= 0 && newJ >= 0 && newI < grid.length && newJ < grid[0].length && grid[newI][newJ]
						&& !visited[newI][newJ] && helper(newI, newJ, up)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean Cortocircuito() {
		return cortocircuito;
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