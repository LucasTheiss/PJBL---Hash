package gerador;

import registro.Registro;
import java.util.Random;

public class GeradorDadosAleatorios {


     // gera um array de Registros com códigos de 9 dígitos únicos
     // utiliza um array de booleanos para garantir a unicidade de codigos
    public static Registro[] generate(int size, long seed) {
        System.out.printf("Gerando %d registros únicos com a seed %d...%n", size, seed);
        Random rand = new Random(seed);
        Registro[] data = new Registro[size];
        int count = 0;

        // o intervalo de códigos é de 100.000.000 a 999.999.999 (900.000.000 de possibilidades)
        boolean[] codigosUsados = new boolean[900_000_000];

        while (count < size) {
            int codigo = 100_000_000 + rand.nextInt(900_000_000);
            int index = codigo - 100_000_000;

            if (!codigosUsados[index]) {
                codigosUsados[index] = true;
                data[count] = new Registro(codigo);
                count++;
            }
        }
        return data;
    }
}