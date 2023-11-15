package es.uva.eda;

class DisjointSet {
	private int[] parent;
	private int[] rank;

	public DisjointSet(int size) {
		parent = new int[size];
		rank = new int[size];
		for (int i = 0; i < size; i++) {
			parent[i] = i;
			rank[i] = 0;
		}
	}

	public int find(int item) {
		if (parent[item] != item) {
			parent[item] = find(parent[item]);
		}
		return parent[item];
	}

	public void union(int item1, int item2) {
		int root1 = find(item1);
		int root2 = find(item2);
		if (root1 != root2) {
			if (rank[root1] < rank[root2]) {
				parent[root1] = root2;
			} else if (rank[root1] > rank[root2]) {
				parent[root2] = root1;
			} else {
				parent[root2] = root1;
				rank[root1]++;
			}
		}
	}
}

public class CeldaAvanzada implements Celda {
	private boolean[][] grid;
	private int[][] nei = new int[][] { { -1, 0 }, { 1, 0 }, { 1, -1 }, { 1, 1 }, { -1, -1 }, { -1, 1 }, { 0, -1 },
			{ 0, 1 } };
	private boolean cortocircuito;
	private DisjointSet ds;
	private int n;

	/**
	 * Inicializa la cuadrícula con un tamaño específico.
	 * 
	 * @param n El tamaño de la cuadrícula (n x n).
	 */
	@Override
	public void Inicializar(int n) {
		this.n = n;
		grid = new boolean[n][n];
		cortocircuito = false;
		ds = new DisjointSet((n * n) + 2);

		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				grid[i][j] = false;
			}
		}
	}

	private int getIndex(int i, int j) {
		return i * n + j;
	}

	/**
	 * Aplica un rayo cósmico en una posición específica de la cuadrícula. Si la
	 * celda en la posición (i, j) está desactivada, la activa y verifica si hay un
	 * cortocircuito.
	 * 
	 * @param i Índice de la fila.
	 * @param j Índice de la columna.
	 */
	@Override
	public void RayoCosmico(int i, int j) {
		if (!grid[i][j]) {
			grid[i][j] = true;
			int cellIndex = getIndex(i, j);

			if (i == 0) {
				ds.union(cellIndex, n * n); // Connect to virtual top node
			} else if (i == n - 1) {
				ds.union(cellIndex, n * n + 1); // Connect to virtual bottom node
			}

			for (int[] k : nei) {
				int newI = i + k[0];
				int newJ = j + k[1];
				if (newI >= 0 && newJ >= 0 && newI < n && newJ < n && grid[newI][newJ]) {
					ds.union(cellIndex, getIndex(newI, newJ));
				}
			}

			cortocircuito = ds.find(n * n) == ds.find(n * n + 1);
		}
	}

	/**
	 * Verifica si hay un cortocircuito en la cuadrícula.
	 * 
	 * @return true si hay un cortocircuito, false en caso contrario.
	 */
	@Override
	public boolean Cortocircuito() {
		return cortocircuito;
	}

	/**
	 * Devuelve un string con la cuadrícula.
	 * 
	 * @return Una cadena de texto representando el estado actual de la cuadrícula.
	 */
	@Override
	public String toString() {
		String out = "";

		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[0].length; j++) {
				out += grid[i][j] ? "X" : ".";
				out += (j < grid[0].length - 1) ? " " : "";
			}
			out += "\n";
		}

		return out;
	}
}