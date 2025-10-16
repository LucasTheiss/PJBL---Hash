import registro.Registro;
import estruturas.EncadeamentoHashTable;
import estruturas.SondagemLinearHashTable;
import estruturas.SondagemQuadraticaHashTable;
import gerador.GeradorDadosAleatorios;

import java.io.FileWriter;
import java.io.IOException;

public class Main {
    // definicao dos tamanhos escolhidos para as tabelas e para a quantidade de dados
    private static final int[] TABLE_SIZES = { 100_000, 1_000_000, 10_000_000 };
    private static final int[] DATA_SIZES = { 100_000, 1_000_000, 10_000_000 };
    // seed usada na geracao dos dados aleatorios
    private static final long SEED = 42L;

    public static void main(String[] args) {

        // stringBuilder para montar o relatório CSV
        StringBuilder csvReport = new StringBuilder();
        csvReport.append("TamanhoTabela,TamanhoDados,FatorCarga,Estrategia,TempoInsercao(ms),Colisoes,TempoBusca(ms),Metrica1_Nome,Metrica1_Valor,Metrica2_Nome,Metrica2_Valor,Metrica3_Nome,Metrica3_Valor\n");

        // Loop principal para iterar sobre cada tamanho de tabela definido.
        for (int tableSize : TABLE_SIZES) {
            // Loop secundário para iterar sobre cada conjunto de dados.
            for (int dataSize : DATA_SIZES) {

                System.out.println("========================================================================");
                System.out.printf("== INICIANDO RODADA: TAMANHO TABELA = %-10d | TAMANHO DADOS = %-10d ==\n", tableSize, dataSize);
                System.out.println("========================================================================");

                // ETAPA 1: Geração dos dados (feito uma vez por rodada para garantir comparação justa)
                System.out.println("\n[ETAPA 1 de 4] Gerando conjunto de dados...");
                Registro[] data = GeradorDadosAleatorios.generate(dataSize, SEED);
                System.out.println("-> Geração de dados concluída.");

                //
                // TESTE COM ENCADEAMENTO SEPARADO (CHAINING)
                //
                System.out.println("\n--- [ETAPA 2 de 4] Testando: Encadeamento Separado ---");
                EncadeamentoHashTable chainingTable = new EncadeamentoHashTable(tableSize);

                // Inserção
                long startTime = System.nanoTime();
                for (Registro registro : data) {
                    chainingTable.insert(registro);
                }
                long insertionTimeChaining = (System.nanoTime() - startTime) / 1_000_000;

                // Busca
                startTime = System.nanoTime();
                for (Registro registro : data) {
                    chainingTable.search(registro.codigo);
                }
                long searchTimeChaining = (System.nanoTime() - startTime) / 1_000_000;

                // Coleta de métricas
                long collisionsChaining = chainingTable.getCollisionCount();
                double[] metricsChaining = chainingTable.getAnalise();
                System.out.printf("-> Resultados: Tempo Inserção = %d ms | Colisões = %d | Tempo Busca = %d ms\n",
                        insertionTimeChaining, collisionsChaining, searchTimeChaining);

                // Adiciona ao relatório CSV
                double loadFactor = (double) dataSize / tableSize;
                csvReport.append(String.format("%d,%d,%.2f,%s,%d,%d,%d,%s,%.0f,%s,%.0f,%s,%.0f\n",
                        tableSize, dataSize, loadFactor, "Encadeamento Separado",
                        insertionTimeChaining, collisionsChaining, searchTimeChaining,
                        "maior_lista", metricsChaining[0], "2a_maior_lista", metricsChaining[1], "3a_maior_lista", metricsChaining[2]));

                // As estratégias de sondagem só funcionam se N < M (fator de carga < 1)
                if (dataSize >= tableSize) {
                    System.out.println("\n[AVISO] Pulando testes de Sondagem Linear e Quadrática pois o número de dados é >= ao tamanho da tabela.\n");
                    continue; // Pula para a próxima iteração do loop
                }

                //
                // TESTE COM SONDAGEM LINEAR (LINEAR PROBING)
                //
                System.out.println("\n--- [ETAPA 3 de 4] Testando: Sondagem Linear ---");
                SondagemLinearHashTable linearTable = new SondagemLinearHashTable(tableSize);

                // Inserção
                startTime = System.nanoTime();
                for (Registro registro : data) {
                    linearTable.insert(registro);
                }
                long insertionTimeLinear = (System.nanoTime() - startTime) / 1_000_000;

                // Busca
                startTime = System.nanoTime();
                for (Registro registro : data) {
                    linearTable.search(registro.codigo);
                }
                long searchTimeLinear = (System.nanoTime() - startTime) / 1_000_000;

                // Coleta de métricas
                long collisionsLinear = linearTable.getCollisionCount();
                double[] metricsLinear = linearTable.getAnalise();
                System.out.printf("-> Resultados: Tempo Inserção = %d ms | Colisões = %d | Tempo Busca = %d ms\n",
                        insertionTimeLinear, collisionsLinear, searchTimeLinear);

                // Adiciona ao relatório CSV
                csvReport.append(String.format("%d,%d,%.2f,%s,%d,%d,%d,%s,%.0f,%s,%.0f,%s,%.2f\n",
                        tableSize, dataSize, loadFactor, "Sondagem Linear",
                        insertionTimeLinear, collisionsLinear, searchTimeLinear,
                        "menor_gap", metricsLinear[0], "maior_gap", metricsLinear[1], "media_gap", metricsLinear[2]));

                //
                // TESTE COM SONDAGEM QUADRÁTICA (QUADRATIC PROBING)
                //
                System.out.println("\n--- [ETAPA 4 de 4] Testando: Sondagem Quadrática ---");
                SondagemQuadraticaHashTable quadraticTable = new SondagemQuadraticaHashTable(tableSize);

                // Inserção
                startTime = System.nanoTime();
                for (Registro registro : data) {
                    quadraticTable.insert(registro);
                }
                long insertionTimeQuadratic = (System.nanoTime() - startTime) / 1_000_000;

                // Busca
                startTime = System.nanoTime();
                for (Registro registro : data) {
                    quadraticTable.search(registro.codigo);
                }
                long searchTimeQuadratic = (System.nanoTime() - startTime) / 1_000_000;

                // Coleta de métricas
                long collisionsQuadratic = quadraticTable.getCollisionCount();
                double[] metricsQuadratic = quadraticTable.getAnalise();
                System.out.printf("-> Resultados: Tempo Inserção = %d ms | Colisões = %d | Tempo Busca = %d ms\n",
                        insertionTimeQuadratic, collisionsQuadratic, searchTimeQuadratic);

                // Adiciona ao relatório CSV
                csvReport.append(String.format("%d,%d,%.2f,%s,%d,%d,%d,%s,%.0f,%s,%.0f,%s,%.2f\n",
                        tableSize, dataSize, loadFactor, "Sondagem Quadrática",
                        insertionTimeQuadratic, collisionsQuadratic, searchTimeQuadratic,
                        "menor_gap", metricsQuadratic[0], "maior_gap", metricsQuadratic[1], "media_gap", metricsQuadratic[2]));

                System.out.println("\nFIM DA RODADA.\n");
            }
        }

        // Salva o relatório CSV completo no final de todos os testes.
        saveReport(csvReport.toString());
    }

    /**
     * Função auxiliar para salvar o conteúdo do relatório em um arquivo .csv.
     * @param report O conteúdo do relatório em formato de String.
     */
    private static void saveReport(String report) {
        FileWriter writer = new FileWriter("results/performance_report.csv");
        writer.write(report);
        System.out.println("========================================================================");
        System.out.println("== Relatório 'performance_report.csv' salvo com sucesso na pasta 'results/'. ==");
        System.out.println("========================================================================");

    }
}
