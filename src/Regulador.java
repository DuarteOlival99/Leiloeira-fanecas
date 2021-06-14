import jdk.internal.dynalink.beans.StaticClass;

import javax.swing.plaf.nimbus.State;
import java.io.*;
import java.net.*;
import java.nio.channels.SocketChannel;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

// Server class
public class Regulador
{

    private static LinkedHashMap<String, Licitador> users = new LinkedHashMap<>();
    private static LinkedHashMap<String,Leilao> leiloes = new LinkedHashMap<>();

    private static HashSet<String> leiloesInativos = new HashSet<>();
    static List<Integer> portos = new ArrayList<>();
    static Map<String, Licitador> clients = new HashMap<String, Licitador>();

    public static HashSet<String> getLeiloesInativos() {
        return leiloesInativos;
    }

    public static void adicionaLeilaoInativo(String id){
        leiloesInativos.add(id);
    }


    public static boolean atualizaLeiloes(int id, Leilao leilao,String nome, int valor){
        try {
            BufferedReader reader = new BufferedReader(new FileReader("leiloes.txt"));
            String line = "", oldtext = "";
            String result ="",antiga ="[]",nova="";

            while ((line = reader.readLine()) != null) {
                oldtext += line + "\r\n";
            }
            reader.close();
            if (leilao.getLicitadores().size()>1 && valor == 0){
                System.out.println("1");
                antiga = leilao.escreverLeilaoAntigo().replace("\n","");
                result = oldtext.replace(antiga, leilao.escreverLeilao().replace("\n",""));

            }else if (leilao.getLicitadores().size()==1 && valor == 0){//Ainda não licitou neste leilao mas já existe uma licitaçao

                System.out.println("Nova: "+leilao.escreverLeilao().replace("\n",""));
                antiga = leilao.escreverLeilaoVazio().replace("\n","");
                System.out.println("Antiga; "+antiga);
                result = oldtext.replace(antiga, leilao.escreverLeilao().replace("\n",""));

            }else if (leilao.getLicitadores().size()>1 && valor != 0){//Licitação antiga com outras
                Licitacao licitacao = new Licitacao(nome,valor);
                antiga = leilao.escreverLeilaoSubtrativo(valor,licitacao)[0].replace("\n","");
                nova =leilao.escreverLeilaoSubtrativo(valor,licitacao)[1].replace("\n","");

                result = oldtext.replace(antiga, nova);

            }else if (leilao.getLicitadores().size()==1 && valor != 0){//Apenas a licitação antiga

                antiga = leilao.escreverLeilaoSubtrativoOne(valor)[0].replace("\n","");
                nova =leilao.escreverLeilaoSubtrativoOne(valor)[1].replace("\n","");
                result = oldtext.replace(antiga, nova);

            }else {//Ainda não tem licitações

                System.out.println(leilao.escreverLeilao().replace("\n",""));
                result = oldtext.replace("6 : 18/07/1999 : afonso : AAAAAAAAAAAAAAAA : []", leilao.escreverLeilao().replace("\n",""));
            }


            // System.out.println(result);
            // Write updated record to a file
            FileWriter writer = new FileWriter("leiloes.txt");
            writer.write(result);
            writer.close();
        } catch (IOException ioe) {
            System.out.println("Write error");
        }

        if (leiloes.containsKey(String.valueOf(id))){
            leiloes.replace(String.valueOf(id),leilao);
            return true;
        }else {
            return false;
        }
    }
    public static boolean atualizaDadosUser(Licitador licitador, int valorDebitar){
        try {
            System.out.println(licitador);
            BufferedReader reader = new BufferedReader(new FileReader("dadosUsers.txt"));
            String line = "", oldtext = "";
            String result ="",antiga ="[]",nova="";
            Licitador licitadorAntigo = licitador;
            licitadorAntigo.diminuiPlafond(valorDebitar);

            while ((line = reader.readLine()) != null) {
                oldtext += line + "\r\n";
            }
            reader.close();

            antiga = licitador.licitadorComValorAntigo(valorDebitar).replace("\n","");

            result = oldtext.replace(antiga, licitador.toString().replace("\n",""));
            // System.out.printf(result);

            // System.out.println(result);
            // Write updated record to a file
            FileWriter writer = new FileWriter("dadosUsers.txt");
            writer.write(result);
            writer.close();
        } catch (IOException ioe) {
            System.out.println("Write error");
        }

        if (users.containsKey(licitador.getUserName())){
            users.replace(licitador.getUserName(),licitador);
            return true;
        }else {
            return false;
        }
    }
    public static void devolveDinheiro(List<Licitacao> licitacoes){
        try {
            List<Licitacao> licita = licitacoes;
            Licitador licAntigo = null;

            BufferedReader reader = new BufferedReader(new FileReader("dadosUsers.txt"));

            String line = "", oldtext = "";
            String result ="",antiga ="[]",nova="";

            while ((line = reader.readLine()) != null) {
                oldtext += line + "\r\n";
            }
            reader.close();
            for (Licitacao licitacao : licita){
                licAntigo =clients.get(licitacao.getNome());
                Licitador licNovo = new Licitador(licitacao.getNome(),licAntigo.getPassword(),licitacao.getValor()+licAntigo.getPlafond());


            antiga =  licAntigo.toString().replace("\n","");

            result = oldtext.replace(antiga, licNovo.toString().replace("\n",""));
            // System.out.printf(result);

            // System.out.println(result);
            // Write updated record to a file
            FileWriter writer = new FileWriter("dadosUsers.txt");
            writer.write(result);
            writer.close();
            }
        } catch (IOException ioe) {
            System.out.println("Write error");
        }

    }
    private static void lerUsers() throws IOException{
        String nomeFicheiro = "dadosUsers.txt";
        try {
            File ficheiro = new File(nomeFicheiro);
            Scanner leitorFicheiro = new Scanner(ficheiro);
            while(leitorFicheiro.hasNextLine()) {
                // ler uma linha do ficheiro
                String linha = leitorFicheiro.nextLine();
                // partir a linha no caractere separador
                String dados[] = linha.split(":");
                // converter as Strings lidas para os tipos esperados

                String username =dados[0];
                String password = dados[1];
                int plafond = Integer.parseInt(dados[2]);
                Licitador licitador = new Licitador(username ,password,plafond);
                clients.put(username,licitador);
                users.put(username,licitador);
                // criar o objecto Utilizador
                // Leilao utilizadorA = new Leilao("2020/5/2","Jon","1 leilao");
            }
            System.out.println("Ficheiro " +nomeFicheiro+" lido com sucesso");
            leitorFicheiro.close();
        }
        catch(FileNotFoundException exception) {
            String mensagem = "Erro: o ficheiro " + nomeFicheiro + " nao foi encontrado.";
            System.out.println(mensagem);
        }
    }
    static void escreverUser(Licitador licitador) throws FileNotFoundException {
        try
        {
            String filename= "dadosUsers.txt";
            FileWriter fw = new FileWriter(filename,true); //Caso seja verdadeiro vai fazer append da nova informação
            fw.write("\n"+licitador);//appends username e password
            fw.close();
        }
        catch(IOException ioe)
        {
            System.err.println("IOException: " + ioe.getMessage());
        }
    }
    static void escreverLeilao(Leilao leilao) throws FileNotFoundException {
        try
        {
            String filename= "leiloes.txt";
            FileWriter fw = new FileWriter(filename,true); //Caso seja verdadeiro vai fazer append da nova informação
            fw.write(leilao.toString());//appends
            System.out.println("Sucesso: "+leilao);
            fw.close();
        }
        catch(IOException ioe)
        {
            System.err.println("IOException: " + ioe.getMessage());
        }
    }

    public static String escreverLeiloesAtivos(){
        StringBuilder leiloesAtivos =new StringBuilder();
        for (Leilao leilao : leiloes.values()){
            leiloesAtivos.append(leilao.escreverLeilao());
        }
        return String.valueOf(leiloesAtivos);
    }
    public static Leilao leilaoComId (String id){
        return leiloes.get(id);
    }
    public static HashMap<String,Leilao> getLeiloes(){
        return leiloes;
    }
    public static String fromString(String string) {
        String palavra = string.replace("[", "").replace("]", "").replace(",","").replace("->"," ").replace("€","");

        return palavra;
    }
    public static void lerLeiloes() throws IOException{
        String nomeFicheiro = "leiloes.txt";
        String nomeValorDados ="";
        String nomeValor[] = {};
        List<String> lista = new ArrayList<>();
        int idCount = 1;
        try {
            File ficheiro = new File(nomeFicheiro);
            Scanner leitorFicheiro = new Scanner(ficheiro);
            while(leitorFicheiro.hasNextLine()) {
                // ler uma linha do ficheiro
                String linha = leitorFicheiro.nextLine();
                // partir a linha no caractere separador
                String dados[] = linha.split(" \\| ");
                // converter as Strings lidas para os tipos esperados

                String id = dados[0];
                String data = dados[1];
                String hora = dados[2];
                String nomeUser = dados[3];
                String descricao = dados[4];


                String array[]=fromString(dados[5]).split(" ");

                Leilao leilao = new Leilao(String.valueOf(idCount),data,hora,nomeUser,descricao);

                String licitador ="";
                String licitacao="";

                for (int count = 0 ; count < array.length; count+=1){
                    if (count%2==0){
                        licitador = array[count];
                    }else {
                        licitacao = array[count];
                    }
                    if (count%2!=0){
                        Licitacao aposta = new Licitacao(licitador,Integer.parseInt(licitacao));
                        leilao.addLicitador(aposta);
                    }
                }
                leiloes.put(String.valueOf(idCount),leilao);
                idCount++;
                Leilao.aumentaId();
            }
            System.out.println("Ficheiro " +nomeFicheiro+" lido com sucesso");
            leitorFicheiro.close();
        }
        catch(FileNotFoundException exception) {
            String mensagem = "Erro: o ficheiro " + nomeFicheiro + " nao foi encontrado.";
            System.out.println(mensagem);
        }
    }

    static HashMap<String, Licitador> getUsers() {
        return users;
    }
    public boolean contemUser(String user){
        if (users.containsKey(user)){
            return true;
        }else {
            return false;
        }
    }
    static boolean adicionaUser(String username, Licitador licitador){
        if (users.containsKey(username)){
            return false;
        }else {
            users.put(username,licitador);
            return true;
        }
    }
    static void adicionarLeilao(Leilao leilao){
        leiloes.put(leilao.getId(),leilao);
    }

    public int escreverLicitador (Licitador licitador) throws IOException {

        Scanner leitorFicheiro = new Scanner("licitadores.txt");
        // enquanto o ficheiro tiver linhas

        while(leitorFicheiro.hasNextLine()) {
            String linha = leitorFicheiro.nextLine();
            // partir a linha no caractere
            String dados[] = linha.split(" : ");
            if (dados[0].equals(licitador.getUserName())){
                return 0;
            }
        }
        PrintWriter writer = new PrintWriter("licitadores.txt");
        writer.println(licitador.toString());
        writer.close();
        leitorFicheiro.close();
        return 1;
    }
    public void clienteUdp(String porto){

    }
    public static void main(String[] args) throws IOException
    {
        System.out.println("Servidor TCP Iniciado\n");

        lerUsers();
        //System.out.println(leiloes.size());
        lerLeiloes();
        //System.out.println(leiloes.size());
        // server is listening on port 7810
        ServerSocket ss = new ServerSocket(7810);

        // running infinite loop for getting
        // client request
        while (true)
        {
            Socket s = null;
            try
            {
                // socket object to receive incoming client requests
                s = ss.accept();

                // nrPorto++;//Aumenta o numero do porto
                System.out.println("A new client is connected : " + s);

                // obtaining input and out streams
                DataInputStream dis = new DataInputStream(s.getInputStream());
                DataOutputStream dos = new DataOutputStream(s.getOutputStream());

                System.out.println("Assigning new thread for this client");

                // create a new thread object
                Thread t = new CommandHandler(s, dis, dos);

                // Invoking the start() method
                t.start();


            }
            catch (Exception e){
                s.close();
                e.printStackTrace();
            }
        }
    }


}

// ClientHandler class
class CommandHandler extends Thread
{

    static DateFormat fordate = new SimpleDateFormat("yyyy/MM/dd");
    DateFormat fortime = new SimpleDateFormat("hh:mm:ss a");
    final DataInputStream dis;
    final DataOutputStream dos;
    final Socket s;
    private Hash hash = new Hash();
    private DatagramSocket socket;
    int nrPorto = 1024;
    String ip;


    // Constructor
    public CommandHandler(Socket s, DataInputStream dis, DataOutputStream dos) throws SocketException {
        socket = new DatagramSocket();
        this.s = s;
        this.dis = dis;
        this.dos = dos;
        ip = String.valueOf(s.getInetAddress());
    }


    public static int generate(int min,int max)
    {
        return min + (int)(Math.random() * ((max - min) + 1));
    }

    public void sendEcho(String msg) throws IOException {
        byte[] buf;
        buf = msg.getBytes();
        DatagramPacket packet = new DatagramPacket(buf, buf.length, InetAddress.getByName("localhost"), nrPorto);
        socket.send(packet);

    }
    public void sendEchoAll(String msg) throws IOException {
        byte[] buf;
        buf = msg.getBytes();
        for (int porto : Regulador.portos){
            if (nrPorto!=porto){
                DatagramPacket packet = new DatagramPacket(buf, buf.length, InetAddress.getByName("192.168.1.66"), porto);
                socket.send(packet);
            }
        }

    }
        public void sendEchoIp(String msg) throws IOException {
            byte[] buf;
            buf = msg.getBytes();
            char conertStringToChar[] = ip.toCharArray();
            char ipUser[]  = new char[200];
            for(int i = 1; i < conertStringToChar.length ; i++){
                ipUser[i-1] = conertStringToChar[i];
            }

            DatagramPacket packet = new DatagramPacket(buf, buf.length, InetAddress.getByName(String.valueOf(ipUser)), nrPorto);
            socket.send(packet);

        }


    public static boolean isThisDateValid(String dateToValidate, String dateFromat){

        if(dateToValidate == null){
            return false;
        }

        SimpleDateFormat sdf = new SimpleDateFormat(dateFromat);
        sdf.setLenient(false);

        try {

            //if not valid, it will throw ParseException
            Date date = sdf.parse(dateToValidate);
            // System.out.println(date);

        } catch (ParseException e) {
            return false;
        }
        return true;
    }

    public static int dataValida(String dataUser) {

        if(isThisDateValid(String.valueOf(dataUser), "dd/MM/yyyy")) {

            Date date = new Date();


            String toreturn = fordate.format(date);

            List<String> dA = Arrays.asList(toreturn.split("/"));
            List<String> dU = Arrays.asList(dataUser.split("/"));

            Collections.reverse(dU);


//nr maior = 2; nr igual = 1; nr menos =0;
            int ano = 0;
            int mes = 0;
            int dia = 0;

            for (int i = 0; i < 3; i++) {

                // i = 0 -> dia
                // i = 1 -> mes
                // i = 2 -> ano
                int d1 = Integer.parseInt(dA.get(i));//data do sistema
                int d2 = Integer.parseInt(dU.get(i));//data do user

                if (i == 0) {
                    if (d2 > d1) {
                        ano = 2;
                    } else if (d2 == d1) {
                        ano = 1;
                    }
                } else if (i == 1) {
                    if (d2 > d1) {
                        mes = 2;
                    } else if (d2 == d1) {
                        mes = 1;
                    }
                } else if (i == 2) {
                    if (d2 > d1) {
                        dia = 2;
                    } else if (d2 == d1) {
                        dia = 1;
                    }
                }
            }

            if (ano == 2) {
                return 1;
            } else if (ano == 1 && mes == 2) {
                return 1;
            } else if (ano == 1 && mes == 1 && (dia == 2)) {
                return 1;
            } else if (ano == 1 && mes == 1 && dia == 1){
                return 2;
            }else {
                return 0;
            }

        }
        return 0;


    }

    public boolean hourValida(String hourUser, String dateLimite) {

        int result = dataValida(dateLimite);

        if(result == 1){
            return true;
        }

        if(result == 2) {

            if (isThisDateValid(String.valueOf(hourUser), "hh:mm:ss a")) {

                Date date = new Date();


                String toreturn = fortime.format(date);

                //dU == dataUser
                //dA == dataAtual
                List<String> dA = Arrays.asList(toreturn.split(" "));
                List<String> dU = Arrays.asList(hourUser.split(" "));

                List<String> dAA = Arrays.asList(dA.get(0).split(":"));
                List<String> dUU = Arrays.asList(dU.get(0).split(":"));

                //0 AM 1 PM 2 ERRO
                int dAAA = 2;
                int dUUU = 2;
                int hour = 0;
                int minutos = 0;
                int segundos = 0;

                if (dA.get(1).equals("AM")) {
                    dAAA = 0;
                } else if (dA.get(1).equals("PM")) {
                    dAAA = 1;
                }

                if (dU.get(1).equals("AM")) {
                    dUUU = 0;
                } else if (dA.get(1).equals("PM")) {
                    dUUU = 1;
                }

                if (dAAA < 2 && dUUU < 2) {

//nr maior = 2; nr igual = 1; nr menos =0;

                    // i = 0 -> hour
                    // i = 1 -> minutos
                    // i = 2 -> segundos
                    int d1 = Integer.parseInt(dAA.get(0));//data do sistema
                    int d2 = Integer.parseInt(dUU.get(0));//data do user


                    if (d2 >= d1) {
                        hour = 2;
                    }


                }

                if (dAAA == dUUU && hour == 2) {

                    return true;
                } else if (dUUU == 1 && dAAA == 0 && hour == 2) {
                    return true;
                }

            }
        }
        return false;
    }

    @Override
    public void run()
    {
        HashMap<String, Licitador> users = Regulador.getUsers();
        String received;
        String userName ="";
        String passWord ="";
        //Porto portoParaReceber = new Porto();
        //int porto = portoParaReceber.escolhePorto();

        boolean validMenu = false;
        boolean validAutentication = false;
        boolean validData = false;
        boolean validOption = false;
        boolean menuDois = false;

        Thread validacao = new Thread(()-> {

            while (true) {
                try {
                    Set<String> chaves = Regulador.getLeiloes().keySet();
                    List<Licitacao> licitadores = new ArrayList<>();

                    // read the message sent to this client
                    String idRemover = "";
                    for(String id: chaves){
                        if(id != null){
                            Leilao leilaoTeste = Regulador.getLeiloes().get(id);
                            if (!Regulador.getLeiloesInativos().contains(id)){
                                if (!leilaoTeste.verificaLeilaoTerminado()) {
                                    licitadores= leilaoTeste.getLicitadores();
                                    Licitacao licitadorAux = null;
                                    for (Licitacao licitacao : licitadores){
                                        if (licitacao.getNome().equals(leilaoTeste.maiorLicitacaoNome())){
                                            licitadorAux= licitacao;
                                        }

                                    }
                                    licitadores.remove(licitadorAux);
                                    Regulador.devolveDinheiro(licitadores);
                                    sendEchoAll("\nO leilão com ID " + id + " acaba de terminar.");
                                    Regulador.adicionaLeilaoInativo(id);
                                    idRemover = (id);
                                }
                            }
                        }
                    }
                    Thread.sleep(30000);
                } catch (IOException | InterruptedException e) {

                    e.printStackTrace();
                }
            }
        });

        while (true)
        {
            try {

                //carregar no nr 0 volta ao menu anterior

                // Ask user what he wants
                // dos.writeUTF("What do you want?[data | horas | listar | frase]..\n"+"Type tchau to terminate connection.");
                //System.out.println(dis.readUTF());
                // System.out.println(dis.readUTF());
                //porto=dis.readUTF();
                String opcaoLogin ="1";
                String opcao ="1";

                nrPorto= Integer.parseInt(dis.readUTF());

                Regulador.portos.add(nrPorto);
                System.out.println("Porto recebido: "+nrPorto);
/*
                ip= (dis.readUTF());
                System.out.println("ip recebido: "+ip);
*/
                validacao.start();

                //sendEcho(String.valueOf(Regulador.nrPorto));
                //System.out.println(Regulador.nrPorto);
                boolean menu1 = true;
                boolean menu2 = true;
                boolean programa = false;
                while (!programa) {

                    while (!validMenu) {
                        validAutentication = false;

                        sendEcho("_____________Bem Vindo Leiloeira Fanecas_____________\n(1)Login\n(2)Signup\n(3)Exit");
                        opcaoLogin = dis.readUTF();

                        while (!opcaoLogin.equals("1") && !opcaoLogin.equals("2") && !opcaoLogin.equals("3")) {
                            sendEcho("Error, tente de novo...");
                            opcaoLogin = dis.readUTF();

                        }

                        while (!validAutentication) {
                            //if (!validAutentication){
                            if (opcaoLogin.equals("1")) {
                                String username;
                                String pass;

                                sendEcho("Insira o username: ");
                                //   System.out.println("System.out.println(\"opção de login: \"+opcaoLogin);");
                                username = dis.readUTF();
                                System.out.println(username);

                                sendEcho("Insira a password: ");
                                pass = dis.readUTF();

                                if (!username.equals("") || !pass.equals("")) {

                                    if (Regulador.getUsers().containsKey(username)) {
                                        userName = username;
                                        String passAtual = Regulador.getUsers().get(username).getPassword();
                                        System.out.println();
                                        if (hash.hashComparator(pass, passAtual)) {
                                            sendEcho("Login efectuado com sucesso");
                                            passWord=passAtual;
                                            validAutentication = true;
                                            validMenu = true;
                                            menuDois = false;
                                        } else {
                                            sendEcho("Username ou Password erradas. Tente novamente:");
                                            System.out.println("Erro Password");
                                        }
                                    } else {
                                        //Será para mudar;
                                        //  opcaoLogin = "-1";
                                        sendEcho("Username ou Password erradas. Tente novamente:");
                                        System.out.println("Erro username");
                                    }
                                } else {
                                    //Será para mudar;
                                    //  opcaoLogin = "-1";
                                    sendEcho("Username ou Password erradas. Tente novamente:");
                                    System.out.println("User ou pass vazias");
                                }
                            } else if (opcaoLogin.equals("2")) {

                                while (!validData) {

                                    String username;
                                    String pass;

                                    sendEcho("Insira o username:\n>4 caracteres ");
                                    username = dis.readUTF();
                                    System.out.println(username);

                                    sendEcho("Insira a password:\n>4 caracteres  ");
                                    pass = dis.readUTF();

                                    if (!username.equals("") && !pass.equals("") && username.length()>4 && pass.length()>4) {

                                        if (!users.containsKey(username)) {
                                            String passHash = Hash.getSHA(pass);

                                            Licitador licitador = new Licitador(username, passHash);
                                            Regulador.escreverUser(licitador);
                                            Regulador.adicionaUser(username, licitador);
                                            userName = username;
                                            sendEcho(username + " adicionado com sucesso.");
                                            System.out.println("Sucesso");
                                            validData = true;
                                            validAutentication = true;
                                            validMenu = true;
                                            menuDois = false;
                                        } else {
                                            sendEcho("Utilizado ja existe, queira por favor escolher outro username.");
                                        }
                                    } else {
                                        sendEcho("Utilizador ou PassWord, com credenciais erradas");
                                    }
                                }
                            } else if (opcaoLogin.equals("3")) {
                                sendEcho("Connection closed");

                                System.out.println("Connection closed");
                                validAutentication = true;
                                validMenu = true;
                                programa = true;
                                break;
                            }
                        }
                    }
                    while (!menuDois) {
                        String opcaoMenu2 = "";
                        boolean OM2 = false;
                        while (!OM2) {

                            sendEcho("Opções: \n(1)Novo leilão | (2)Leilões atuais | (3)Licitar | (4)Plafond | (0)Logout");
                            String opcaoMenu2Aux = dis.readUTF();

                            if (opcaoMenu2Aux.matches("[0-4]+")) {

                                opcaoMenu2 = opcaoMenu2Aux;
                                OM2 = true;

                            }

                        }
                        switch (opcaoMenu2) {
                            case "1":
                                boolean dataResult = false;
                                while (!dataResult) {

                                    sendEcho("Insira a data limite no formato dd/mm/aaaa e a hora no formato hh:mm:ss AM ou PM");

                                    String dateLimite = dis.readUTF();
                                    String horaLimite = dis.readUTF();


                                    boolean result = hourValida(horaLimite, dateLimite);

                                    if (result) {
                                        sendEcho("A data introduzida e valida");
                                        String nomeAutor = userName;
                                        sendEcho("Insira uma descrição");
                                        String descricao = dis.readUTF();
                                        Leilao novoLeilao = new Leilao(dateLimite, horaLimite, nomeAutor, descricao);
                                        Regulador.adicionarLeilao(novoLeilao);
                                        Regulador.escreverLeilao(novoLeilao);
                                        sendEcho("O seu leilão foi criado com sucesso com ID: " + Leilao.getId());
                                        sendEchoAll("Há um novo leilão disponível, queira consultar os leilões disponíveis.");
                                        dataResult = true;
                                    } else {

                                        sendEcho("A data introduzida e invalida.\n Introduza a data novamente:");
                                    }
                                }
                                break;

                            case "2":
                                Regulador.lerLeiloes();
                                sendEcho(Regulador.escreverLeiloesAtivos());
                                System.out.println(Regulador.escreverLeiloesAtivos());
                                break;
                            case "3":
                                sendEcho("Insira o id do leilão que deseja licitar: ");
                                int idLeilao = -1;
                                Licitador licitadorPlafond = users.get(userName);
                                int plafond = licitadorPlafond.getPlafond();
                                Licitador licitador = new Licitador(userName,passWord,plafond);

                                String idAux = dis.readUTF();

                                if(idAux.matches("[0-9]+")) {
                                    idLeilao = Integer.parseInt(idAux);
                                }else{
                                    sendEcho("Leilao com Id invalido");
                                    break;
                                }


                                if (idLeilao >= 1 && idLeilao <= Integer.parseInt(Leilao.getId())) {
                                    sendEcho("O leilão com ID " + idLeilao + " esta disponivel");
                                    Leilao leilao = Regulador.leilaoComId(String.valueOf(idLeilao));


                                    if (leilao.maiorLicitacao() == 0) {
                                        sendEcho("Ainda não existem licitações");
                                    } else {
                                        sendEcho("Ultima licitação para o leilão com o id " + idLeilao + " foi : " + leilao.maiorLicitacao() + "€");
                                    }
                                    int quantia = 0;

                                    if (leilao.jaLicitou(userName)) {
                                        int quantiaAnterior = leilao.valorLicitacaoDoId(userName);

                                        sendEcho("A sua ultima licitação foi : " + quantiaAnterior + "€\nQuanto quer adicionar:");

                                        String qtd = dis.readUTF();

                                        if (qtd.matches("[0-9]+")) {

                                            quantia = Integer.parseInt(qtd);

                                            if (quantiaAnterior + quantia <= leilao.maiorLicitacao() && quantiaAnterior + quantia <= licitadorPlafond.getPlafond()) {
                                                sendEcho("A sua licitação não foi aceite, o valor proposto não é superior ao máximo atual.");
                                            } else if (quantiaAnterior + quantia > licitadorPlafond.getPlafond()) {
                                                sendEcho("A sua solicitação não foi aceite, o valor da sua proposta é superior ao seu plafond.");
                                            } else {
                                                Licitacao licitacao = new Licitacao(userName, quantia);
                                                leilao.addLicitador(licitacao);
                                                Regulador.atualizaDadosUser(licitador,quantia);
                                                Regulador.atualizaLeiloes(idLeilao, leilao, userName, quantia);
                                                sendEcho("A sua licitação foi aceite. Estanto atualmente em: " + (quantiaAnterior + quantia) + "€");
                                                System.out.println(Regulador.leilaoComId(String.valueOf(idLeilao)).escreverLeilao());
                                            }
                                        }else {
                                            sendEcho("A sua licitacao nao foi aceite, quantidade invalida");
                                            break;
                                        }
                                    }else {
                                        sendEcho("Insira a quantia da licitação:");
                                        quantia = Integer.parseInt(dis.readUTF());
                                        if (quantia <= leilao.maiorLicitacao() && quantia <= licitadorPlafond.getPlafond()) {
                                            sendEcho("A sua licitação não foi aceite, o valor proposto não é superior ao máximo atual.");
                                        } else if (quantia > licitadorPlafond.getPlafond()) {
                                            sendEcho("A sua solicitação não foi aceite, o valor da sua proposta é superior ao seu plafond.");
                                        } else {
                                            Licitacao licitacao = new Licitacao(userName, quantia);
                                            leilao.addLicitador(licitacao);
                                            Regulador.atualizaLeiloes(idLeilao, leilao, userName, 0);
                                            sendEcho("A sua licitação foi aceite.");
                                            sendEchoAll("Foi recebida uma nova licitação no leilão com ID "+idLeilao);
                                        //    System.out.println(Regulador.leilaoComId(String.valueOf(idLeilao)).escreverLeilao());
                                        }
                                    }


                                } else {
                                    sendEcho("O leilão com ID " + idLeilao + " não existe ou já não está disponível.");
                                }


                                break;
                            case "4":
                                Licitador licitadors = users.get(userName);
                                sendEcho("O seu plafond atual é de: " + licitadors.getPlafond() + "€");
                                break;
                            case "0":
                                menuDois = true;
                                validMenu = false;
                                break;
                            default:
                                break;
                        }

                    }
                }

            } catch(IOException e){
                e.printStackTrace();
            }

        }

    }
}