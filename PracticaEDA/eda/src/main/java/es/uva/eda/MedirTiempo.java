package es.uva.eda;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class MedirTiempo {
	public static void main(String[] args) {
		Random rnd = new Random();
		ExecutorService executor = Executors.newCachedThreadPool();

		for (int n = 2; n < 1000; n += 5) {
			List<Future<Void>> futures = new ArrayList<>(); // List to hold future results
			for (int k = 0; k < (n/2); k++) {
				final int finalN = n;

				// Submit tasks to the executor
				Future<Void> future = executor.submit(() -> {
					Celda celda = new CeldaSimple();
					celda.Inicializar(finalN);

					long t = System.nanoTime();
					// C(n^2, f(x)) numero total de combinaciones de f(x) atomos cambiados
					while (!celda.Cortocircuito()) {
						celda.RayoCosmico(rnd.nextInt(finalN), rnd.nextInt(finalN)); // Peor caso O(n^2)
					}
					long t2 = System.nanoTime();

					try (BufferedWriter bw = new BufferedWriter(new FileWriter("t_values.txt", true))) {
						bw.write(finalN + " " + (t2 - t));
						bw.newLine();
					} catch (IOException e) {
						e.printStackTrace();
					}

					System.out.println(finalN + " " + (t2 - t));
					return null;
				});
				futures.add(future);
			}

			for (Future<Void> future : futures) {
				try {
					future.get();
				} catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
				}
			}
		}

		executor.shutdown();

	}
}
