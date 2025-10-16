package estruturas;

import registro.Registro;

public class SondagemQuadraticaHashTable {
    // sondagem quadrática evita aglomeração em um ponto específico e em média, distribui mais os registros comparado a sondagem linear
    private Registro[] table;
    private int currentSize;
    private long collisionCount = 0;
    private int size;

    public SondagemQuadraticaHashTable(int size) {
        this.table = new Registro[size];
        this.currentSize = 0;
        this.size = size;
    }

    private int hash(int key) {
        return (key & 0x7FFFFFFF) % size; // explicado em outros arquivos
    }

    public void insert(Registro registro) {
        if ((double)currentSize / size > 0.95) {
            // pode gerar loops infinitos em situações especificas caso esteja muito cheia ou prejudicar muito a performance
            return;
        }

        int initialIndex = hash(registro.codigo);
        int currentIndex = initialIndex;
        long i = 1; // como a lista pode ser extremamente grande, o long é mais apropriado

        while (table[currentIndex] != null) {
            collisionCount++;
            // fórmula comum de Sondagem Quadrática
            currentIndex = (int) ((initialIndex + i * i) % size);
            i++;
        }
        table[currentIndex] = registro;
        currentSize++;
    }

    public Registro search(int codigo) {
        int initialIndex = hash(codigo);
        int currentIndex = initialIndex;
        long i = 1;

        while (table[currentIndex] != null) {
            if (table[currentIndex].codigo == codigo) {
                return table[currentIndex];
            }
            // usa a mesma sequência de saltos usados na inserção
            currentIndex = (int) ((initialIndex + i * i) % size);
            i++;

            if (i > size) { // caso não tenha espaço ou o espaço não foi encontrado por conta dos saltos em posições específicas
                break;
            }
        }
        return null;
    }

    public long getCollisionCount() {
        return this.collisionCount;
    }

    public double[] getAnalise() {
        // Usa Double.MAX_VALUE para garantir que qualquer gap encontrado seja menor
        double menorGap = Double.MAX_VALUE;
        double maiorGap = 0;
        double somaGaps = 0;
        int contagemGaps = 0;

        int gapAtual = 0;
        boolean emGap = false;

        // Itera uma única vez pela tabela para coletar todas as estatísticas
        for (int i = 0; i < size; i++) {
            if (table[i] == null) {
                // Se encontramos um espaço vazio, estamos em um gap
                gapAtual++;
                emGap = true;
            } else {
                // Se encontramos um elemento e estávamos em um gap, o gap acabou
                if (emGap) {
                    // Processa o gap que acabamos de encontrar
                    if (gapAtual < menorGap) menorGap = gapAtual;
                    if (gapAtual > maiorGap) maiorGap = gapAtual;
                    somaGaps += gapAtual;
                    contagemGaps++;

                    // Reseta para o próximo
                    gapAtual = 0;
                    emGap = false;
                }
            }
        }

        // Se a tabela terminar com um espaço vazio, precisamos processar o último gap
        if (emGap) {
            if (gapAtual < menorGap) menorGap = gapAtual;
            if (gapAtual > maiorGap) maiorGap = gapAtual;
            somaGaps += gapAtual;
            contagemGaps++;
        }

        // Se não houver gaps (tabela 100% cheia), retorna 0 para todas as métricas
        if(contagemGaps == 0) {
            return new double[]{0, 0, 0};
        }

        double mediaGap = somaGaps / contagemGaps;
        return new double[]{menorGap, maiorGap, mediaGap};
    }
}
