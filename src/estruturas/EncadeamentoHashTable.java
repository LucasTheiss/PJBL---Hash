package estruturas;

import registro.Registro;

public class EncadeamentoHashTable {
    private static class Node {
        Registro registro;
        Node next;

        Node(Registro registro) {
            this.registro = registro;
            this.next = null;
        }
    }
    private int size;
    private Node[] table; // lista de nodes, mais nodes podem ser encadeados em casos de colisao
    private long collisionCount = 0;

    public EncadeamentoHashTable(int size) {
        this.table = new Node[size];
        this.size = size;
    }
    private int hash(int key) {
        return (key & 0x7FFFFFFF) % size; // linha para criacao de hash pesquisada (usa bitwise e faz um And com a key)
    }
    public long getCollisionCount() {
        return this.collisionCount;
    }

    public void insert(Registro registro) {
        int index = hash(registro.codigo);
        Node newNode = new Node(registro);

        if (table[index] == null) {
            table[index] = newNode;
        } else { // caso ocorra colisão, percorre pelos nodes até adicionar
            Node current = table[index];
            collisionCount++;
            while (current.next != null) {
                collisionCount++;
                current = current.next;
            }
            current.next = newNode;
        }
    }

    public Registro search(int codigo) {
        int index = hash(codigo);
        Node current = table[index];
        while (current != null) {
            if (current.registro.codigo == codigo) {
                return current.registro;
            }
            current = current.next;
        }
        return null;
    }

    public double[] getAnalise() {
        double maior1 = 0, maior2 = 0, maior3 = 0;

        // percorre por toda tabela e itera por todas as listas encadeadas formadas
        for (int i = 0; i < size; i++) {
            int count = 0;
            Node current = table[i];
            while (current != null) {
                count++;
                current = current.next;
            }

            if (count > maior1) {
                maior3 = maior2;
                maior2 = maior1;
                maior1 = count;
            } else if (count > maior2) {
                maior3 = maior2;
                maior2 = count;
            } else if (count > maior3) {
                maior3 = count;
            }
        }

        return new double[]{maior1, maior2, maior3};
    }
}