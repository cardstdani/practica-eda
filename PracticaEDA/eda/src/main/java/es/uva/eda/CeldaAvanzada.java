package es.uva.eda;

import java.util.HashMap;
import java.util.Map;

class DisjointSet<Item> {
	private Map<Item, Item> map;
	private Map<Item, Integer> weightMap;
	private int setsSize;

	public DisjointSet() {
		map = new HashMap<>();
		weightMap = new HashMap<>();
		setsSize = 0;
	}

	public DisjointSet(Item[] items) {
		map = new HashMap<>();
		weightMap = new HashMap<>();
		for (Item item : items) {
			if (map.containsKey(item))
				throw new IllegalArgumentException("The items array contains at least two same items.");
			map.put(item, item);
			weightMap.put(item, 1);
		}
		setsSize = items.length;
	}

	public boolean makeSet(Item item) {
		if (contains(item))
			return false;
		map.put(item, item);
		weightMap.put(item, 1);
		setsSize++;
		return true;
	}

	public boolean isEmpty() {
		if (map.isEmpty())
			return true;
		return false;
	}

	public int itemsSize() {
		return map.size();
	}

	public int itemsSetSize() {
		return setsSize;
	}

	public void union(Item item1, Item item2) {
		Item rootItem1 = find(item1);
		Item rootItem2 = find(item2);
		if (rootItem1 != rootItem2) {
			int weightRootItem1 = weightMap.get(rootItem1);
			int weightRootItem2 = weightMap.get(rootItem2);
			if (weightRootItem1 >= weightRootItem2) {
				map.put(rootItem2, rootItem1);
				weightMap.put(rootItem1, weightRootItem1 + weightRootItem2);
			} else {
				map.put(rootItem1, rootItem2);
				weightMap.put(rootItem2, weightRootItem1 + weightRootItem2);
			}
			setsSize--;
		}
	}

	public Item find(Item item) {
		if (!contains(item))
			throw new IllegalArgumentException("Illegal Argument.");
		Item root = item;
		while (!root.equals(map.get(root)))
			root = map.get(root);
		return root;
	}

	public boolean contains(Item item) {
		return map.containsKey(item);
	}

}

class Tuple<X, Y> {
	public final X x;
	public final Y y;

	public Tuple(X x, Y y) {
		this.x = x;
		this.y = y;
	}
}

/**
 * Para más detalles, visitar el github:
 * https://github.com/cardstdani/practica-eda
 * 
 * @author Daniel García Solla
 */
public class CeldaAvanzada implements Celda {
	private boolean[][] grid;
	private int[][] nei = new int[][] { { -1, 0 }, { 1, 0 }, { 1, -1 }, { 1, 1 }, { -1, -1 }, { -1, 1 }, { 0, -1 },
			{ 0, 1 } };
	private boolean cortocircuito;
	private DisjointSet ds;

	/**
	 * Inicializa la cuadrícula con un tamaño específico.
	 * 
	 * @param n El tamaño de la cuadrícula (n x n).
	 */
	@Override
	public void Inicializar(int n) {
		grid = new boolean[n][n];
		cortocircuito = false;
		ds = new DisjointSet();
		
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				grid[i][j] = false;
			}
		}
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
			ds.makeSet(new Tuple(i, j));

			int newI = 0, newJ = 0;
			
			for (int k = 0; k < nei.length; k++) {
				newI = nei[k][0] + i;
				newJ = nei[k][1] + j;
				if (newI >= 0 && newJ >= 0 && newI < grid.length && newJ < grid[0].length && grid[newI][newJ]) {
					Tuple t = new Tuple(newI, newJ);
					ds.find(t);
					ds.union(new Tuple(i, j), new Tuple(newI, newJ));
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