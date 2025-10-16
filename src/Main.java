import registro.Registro;
import estruturas.EncadeamentoHashTable;
import estruturas.SondagemLinearHashTable;
import estruturas.SondagemQuadraticaHashTable;
import gerador.GeradorDadosAleatorios;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;

public class Main {
    // definicao dos tamanhos escolhidos para as tabelas e para a quantidade de dados
    private static final int[] TABLE_SIZES = { 100_000, 1_000_000, 10_000_000 };
    private static final int[] DATA_SIZES = { 100_000, 1_000_000, 10_000_000 };
    // seed usada na geracao dos dados aleatorios
    private static final long SEED = 42L;

    public static void main(String[] args) {

        // stringBuilder para montar o relatório CSV e usar ele para formar os gráficos
        StringBuilder csvReport = new StringBuilder();
        csvReport.append("TamanhoTabela,TamanhoDados,FatorCarga,Estrategia,TempoInsercao(ms),Colisoes,TempoBusca(ms),Metrica1_Nome,Metrica1_Valor,Metrica2_Nome,Metrica2_Valor,Metrica3_Nome,Metrica3_Valor\n");

        for (int tableSize : TABLE_SIZES) {
            for (int dataSize : DATA_SIZES) {

                System.out.println("========================================================================");

                System.out.println("\nGerando conjunto de dados...");
                Registro[] data = GeradorDadosAleatorios.generate(dataSize, SEED);
                System.out.println("-> Geração de dados concluída.");

                // ENCADEAMENTO
                System.out.println("\nExecutando: Hash Table de Encadeamento ---");
                EncadeamentoHashTable chainingTable = new EncadeamentoHashTable(tableSize);

                // inserir
                long startTime = System.nanoTime();
                for (Registro registro : data) {
                    chainingTable.insert(registro);
                }
                long insertionTimeChaining = (System.nanoTime() - startTime) / 1_000_000;

                // buscar
                startTime = System.nanoTime();
                for (Registro registro : data) {
                    chainingTable.search(registro.codigo);
                }
                long searchTimeChaining = (System.nanoTime() - startTime) / 1_000_000;

                // resultados de dados internos
                long collisionsChaining = chainingTable.getCollisionCount();
                double[] metricsChaining = chainingTable.getAnalise();
                System.out.printf("-> Resultados: Tempo Inserção = %d ms | Colisões = %d | Tempo Busca = %d ms\n",
                        insertionTimeChaining, collisionsChaining, searchTimeChaining);

                // adiciona ao arquivo CSV
                double loadFactor = (double) dataSize / tableSize;
                csvReport.append(String.format(Locale.US, "%d,%d,%.2f,%s,%d,%d,%d,%s,%.0f,%s,%.0f,%s,%.0f\n",
                        tableSize, dataSize, loadFactor, "Encadeamento Separado",
                        insertionTimeChaining, collisionsChaining, searchTimeChaining,
                        "maior_lista", metricsChaining[0], "2a_maior_lista", metricsChaining[1], "3a_maior_lista", metricsChaining[2]));

                // sondagem só funcionam se fator de carga for menor q 1
                if (dataSize >= tableSize) {
                    System.out.println("dataSize " +dataSize + " > tableSize " + tableSize);
                    continue;
                }

                // SONDAGEM LINEAR
                System.out.println("\n--- Executando: Hash Table de Sondagem Linear ---");
                SondagemLinearHashTable linearTable = new SondagemLinearHashTable(tableSize);

                // inserir
                startTime = System.nanoTime();
                for (Registro registro : data) {
                    linearTable.insert(registro);
                }
                long insertionTimeLinear = (System.nanoTime() - startTime) / 1_000_000;

                // buscar
                startTime = System.nanoTime();
                for (Registro registro : data) {
                    linearTable.search(registro.codigo);
                }
                long searchTimeLinear = (System.nanoTime() - startTime) / 1_000_000;

                // resultados de dados internos
                long collisionsLinear = linearTable.getCollisionCount();
                double[] metricsLinear = linearTable.getAnalise();
                System.out.printf("-> Resultados: Tempo Inserção = %d ms | Colisões = %d | Tempo Busca = %d ms\n",
                        insertionTimeLinear, collisionsLinear, searchTimeLinear);

                // adiciona ao arquivo CSV
                csvReport.append(String.format(Locale.US, "%d,%d,%.2f,%s,%d,%d,%d,%s,%.0f,%s,%.0f,%s,%.2f\n",
                        tableSize, dataSize, loadFactor, "Sondagem Linear",
                        insertionTimeLinear, collisionsLinear, searchTimeLinear,
                        "menor_gap", metricsLinear[0], "maior_gap", metricsLinear[1], "media_gap", metricsLinear[2]));

                //SONDAGEM QUADRATICA
                System.out.println("\n--- Executando: Hash Table de Sondagem Quadrática ---");
                SondagemQuadraticaHashTable quadraticTable = new SondagemQuadraticaHashTable(tableSize);

                // inserir
                startTime = System.nanoTime();
                for (Registro registro : data) {
                    quadraticTable.insert(registro);
                }
                long insertionTimeQuadratic = (System.nanoTime() - startTime) / 1_000_000;

                // buscar
                startTime = System.nanoTime();
                for (Registro registro : data) {
                    quadraticTable.search(registro.codigo);
                }
                long searchTimeQuadratic = (System.nanoTime() - startTime) / 1_000_000;

                // resultados de dados internos
                long collisionsQuadratic = quadraticTable.getCollisionCount();
                double[] metricsQuadratic = quadraticTable.getAnalise();
                System.out.printf("-> Resultados: Tempo Inserção = %d ms | Colisões = %d | Tempo Busca = %d ms\n",
                        insertionTimeQuadratic, collisionsQuadratic, searchTimeQuadratic);

                // adiciona ao arquivo CSV
                csvReport.append(String.format(Locale.US, "%d,%d,%.2f,%s,%d,%d,%d,%s,%.0f,%s,%.0f,%s,%.2f\n",
                        tableSize, dataSize, loadFactor, "Sondagem Quadrática",
                        insertionTimeQuadratic, collisionsQuadratic, searchTimeQuadratic,
                        "menor_gap", metricsQuadratic[0], "maior_gap", metricsQuadratic[1], "media_gap", metricsQuadratic[2]));

                System.out.println("\nFIM DA RODADA.\n");
            }
        }

        // salva de fato o relatório CSV completo.
        saveReport(csvReport.toString());
    }

    // funcao para salvar os dados no arquivo CSV
    private static void saveReport(String report) {
        // o uso de try é obrigatório no uso de FileWriter
        try (FileWriter writer = new FileWriter("results/performance_report.csv")) {
            writer.write(report);
            System.out.println("========================================================================");
            System.out.println("== salvo na pasta 'results/'. ==\n");

        } catch (IOException e) {
            System.err.println("erro: " + e.getMessage());
        }
    }
}
