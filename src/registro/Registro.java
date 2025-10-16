package registro;

public class Registro {
    public final int codigo; // final, pois nao faz sentido permitir mutações no códgigo após a criação

    public Registro(int codigo) {
        this.codigo = codigo;
    }

    @Override
    public String toString() { // sobreescreve funcao ja existente para facilidade e clareza na execucao (retorna o codigo em str)
        return String.valueOf(this.codigo);
    }
}