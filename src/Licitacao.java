public class Licitacao {
    private String nome;
    private int valor;

    Licitacao(String nome ,int valor){
        this.nome = nome;
        this.valor = valor;
    }


    @Override
    public String toString() {
        return nome+"->"+valor+"€";
    }

    public String getNome() {
        return nome;
    }

    public int getValor() {
        return valor;
    }

    public void alterarValor(int valor){
        this.valor = valor;
    }
    public void aumentaValor(int valor){
        this.valor +=valor;
    }
}
