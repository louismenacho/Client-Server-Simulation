import java.io.IOException;

public class StudentClient extends Client {

    private int id;

    public StudentClient(int id) throws IOException {
        super();
        this.id = id;

        sendRequest(clientName());
        msg(receiveResponse());

        requests.add("enter");
        for (int i = 0; i < 3; i++) {
            requests.add("takeSeat");
            requests.add("beginExam");
            requests.add("returnExam");
            requests.add("getGrade");
        }
        requests.add("leave");
    }

    public String clientName() {
        return id > 9 ? "StudentClient-" + id : "StudentClient-0" + id;
    }
}
