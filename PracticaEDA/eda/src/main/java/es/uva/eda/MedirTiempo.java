package es.uva.eda;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class MedirTiempo {
	public static void main(String[] args) {
		Random rnd = new Random();
		Celda celda = new CeldaSimple();
		ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()); // Create a
																												// thread
																												// pool
		for (int n = 2; n < 1000; n += 5) {
			List<Future<Void>> futures = new ArrayList<>(); // List to hold future results
			for (int k = 0; k < n; k++) {
				final int finalN = n;

				// Submit tasks to the executor
				Future<Void> future = executor.submit(() -> {
					celda.Inicializar(finalN);

					long t = System.nanoTime();
					while (!celda.Cortocircuito()) { // n*n
						celda.RayoCosmico(rnd.nextInt(finalN), rnd.nextInt(finalN));
						// 2*n*n
					}
					long t2 = System.nanoTime();

					try (BufferedWriter bw = new BufferedWriter(new FileWriter("t_values.txt", true))) {
						bw.write(finalN + " " + (t2 - t));
						bw.newLine();
						System.out.println(finalN + " " + (t2 - t));
					} catch (IOException e) {
						e.printStackTrace();
					}
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
