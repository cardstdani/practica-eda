import java.util.Random;

public class EDA2324Test {

    static int SEMILLA = 123456789;

    public static void Test(int n, int m, int r) {
        Random rnd = new Random(SEMILLA);
        CeldaSimple3D2 celda = new CeldaSimple3D2();
        int num_rayos = 0;
        // Simulación
        for (int k = 0; k < m; k++) {
            celda.Inicializar(n, n, 1);
            num_rayos = 0;
            while (!celda.Cortocircuito()) {
                // Elegir átomo al azar y transmutarlo
                int i = rnd.nextInt(n), j = rnd.nextInt(n);
                celda.RayoCosmico(i, j, 0);
                num_rayos++;
            }
        }
        if (num_rayos == r) {
            System.out.printf("Ha pasado el test %d-%d\n", n, m);
        } else {
            if (r > 1 && num_rayos == 0) {
                System.out.println("ERROR: NO INICIALIZAS CORRECTAMENTE!!");
            } else {
                System.out.println("NO FUNCIONA!!!!!");
            }
        }
    }

    public static void main(String[] args) {
        Test(250, 1, 32512);
        Test(500, 1, 132837);
        Test(1000, 1, 515474);
        Test(2000, 1, 2091232);
        Test(250, 3, 32308);
        Test(500, 3, 130051);
        Test(1000, 3, 526449);
        Test(2000, 3, 2094060);

        /*
         * CeldaSimpleReferencia celda = new CeldaSimpleReferencia();
         * celda.Inicializar(10); System.out.println(celda.HayCamino(9,4, 0));
         * System.out.println(celda);
         */
    }

}