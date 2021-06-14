
// Connectar ao porto 6500 de um servidor especifico,
// envia uma mensagem e imprime resultado,

public class Licitador {
    // usage: java EchoClient <servidor> <mensagem>
    private String userName;
    private String password;
    private int plafond =3000;

    Licitador(String userName ,String password, int plafond){
        this.userName = userName;
        this.password =password;
        this.plafond = plafond;
    }
    Licitador(String userName ,String password){
        this.userName = userName;
        this.password =password;
    }

    public String getPassword() {
        return password;
    }

    public int getPlafond() {
        return plafond;
    }


    public String getUserName() {
        return userName;
    }
    public void diminuiPlafond(int valor){
        int plafond = getPlafond();
        if (valor>0 && plafond-valor>=0){
            this.plafond-=valor;
        }
    }

    public void aumentaPlafond(int valor){
        if (valor > 0){
            plafond+=valor;
        }
    }
    public void atualizaLicitador (int valor){
        if(valor < 0){
            valor*=-1;
            diminuiPlafond(valor);
        }else {
            aumentaPlafond(valor);
        }
    }
    public String toString(){
        return userName + ":" +password+":"+plafond;
    }
    public String licitadorComValorAntigo(int valor){
        int liquidoV = plafond+valor;
        return userName + ":" +password+":"+liquidoV;
    }
    public String licitadorAtual(int valorRetirar){
        int plafond = getPlafond();
        plafond -=valorRetirar;
        atualizaLicitador(-valorRetirar);
        return userName + ":" +password+":"+plafond;
    }
}