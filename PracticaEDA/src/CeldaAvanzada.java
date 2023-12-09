/**
 * Para optimizar la complejidad del algoritmo original y que el resultado de una simulación se pueda realizar en tiempo
 * O(n^2) o algo lo suficientemente próximo, es necesario reducir el tiempo de la función RayoCosmico() a O(1).
 * Para ello, este código implementa el algoritmo UnionFind con compresión de caminos y unión por rango. Así, cada vez que
 * se añade un elemento a la matriz, se une con los conjuntos de sus vecinos y se comprueba si hay cortocircuito con 2 nodos
 * extra que representan los bordes de arriba/abajo de la matriz. En caso de que haya cortocircuito, los bordes estarán en el
 * mismo conjunto.
 * Con estas optimizaciones, se alcanza un tiempo amortizado en RayoCosmico() de O(α(n)), que no es constante pero se
 * aproxima lo suficiente
 *
 * @author Daniel García Solla
 */
public class CeldaAvanzada implements Celda {
    private boolean[][] grid;
    private int[][] nei = new int[][]{{-1, -1}, {-1, 0}, {-1, 1}, {0, -1}, {0, 1}, {1, -1}, {1, 0},
            {1, 1}};
    private boolean cortocircuito;
    private int n, n2, root1, root2;
    private int cellIndex;

    // Disjoint Set
    private int[] parent;
    private int[] rank;

    /**
     * Encuentra el representante del conjunto del elemento dado como entrada.
     *
     * @param item Un elemento de la estructura.
     * @return La raíz del conjunto al que pertenece el elemento.
     */
    public int find(int item) {
        while (item != parent[item]) {
            parent[item] = parent[parent[item]];
            item = parent[item];
        }
        return item;
    }

    /**
     * Une dos conjuntos a los que pertenecen los elementos dados.
     *
     * @param item1 Primer elemento
     * @param item2 Segundo elemento
     */
    public void union(int item1, int item2) {
        root1 = find(item1);
        root2 = find(item2);

        if (root1 != root2) {
            if (rank[root1] == rank[root2]) {
                parent[root1] = root2;
                rank[root2]++;
            } else if (rank[root1] > rank[root2]) {
                parent[root2] = root1;
            } else {
                parent[root1] = root2;
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
        grid = new boolean[n][n];
        cortocircuito = false;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                grid[i][j] = false;
            }
        }

        int size = (n * n) + 2;
        parent = new int[size];
        rank = new int[size];
        for (int i = 0; i < size; i++) {
            parent[i] = i;
            rank[i] = 0;
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

            cellIndex = i * n + j;
            if (i == 0) {
                union(cellIndex, n2);
            } else if (i == n - 1) {
                union(cellIndex, n2 + 1);
            }

            int newI = 0, newJ = 0;
            for (int[] k : nei) {
                newI = k[0] + i;
                newJ = k[1] + j;
                if (newI >= 0 && newJ >= 0 && newI < grid.length && newJ < grid[0].length && grid[newI][newJ]) {
                    union(cellIndex, newI * n + newJ);
                }
            }
            cortocircuito = find(n2) == find(n2 + 1);
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

                if (j < grid[0].length - 1) {
                    out += " ";
                }
            }
            out += "\n";
        }
        return out;
    }
}