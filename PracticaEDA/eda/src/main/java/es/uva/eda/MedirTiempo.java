package es.uva.eda;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class MedirTiempo {
	public static void main(String[] args) throws InterruptedException, ExecutionException, IOException {
		Random rnd;
		Celda celda = new CeldaAvanzada();
		long t = 0;
		BufferedWriter bw = new BufferedWriter(new FileWriter("t_values.txt", true));
		for (int n = 2; n < 800; n += 4) {
			for (int k = 0; k < 150; k++) {
				rnd = new Random();
				celda.Inicializar(n);

				t = System.nanoTime();
				while (!celda.Cortocircuito()) {
					celda.RayoCosmico(rnd.nextInt(n), rnd.nextInt(n));
				}
				t = System.nanoTime() - t;
				bw.write(n + " " + t + "\n");
				System.out.println(n + " " + t);
			}
			System.gc();
		}
		bw.close();
	}
}

class PruebaMultiThreading {
	public static void main(String[] args) throws InterruptedException, ExecutionException, IOException {
		ExecutorService executor = Executors.newFixedThreadPool(24);
		ConcurrentLinkedQueue<String> results = new ConcurrentLinkedQueue<>(); // A thread-safe data structure to store
																				// results

		for (int n = 2; n < 300; n += 4) {
			List<Callable<Void>> tasks = new ArrayList<>(); // List to hold tasks
			int finalN = n;
			for (int k = 0; k < n; k++) {
				tasks.add(() -> {
					Celda celda = new CeldaSimple();
					celda.Inicializar(finalN);

					long t = System.nanoTime();
					while (!celda.Cortocircuito()) {
						celda.RayoCosmico(ThreadLocalRandom.current().nextInt(finalN),
								ThreadLocalRandom.current().nextInt(finalN));
					}
					long t2 = System.nanoTime();

					String result = finalN + " " + (t2 - t);
					results.add(result);
					System.out.println(result);
					return null;
				});
			}

			executor.invokeAll(tasks); // This will wait for all tasks to complete
		}

		// Write to the file once all tasks are done
		try (BufferedWriter bw = new BufferedWriter(new FileWriter("t_values.txt", true))) {
			for (String result : results) {
				bw.write(result + "\n");
			}
		}

		executor.shutdown();
	}
}