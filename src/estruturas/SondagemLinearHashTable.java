package estruturas;

import registro.Registro;

public class SondagemLinearHashTable {
    private Registro[] table;
    private int currentSize;
    private long collisionCount = 0;
    private int size;

    public SondagemLinearHashTable(int size) {
        this.table = new Registro[size];
        this.currentSize = 0;
        this.size = size;
    }

    private int hash(int key) {
        return (key & 0x7FFFFFFF) % size; // explicado em outros arquivos
    }
    public long getCollisionCount() {
        return this.collisionCount;
    }

    public void insert(Registro registro) {
//        if ((double)currentSize / size > 0.95) {
//            return;
//             como pesquisado, é comum parar de inserir a partir de certa porcentagem de agrupamento,
//             pois a partir de certo ponto, seria imprático e não performático percorrer o array até encontrar o espaço vazio
//             escolhemos fazer isso para ser o mais justo possível com todas as implementações de hash e a sondagem quadrática pode gerar loops infinitos (então também limitamos a quantidade aqui)
//        }

        int initialIndex = hash(registro.codigo);
        int currentIndex = initialIndex;
        int i = 1;

        while (table[currentIndex] != null) {
            collisionCount++;
            currentIndex = (initialIndex + i) % size;
            // percorre o próximo indíce até encontrar um espaço vazio
            // usar `% size` faz com que, quando chegue ao fim da lista, o index dê a volta
            i++;
        }
        table[currentIndex] = registro;
        currentSize++;
    }

    public Registro search(int codigo) {
        int initialIndex = hash(codigo);
        int currentIndex = initialIndex;
        int i = 1;

        while (table[currentIndex] != null) {
            if (table[currentIndex].codigo == codigo) {
                return table[currentIndex];
            }
            currentIndex = (initialIndex + i) % size;
            i++;
            if(currentIndex == initialIndex) break; // se deu a volta no array e não encontrou, para a execução
        }
        return null;
    }

    public double[] getAnalise() {
        double menorGap = Double.MAX_VALUE; // evita necessidade de comparações com null que reduziriam a performance desnecessariamente
        double maiorGap = 0;
        double somaGaps = 0;
        int contagemGaps = 0;

        int gapAtual = 0;
        boolean emGap = false;

        for (int i = 0; i < size; i++) {
            if (table[i] == null) {
                gapAtual++;
                emGap = true;
            } else {
                if (emGap) {
                    // caso seja o final de um gap, registra nas variáveis
                    if (gapAtual < menorGap) menorGap = gapAtual;
                    if (gapAtual > maiorGap) maiorGap = gapAtual;
                    somaGaps += gapAtual;
                    contagemGaps++;

                    gapAtual = 0;
                    emGap = false;
                }
            }
        }
        // um gap pode ter ficado em aberto anteriormente, entao é registrado aqui
        if (emGap) {
            if (gapAtual < menorGap) menorGap = gapAtual;
            if (gapAtual > maiorGap) maiorGap = gapAtual;
            somaGaps += gapAtual;
            contagemGaps++;
        }

        if(contagemGaps == 0) {
            return new double[]{0, 0, 0};
        }

        double mediaGap = somaGaps / contagemGaps;
        return new double[]{menorGap, maiorGap, mediaGap};
    }
}