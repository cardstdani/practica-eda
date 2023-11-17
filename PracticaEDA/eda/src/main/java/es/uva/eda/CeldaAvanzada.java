package es.uva.eda;

public class CeldaAvanzada implements Celda {
	private boolean[] grid;
	private int[] nei;
	private boolean cortocircuito;
	private int n, n2;
	private int cellIndex;

	// Disjoint Set
	private int[] parent;
	private int[] rank;

	public int find(int item) {
		while (item != parent[item]) {
			parent[item] = parent[parent[item]];
			item = parent[item];
		}
		return item;
	}

	public void union(int item1, int item2) {
		int root1 = find(item1);
		int root2 = find(item2);
		// System.out.println(root1 + " " + root2);
		if (root1 != root2) {
			if (rank[root1] < rank[root2]) {
				parent[root1] = root2;
			} else if (rank[root1] > rank[root2]) {
				parent[root2] = root1;
			} else {
				parent[root1] = root2;
				rank[root2]++;
			}
		}
	}

	/**
	 * Inicializa la cuadrícula con un tamaño específico.
	 * 
	 * @param n El tamaño de la cuadrícula (n x n).
	 */
	@Override
	public void Inicializar(int n) {
		this.n = n;
		this.n2 = n * n;
		grid = new boolean[n * n];
		cortocircuito = false;
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				grid[i * n + j] = false;
			}
		}

		int size = (n * n) + 2;
		parent = new int[size];
		rank = new int[size];
		for (int i = 0; i < size; i++) {
			parent[i] = i;
			rank[i] = 0;
		}
		nei = new int[] { -n, n, n - 1, n + 1, -n - 1, -n + 1, -1, 1 };
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
		cellIndex = i * n + j;
		if (!grid[cellIndex]) {
			grid[cellIndex] = true;

			if (i == 0) {
				union(cellIndex, n2); // Connect to virtual top node
			} else if (i == n - 1) {
				union(cellIndex, n2 + 1); // Connect to virtual bottom node
			}

			for (int k : nei) {
				if (cellIndex + k >= 0 && cellIndex + k < n2 && grid[cellIndex + k]) {
					union(cellIndex, cellIndex + k);
				}
			}
		}
	}

	/**
	 * Verifica si hay un cortocircuito en la cuadrícula.
	 * 
	 * @return true si hay un cortocircuito, false en caso contrario.
	 */
	@Override
	public boolean Cortocircuito() {
		return find(n2) == find(n2 + 1);
	}

	/**
	 * Devuelve un string con la cuadrícula.
	 * 
	 * @return Una cadena de texto representando el estado actual de la cuadrícula.
	 */
	@Override
	public String toString() {
		String out = "";

		/*
		 * for (int i = 0; i < grid.length; i++) { for (int j = 0; j < grid[0].length;
		 * j++) { out += grid[i][j] ? "X" : "."; out += (j < grid[0].length - 1) ? " " :
		 * ""; } out += "\n"; }
		 */

		return out;
	}
}