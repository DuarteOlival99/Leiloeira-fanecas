import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;


public class Leilao {
    private static int id;
    private int idLeilao;
    private String dataLimite;
    private String horaLimite;
    private String autor;
    private String descricao;
    private List<Licitacao> licitadores = new ArrayList<>();
    DateFormat fortime = new SimpleDateFormat("hh:mm:ss a");

    Leilao(String id,String dateLimite,String horaLimite, String autor, String descricao) {
        this.idLeilao= Integer.parseInt(id);
        this.dataLimite = dateLimite;
        this.horaLimite = horaLimite;
        this.autor = autor;
        this.descricao = descricao;
    }
    Leilao(String dateLimite,String horaLimite, String autor, String descricao) {
        id++;
        idLeilao++;
        this.dataLimite = dateLimite;
        this.horaLimite = horaLimite;
        this.autor = autor;
        this.descricao = descricao;
    }

    public static  void aumentaId(){
        id++;
    }
    public String toString(){
        return id+" | "+dataLimite+" | "+horaLimite+" | "+autor+" | "+descricao+" | "+licitadores+"\n";
    }

    public boolean verificaLeilaoTerminado(){

        int resultData = CommandHandler.dataValida(getDataLimite());
        if(resultData == 1){
            return true;
        }
        if(resultData == 2){

            Date date = new Date();


            String toReturn = fortime.format(date);

            //dU == dataUser
            //dA == dataAtual
            List<String> hourSistemaAmPm = Arrays.asList(toReturn.split(" "));
            List<String> hourLimiteAmPm = Arrays.asList(getHoraLimite().split(" "));

            List<String> hourSistema = Arrays.asList(hourSistemaAmPm.get(0).split(":"));
            List<String> hourLimite = Arrays.asList(hourLimiteAmPm.get(0).split(":"));


            int hora = 0;
            int minutos = 0;
            int segundos = 0;
            int sistema = 0;
            int user = 0;

            for (int i = 2; i >= 0; i--) {

                // i = 0 -> dia
                // i = 1 -> mes
                // i = 2 -> ano
                int d1 = Integer.parseInt(hourSistema.get(i));//data do sistema
                int d2 = Integer.parseInt(hourLimite.get(i));//data do user

                if (i == 0) {
                    if (d2 > d1) {
                        hora = 2;

                    } else if (d2 == d1) {
                        hora = 1;
                    }
                } else if (i == 1) {
                    if (d2 > d1) {
                        minutos = 2;
                    } else if (d2 == d1) {
                        minutos = 1;
                    }
                } else if (i == 2) {
                    if (d2 > d1) {
                        segundos = 2;
                    } else if (d2 == d1) {
                        segundos = 1;
                    }
                }
            }

            if (hourSistemaAmPm.get(1).equals("AM")) {
                sistema = 0;
            } else if (hourSistemaAmPm.get(1).equals("PM")) {
                sistema = 1;
            }

            if (hourLimiteAmPm.get(1).equals("AM")) {
                user = 0;
            } else if (hourLimiteAmPm.get(1).equals("PM")) {
                user = 1;
            }


            if(user == 1 && sistema == 0){
                return true;
            }else if(user == sistema) {

                if (hora == 2) {
                    return true;
                } else if (hora == 1 && minutos == 2) {
                    return true;
                } else return hora == 1 && minutos == 1 && (segundos == 2);
            }


        }

        return false;
    }

    public String escreverLeilao(){
        return idLeilao+" | "+dataLimite+" | "+horaLimite+" | "+autor+" | "+descricao+" | "+licitadores+"\n";
    }
    public String escreverLeilaoVazio(){
        List<Licitacao> licitacao = Collections.emptyList();
        return idLeilao+" | "+dataLimite+" | "+horaLimite+" | "+autor+" | "+descricao+" | "+licitacao+"\n";
    }
    public String escreverLeilaoAntigo(){
        return idLeilao+" | "+dataLimite+" | "+horaLimite+" | "+autor+" | "+descricao+" | "+licitadores.subList(0,licitadores.size()-1)+"\n";
    }
    public String[] escreverLeilaoSubtrativoOne(int valor){
        String leiloes[] = new String[2];
        List<Licitacao> licitacao= new ArrayList<>();
        licitacao.add(licitadores.get(0));
        int valorInicial = licitacao.get(0).getValor()-valor;
        licitacao.get(0).alterarValor(valorInicial);
        leiloes[0]=idLeilao+" | "+dataLimite+" | "+horaLimite+" | "+autor+" | "+descricao+" | "+licitacao+"\n";

        valorInicial = licitacao.get(0).getValor()+valor;
        licitacao.get(0).alterarValor(valorInicial);
        leiloes[1]=idLeilao+" | "+dataLimite+" | "+horaLimite+" | "+autor+" | "+descricao+" | "+licitacao+"\n";

        return leiloes;
    }
    public String[] escreverLeilaoSubtrativo(int valor ,Licitacao licitation){
        String leiloes[] = new String[2];
        List<Licitacao> licitacao= new ArrayList<>(licitadores);

        int valorInicial = 0;
        Licitacao licitacaoAtual = licitation;

        for (Licitacao bet : licitacao){
            if (bet.getNome().equals(licitation.getNome())){
                licitacaoAtual = bet;
                valorInicial = bet.getValor()-valor;
                break;
            }
        }

        licitacaoAtual.alterarValor(valorInicial);
        leiloes[0]=idLeilao+" | "+dataLimite+" | "+horaLimite+" | "+autor+" | "+descricao+" | "+licitacao+"\n";

        valorInicial = licitacaoAtual.getValor()+valor;
        licitacaoAtual.alterarValor(valorInicial);
        leiloes[1]=idLeilao+" | "+dataLimite+" | "+horaLimite+" | "+autor+" | "+descricao+" | "+licitacao+"\n";

        return leiloes;
    }

    public static String getId() {
        return String.valueOf(id);
    }


    public String getDescricao() {
        return descricao;
    }

    public String getDataLimite() {
        return dataLimite;
    }

    public void addLicitador(Licitacao licitacao){
        boolean existe = false;

        for (Licitacao licitador : licitadores){
            if (licitacao.getNome().equals(licitador.getNome())){
                //  System.out.println(licitadoresAntigos);
                //  System.out.println("Tamanho "+licitadoresAntigos.size());
                licitador.aumentaValor(licitacao.getValor());
                existe = true;
                break;
            }
        }
        if (!existe){
            licitadores.add(licitacao);
        }
        //System.out.println("Adicionei ao leilao "+idLeilao);

        //   System.out.println(licitadoresAntigos+"--- ");
    }

    public List<Licitacao> getLicitadores() {
        return licitadores;
    }

    public String getAutor() {
        return autor;
    }

    public String getHoraLimite(){
        return horaLimite;
    }

    public int maiorLicitacao (){
        int maior = 0;
        for (Licitacao licitacao : licitadores){
            if (licitacao.getValor()> maior){
                maior = licitacao.getValor();
            }
        }
        return maior;
    }
    public String maiorLicitacaoNome (){
        int maior = 0;
        String nome ="";
        for (Licitacao licitacao : licitadores){
            if (licitacao.getValor()> maior){
                maior = licitacao.getValor();
                nome = licitacao.getNome();
            }
        }
        return nome;
    }
    public int valorLicitacaoDoId(String userName){
        for (Licitacao licitacao : licitadores){
            if (licitacao.getNome().equals(userName)){
                return licitacao.getValor();
            }
        }
        return 0;
    }
    public boolean jaLicitou (String userName){
        for (Licitacao licitacao : licitadores){
            if (licitacao.getNome().equals(userName)){
                return true;
            }
        }
        return false;
    }

}