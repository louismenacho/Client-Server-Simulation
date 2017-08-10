import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public abstract class Client extends Thread {

    protected static String HOST;
    protected static int PORT;

    protected Socket socket;
    protected BufferedReader in;
    protected PrintWriter out;
    protected ArrayList<String> requests;

    public Client() throws IOException {
        socket = new Socket(HOST, PORT);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
        requests = new ArrayList<>();
    }

    protected void sendRequest(String message) {
        out.println(message);
    }

    protected String receiveResponse() throws IOException {
        return in.readLine();
    }

    protected void msg(String m) {
        System.out.println((clientName()) + ": " + m);
    }

    protected abstract String clientName();

    @Override
    public void run() {
        try {
            String methodName = "";
            String response = "";
            while (!requests.isEmpty()) {
                methodName = requests.remove(0);
                sendRequest(methodName);
                response = receiveResponse();
                msg("Server " + response);
                if (response.equals("executed leave")) {
                    break;
                }
            }
            sendRequest("exit");
            socket.close();
            in.close();
            out.close();
            msg("terminated");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected static int numStudents;
    protected static int capacity;
    protected static int numSeats;

    public static void main(String args[]) {

        HOST = args[0];
        PORT = Integer.parseInt(args[1]);

        numStudents = Integer.parseInt(args[2]); //number of students in simulation
        capacity = Integer.parseInt(args[3]); //classroom capacity
        numSeats = Integer.parseInt(args[4]); //number of seats per table

        try {
            TimerClient timerClient = new TimerClient();
            timerClient.start();

            InstructorClient instructorClient = new InstructorClient();
            instructorClient.start();

            for (int i = 0; i < numStudents; i++) {
                StudentClient studentClient = new StudentClient(i + 1);
                studentClient.start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}