import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private ServerSocket serverSocket;

    public Server(int portNumber) throws IOException {
        System.out.println("Server socket created on port " + portNumber);
        serverSocket = new ServerSocket(portNumber);

        while (true) {
            Socket clientSocket = serverSocket.accept();
            ClientHelper clientHelper = new ClientHelper(clientSocket);
            clientHelper.start();
        }
    }

    public static void main(String args[]) {
        try {
            int portNumber = Integer.parseInt(args[0]);
            new Server(portNumber);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}