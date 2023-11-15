package es.uva.eda;

public class CeldaSimpleReferencia implements Celda {

	// Desplazamiento de vecinos
	int[][] VECINOS = { { -1, -1 }, { -1, 0 }, { -1, 1 }, { 0, -1 }, { 0, 1 }, { 1, -1 }, { 1, 0 }, { 1, 1 } };

	int n = -1; // Dimensiones de la celda (n x n)
	boolean roto; // Estado tras la última llamada a RayoCosmico
	boolean[][] conductor;
	boolean[][] visitado;
	// Último átomo transmutado (para la conversión a string)
	int ui, uj;

	@Override
	public void Inicializar(int n) {
		// Solo se crean nuevos arrays si el tamaño es distinto
		if (this.n != n) {
			this.n = n;
			conductor = new boolean[n][n];
			visitado = new boolean[n][n];
		}
		// Inicialización de arrays
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				conductor[i][j] = false;
			}
		}
		BorrarVisitados();
		roto = false; // Cuidado con olvidarse esto!
		ui = -1;
		uj = -1;
	}

	@Override
	public void RayoCosmico(int i, int j) {
		// Si el átomo ya ha sido transmutado no se hace nada
		if (conductor[i][j]) {
			return;
		}
		ui = i;
		uj = j;
		conductor[i][j] = true;
		// Comprobación de conexión con placa superior
		BorrarVisitados();
		if (!HayCamino(i, j, 0)) {
			return;
		}
		// Comprobación de conexión con placa inferior
		BorrarVisitados();
		if (!HayCamino(i, j, n - 1)) {
			return;
		}
		roto = true;
	}

	@Override
	public boolean Cortocircuito() {
		return roto;
	}

	protected void BorrarVisitados() {
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				visitado[i][j] = false;
			}
		}
	}

	protected boolean HayCamino(int i, int j, int i_lim) {
		// Posición en límites
		if (i < 0 || i >= n || j < 0 || j >= n) {
			return false;
		}
		// No visitada
		if (visitado[i][j]) {
			return false;
		}
		// Marcar posición como visitada
		visitado[i][j] = true;
		// Átomo conductor
		if (!conductor[i][j]) {
			return false;
		}
		// Si está en contacto con la placa, hemos terminado
		if (i == i_lim) {
			return true;
		}
		// Comprobación de existencia de camino desde vecino
		for (int[] desp : VECINOS) {
			if (HayCamino(i + desp[0], j + desp[1], i_lim)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		String res = "";
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				res += i == ui && j == uj ? "*" : conductor[i][j] ? "X" : ".";
			}
			res += "\n";
		}
		return res;
	}
}