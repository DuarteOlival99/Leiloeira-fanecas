import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.*;
import java.util.Scanner;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicReference;
// Connectar ao porto 6500 de um servidor especifico,
// envia uma mensagem e imprime resultado,

public class ClienteTcp  {
    private int portoCliente;
    private DatagramSocket socketUdp;
    String mensagem = "";

    public ClienteTcp() throws SocketException {
    }

    public int getPortoCliente() {
        return portoCliente;
    }
    public void setPortoCliente(String portoCliente) {
        this.portoCliente = Integer.parseInt(portoCliente);
    }

    public String recebeUdp(){
        String received = "";
        try {
            byte[] buf = new byte[16384];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            socketUdp.receive(packet);

            InetAddress address = packet.getAddress();
            int port = packet.getPort();
            packet = new DatagramPacket(buf, buf.length, address, port);
            received = new String(packet.getData(), 0, packet.getLength());

        } catch (IOException e) {
            e.printStackTrace();
        }

        return received;
    }



    public void cliente(String ip,int portoUsado) throws IOException  {
        Scanner scanner = new Scanner(System.in); // cria o scanner para poder ser lido do ecra o que o utilizador escrever
        Porto portoParaReceber = new Porto();
        int porto = portoParaReceber.escolhePorto();
      //  System.out.println("Porto envidado: "+porto);
        socketUdp= new DatagramSocket(porto);
        Socket socket = new Socket(ip, portoUsado);
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        DataInputStream dis = new DataInputStream(socket.getInputStream());
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

        dos.writeUTF(String.valueOf(porto));

        // dos.writeUTF("Porto: " + getPortoCliente());//envia o porto a ser utilizado no servidor UDP (Cliente) para o servidor TCP em que
        //sera usado no UDP "cliente" do servidor para mandar informacao para o cliente

    /*
        while (true){
            (Thread) run() -> {

                while (true){
                    String mensage = recebeUdp();
                }

            }.start();
            String userInput = scanner.nextLine();

        }
        */

        boolean validMenu = false;
        boolean menuDois = false;

        while (true) {

            new Thread(() -> {
                while (true){

                    String message = recebeUdp();
                    System.out.println(message);
                    mensagem = message;
                }
            }).start();

            boolean programa = false;
            while (!programa) {

                while (!validMenu) {

                    //System.out.println(recebeUdp());
                    //System.out.println(dis.readUTF());//recebo o que o servidor envia ao se ligar
                    String cmd = "a";
                    while (!cmd.equals("1") && !cmd.equals("2") && !cmd.equals("3") && !cmd.equals("Turn Off")) {
                        cmd = scanner.nextLine();//leio o q foi escrito pelo utilizador
                        dos.writeUTF(cmd);//envio a resposta do utilizador

                        //System.out.println(dis.readUTF());// recebo resposta ao valor introduzido
//                        System.out.println(recebeUdp());
                    }


                    if (cmd.equals("1")) {


                        boolean validAutentication = false;

                        while (!validAutentication) {
//                            System.out.println(recebeUdp());
                            String username = scanner.nextLine(); // escrevo username
                            dos.writeUTF(username); // envio username

                            //String msg = mensagem; // recebe a resposta do servidor a pedir a pass
                            // System.out.println(msg);
                            cmd = scanner.nextLine(); // escrever pass
                            dos.writeUTF(cmd); // envio Pass


                            //  msg = recebeUdp(); // recebe a dizer que o utilizador .... foi adicionado com sucesso
                            //  System.out.println(msg);


                              if (mensagem.startsWith("Login efectuado com sucesso")) {// .startsWith -> ate encontrar  '\u0000' que representa em unicode 0 |
                            // faz o .equal ate encontrar um '|u0000'
                                    validMenu = true;
                                    validAutentication = true;
                                    menuDois = false;
                              }

                        }
                    } else if (cmd.equals("2")) {
                        boolean validAutentication = false;

                        while (!validAutentication) {

                            String username = scanner.nextLine(); // escrevo username
                            dos.writeUTF(username); // envio username

                            // String msg = recebeUdp(); // recebe a resposta do servidor a pedir a pass
                            // System.out.println(msg);

                            cmd = scanner.nextLine(); // escrev
                            dos.writeUTF(cmd); // envio Pass


                            // msg = recebeUdp(); // recebe a dizer que o utilizador .... foi adicionado com sucesso
                            // System.out.println(msg);

                            if (mensagem.startsWith(username + " adicionado com sucesso.")) {
                                validMenu = true;
                                validAutentication = true;
                                menuDois = false;
                            } else {
//                                System.out.println(recebeUdp());
                            }
                        }
                    }
                    if (cmd.startsWith("Turn Off") || cmd.startsWith("3")) {
//                        System.out.println(recebeUdp());
                        socket.close();
                        validMenu = true;
                        programa = true;
                        menuDois = true;
                        break;
                    }
                    // imprime resposta do servidor
                    //System.out.println("Recebido: " + dis.readUTF());
                }


                while (!menuDois) {

                    String cmd = "a";
                    String opcaoMenu2 = "";
                    boolean OM2 = false;
                    while (!OM2) {

//                        System.out.println(recebeUdp());

                        cmd = scanner.nextLine(); // escrevo opcao
                        dos.writeUTF(cmd); // envio opcao

                        if(cmd.matches("[0-4]+")){

                            opcaoMenu2 = cmd;
                            OM2 = true;

                        }


                    }


                    switch (opcaoMenu2) {

                        case "1":
                            boolean dataResult = false;
                            while (!dataResult) {
//                                System.out.println(recebeUdp());
                                cmd = scanner.nextLine(); // escrevo data
                                dos.writeUTF(cmd); // envio data
                                cmd = scanner.nextLine(); // escrevo hora
                                dos.writeUTF(cmd);//envio hora

                                //  String result = recebeUdp();

                                if (mensagem.startsWith("A data introduzida e valida")) {
//                                    System.out.println(recebeUdp());
                                    cmd = scanner.nextLine(); // escrevo descricao
                                    dos.writeUTF(cmd); // envio descricao

//                                    System.out.println(recebeUdp());// recebo msg a dizer que leilao foi criado com sucesso
                                    dataResult = true;
                                } else {
                                    //System.out.println("A data introduzida e invalida.\n Introduza a data novamente:");
                                    // System.out.println(recebeUdp());
                                }
                            }
                            break;
                        case "2":
//                            System.out.println(recebeUdp());
                            break;
                        case "3":
//                            System.out.println(recebeUdp());
                            cmd = scanner.nextLine(); // escrevo idLeilao
                            dos.writeUTF(cmd); // envio idLeilao

                            String idAux = cmd;

                            // cmd = recebeUdp();
                            //  System.out.println(cmd);

                            if(mensagem.startsWith("O leilão com ID " + idAux + " esta disponivel")) {
//                                System.out.println(recebeUdp());

//                                System.out.println(recebeUdp());
                                cmd = "";
                                while (cmd.equals("")) {
                                    cmd = scanner.nextLine(); // escrevo qtd
                                    System.out.println("quantida invalida! Introduza de novo:");
                                }
                                dos.writeUTF(cmd); // envio qtd
//                                System.out.println(recebeUdp());
                            }

                            break;
                        case "4":
//                            System.out.println(recebeUdp());
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

            socket.close();

        }

    }
    // termina socket



    // usage: java EchoClient <servidor> <mensagem>
    public static void main(String args[]) throws Exception {

    }

}
