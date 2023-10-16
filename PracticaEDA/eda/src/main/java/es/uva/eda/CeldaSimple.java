package es.uva.eda;

public class CeldaSimple implements Celda {
	private boolean[][] grid;
	private boolean[][] visited;
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
		//f(x)=n^2 (1-(1 - 1/n^2)^x) Numero estimado de atomos cambiados en iteracion x
		if (!grid[i][j]) { //P(x)=(1-(1/n^2))^x probabilidad de entrar en esta condición = n^2-f(x) / n^2
			grid[i][j] = true;

			for (int a = 0; a < grid.length; a++) { //O(n^2)
				for (int b = 0; b < grid[0].length; b++) {
					visited[a][b] = false;
				}
			}
			cortocircuito = helper(i, j, 0) & helper(i, j, 1); //O(n^2) (peor caso)
		}		
	}

	//Complejidad T(i, j, x)=(1-P(x)) (T(i, j-1)+T(i, j+1)+T(i-1, j)+T(i-1, j-1)+T(i-1, j+1)) + O(1)
	private boolean helper(int i, int j, int up) {
		visited[i][j] = true;
		int[][] nei;
		if (up == 0) {
			if (i == (grid.length - 1)) {
				return true;
			}
			nei = new int[][] { { i, Math.max(0, j - 1) }, { i, Math.min(grid[0].length - 1, j + 1) },
					{ Math.min(grid.length - 1, i + 1), j },
					{ Math.min(grid.length - 1, i + 1), Math.min(grid[0].length - 1, j + 1) },
					{ Math.min(grid.length - 1, i + 1), Math.max(0, j - 1) } };
		} else {
			if (i == 0) {
				return true;
			}
			nei = new int[][] { { i, Math.max(0, j - 1) }, { i, Math.min(grid[0].length - 1, j + 1) },
					{ Math.max(0, i - 1), j }, { Math.max(0, i - 1), Math.min(grid[0].length - 1, j + 1) },
					{ Math.max(0, i - 1), Math.max(0, j - 1) } };
		}

		for (int[] k : nei) {
			if (!visited[k[0]][k[1]] & grid[k[0]][k[1]]) {
				if (helper(k[0], k[1], up)) {
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