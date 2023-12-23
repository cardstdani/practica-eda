public class CeldaAvanzada2 implements Celda {

    // Valores de estado
    static byte AISLANTE = 0; // Es un átomo aislante
    static byte CONDUCTOR = 1; // Es un átomo conductor, sin camino a placas
    static byte CAMINO_SUP = 2; // Conductor con camino a placa superior
    static byte CAMINO_INF = 3; // Conductor con camino a la placa inferior
    /**
     * Matriz que sustituye las matrices conductor y visitados, almacena
     * información sobre el estado del átomo: Si es aislante, si es conductor
     * sin camino a placas, si es conductor con camino a placa superior o si es
     * conductor con camino a placa inferior
     */
    byte[][] estado;

    // Desplazamiento de vecinos
    int[][] VECINOS = {{-1, -1}, {-1, 0}, {-1, 1}, {0, -1}, {0, 1}, {1, -1}, {1, 0}, {1, 1}};

    int n = -1;
    boolean roto;
    // Último átomo transmutado (para la conversión a string)
    int ui, uj;

    /**
     * Inicializa la celda
     *
     * @param n Tamaño de la celda (n x n átomos)
     */
    @Override
    public void Inicializar(int n) {
        if (this.n != n) {
            this.n = n;
            estado = new byte[n][n];
        }
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                estado[i][j] = AISLANTE;
            }
        }
        roto = false;
        ui = -1;
        uj = -1;
    }

    /**
     * Actualiza el estado de la celda tras la llegada de un rayo cósmico
     *
     * @param i La fila del átomo [0..n-1]
     * @param j La columna del átomo [0..n-1]
     */
    @Override
    public void RayoCosmico(int i, int j) {
        // Si el átomo ya ha sido transmutado no se hace nada
        if (estado[i][j] != AISLANTE) {
            return;
        }
        estado[i][j] = CONDUCTOR;
        ui = i;
        uj = j;
        // Recorrer vecinos conductores y comprobar si hay camino en alguno de ellos
        boolean hayCamSup = i == 0;
        boolean hayCamInf = i == n - 1;
        for (int[] d : VECINOS) {
            int vi = i + d[0], vj = j + d[1];
            // Coordenada correcta
            if (vi < 0 || vi >= n || vj < 0 || vj >= n) {
                continue;
            }
            // Átomo conductor
            if (estado[vi][vj] == AISLANTE) {
                continue;
            }
            if (estado[vi][vj] == CAMINO_SUP) {
                hayCamSup = true;
            }
            if (estado[vi][vj] == CAMINO_INF) {
                hayCamInf = true;
            }
        }
        if (hayCamSup && hayCamInf) { // Hay cortocircuito
            roto = true;
            // Deberiamos propagar el estado a los vecinos, pero para
            // el problema actual no es necesario
            return;
        }
        if (hayCamSup || hayCamInf) {
            // Es necesario propagar la información a los átomos
            // conductores vecinos
            PropagaEstado(i, j, hayCamSup ? CAMINO_SUP : CAMINO_INF);
        }
    }

    /**
     * Recorrido recursivo, propaga el estado entre sus vecinos conductores que
     * no tengan ese estado y no hayan sido visitados. Como se cambia el estado
     * no se necesita matriz de visitados
     *
     * @param i   Fila del átomo
     * @param j   Columna del átomo
     * @param est Valor de estado que se propagará
     */
    protected void PropagaEstado(int i, int j, byte est) {
        // Posición en límites
        if (i < 0 || j < 0 || i >= n || j >= n) {
            return;
        }
        // Átomo conductor
        if (estado[i][j] == AISLANTE) {
            return;
        }
        // Si ya tiene el estado nos salimos
        if (estado[i][j] == est) {
            return;
        }
        // Marcar al estado
        estado[i][j] = est;
        // Propagación a vecinos
        for (int[] d : VECINOS) {
            PropagaEstado(i + d[0], j + d[1], est);
        }
    }

    /**
     * Comprueba si existe cortocircuito
     *
     * @return <code>true</code> si existe cortocircuito
     */
    @Override
    public boolean Cortocircuito() {
        return roto;
    }

    /**
     * Representa el estado de la celda: Que átomos son aislantes (·) y cuales
     * conductores (X), indicando el último átomo que pasó a conductor como (*)
     *
     * @return Un String que representa el estado de la celda
     */
    @Override
    public String toString() {
        String res = "";
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                res += i == ui && j == uj ? "*"
                        : estado[i][j] == AISLANTE ? "·" : "X";
            }
            res += "\n";
        }
        return res;
    }
}