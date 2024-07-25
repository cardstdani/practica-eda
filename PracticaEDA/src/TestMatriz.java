import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class TestMatriz {
    public static void main(String[] args) throws InterruptedException, ExecutionException, IOException {
        Random rnd;
        long t = 0;
        boolean[][] visited;
        BufferedWriter bw = new BufferedWriter(new FileWriter("t_values_matrix3.txt", true));
        for (int n = 2; n < 1000; n += 2) {
            for (int k = 0; k < n; k++) {
                rnd = new Random();
                visited = new boolean[n][n];
                for (int i = 0; i < n; i++) {
                    for (int j = 0; j < n; j++) {
                        //visited[i][j] = (rnd.nextInt(2) != 0);
                        visited[i][j] = false;
                    }
                }

                t = System.nanoTime();
                /*for (int i = 0; i < n; i++) {
                    for (int j = 0; j < n; j++) {
                        visited[i][j] = false;
                    }
                }*/

                visited = new boolean[n][n];
                t = System.nanoTime() - t;
                bw.write(n + " " + t + "\n");
                bw.flush();
                System.out.println(n + " " + t);
            }
            System.gc();
        }
        bw.close();
    }
}
