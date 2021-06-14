import java.io.IOException;
import java.net.SocketException;

public class Main {


    public static void main(String[] args) throws IOException {
        ClienteTcp cliente = new ClienteTcp();
        cliente.cliente(args[0],7810);
    }
}
