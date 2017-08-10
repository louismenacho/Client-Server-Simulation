import java.io.IOException;

public class TimerClient extends Client {

    public TimerClient() throws IOException {
        super();

        sendRequest(clientName());
        sendRequest(capacity+"");
        sendRequest(numSeats+"");
        msg(receiveResponse());

        requests.add("printScheduledExams");
        for (int i = 0; i < 4; i++) {
            requests.add("notifyStartExam");
            requests.add("notifyEndExam");
            requests.add("incrementExamNum");
        }
    }

    public String clientName() {
        return "TimerClient";
    }
}
