import java.io.IOException;

public class InstructorClient extends Client {

    public InstructorClient() throws IOException {
        super();

        sendRequest(clientName());
        msg(receiveResponse());

        requests.add("unlock");
        for (int i = 0; i < 4; i++) {
            requests.add("initiateExam");
            requests.add("gradeExams");
        }
        requests.add("leave");
    }

    public String clientName() {
        return "InstructorClient";
    }
}
