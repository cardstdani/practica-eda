/**
 * Para más detalles, visitar el github:
 * https://github.com/cardstdani/practica-eda
 *
 * @author Daniel García Solla
 */
public class CeldaSimple3D2 {
    private boolean[][][] grid;
    private boolean[][][] visited;
    private int[][] visited2;
    private int visitedIndex;
    private int[][] nei = new int[][]{{-1, -1, -1}, {-1, -1, 0}, {-1, -1, 1}, {-1, 0, -1}, {-1, 0, 0}, {-1, 0, 1}, {-1, 1, -1}, {-1, 1, 0}, {-1, 1, 1},
            {0, -1, -1}, {0, -1, 0}, {0, -1, 1}, {0, 0, -1}, {0, 0, 1}, {0, 1, -1}, {0, 1, 0}, {0, 1, 1},
            {1, -1, -1}, {1, -1, 0}, {1, -1, 1}, {1, 0, -1}, {1, 0, 0}, {1, 0, 1}, {1, 1, -1}, {1, 1, 0}, {1, 1, 1}};
    private boolean cortocircuito;

    /**
     * Inicializa la cuadrícula con un tamaño específico.
     *
     * @param n El tamaño de la cuadrícula en la primera dimensión (n x m x p).
     * @param m El tamaño de la cuadrícula en la segunda dimensión.
     * @param p El tamaño de la cuadrícula en la tercera dimensión.
     */
    public void Inicializar(int n, int m, int p) {
        grid = new boolean[n][m][p];
        visited = new boolean[n][m][p];
        visited2 = new int[n * m * p][3];
        cortocircuito = false;

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                for (int k = 0; k < p; k++) {
                    grid[i][j][k] = false;
                }
            }
        }
        for (int i = 0; i < n * m * p; i++) {
            visited2[i][0] = -1;
            visited2[i][1] = -1;
            visited2[i][2] = -1;
        }
        visitedIndex = 0;
    }

    /**
     * Aplica un rayo cósmico en una posición específica de la cuadrícula. Si la
     * celda en la posición (i, j, k) está desactivada, la activa y verifica si hay un
     * cortocircuito.
     *
     * @param i Índice de la primera dimensión.
     * @param j Índice de la segunda dimensión.
     * @param k Índice de la tercera dimensión.
     */
    public void RayoCosmico(int i, int j, int k) {
        if (!grid[i][j][k]) {
            grid[i][j][k] = true;

            for (int a = 0; a < visitedIndex; a++) {
                visited[visited2[a][0]][visited2[a][1]][visited2[a][2]] = false;
            }
            visitedIndex = 0;
            cortocircuito = helper(i, j, k, 0);
            for (int a = 0; a < visitedIndex; a++) {
                visited[visited2[a][0]][visited2[a][1]][visited2[a][2]] = false;
            }
            visitedIndex = 0;
            cortocircuito = cortocircuito && helper(i, j, k, grid.length - 1);
        }
    }

    /**
     * Método auxiliar para verificar si hay un cortocircuito desde una celda
     * específica. Utiliza búsqueda en profundidad para verificar si hay un camino
     * desde la celda (i, j, k) hasta el borde opuesto.
     *
     * @param i  Índice de la primera dimensión.
     * @param j  Índice de la segunda dimensión.
     * @param k  Índice de la tercera dimensión.
     * @param up Dirección de la búsqueda (0 para abajo, 1 para arriba).
     * @return true si hay un cortocircuito, false en caso contrario.
     */
    private boolean helper(int i, int j, int k, int up) {
        if (i == up) {
            return true;
        }
        visited[i][j][k] = true;
        visited2[visitedIndex][0] = i;
        visited2[visitedIndex][1] = j;
        visited2[visitedIndex][2] = k;
        visitedIndex++;

        int newI = 0, newJ = 0, newK = 0;
        for (int x = 0; x < nei.length; x++) {
            newI = nei[x][0] + i;
            newJ = nei[x][1] + j;
            newK = nei[x][2] + k;
            if (newI >= 0 && newJ >= 0 && newK >= 0 && newI < grid.length && newJ < grid[0].length
                    && newK < grid[0][0].length && grid[newI][newJ][newK] && !visited[newI][newJ][newK]
                    && helper(newI, newJ, newK, up)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Verifica si hay un cortocircuito en la cuadrícula.
     *
     * @return true si hay un cortocircuito, false en caso contrario.
     */
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
                out += grid[i][j][j] ? "X" : ".";

                if (j < grid[0].length - 1) {
                    out += " ";
                }
            }
            out += "\n";
        }

        return out;
    }
}