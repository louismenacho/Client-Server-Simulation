import examday.Classroom;
import examday.Instructor;
import examday.Student;
import examday.Timer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHelper extends Thread {

    private static final String STUDENT = "student";
    private static final String INSTRUCTOR = "instructor";
    private static final String TIMER = "timer";
    private static Classroom classroom;

    private Timer timer;
    private Instructor instructor;
    private Student student;
    private static volatile int studentID;

    private String clientName;
    private String clientType;

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public ClientHelper(Socket clientSocket) throws IOException {
        socket = clientSocket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
    }

    public void sendResponse(String message) {
        out.println(message);
    }

    public String receiveRequest() throws IOException {
        return in.readLine();
    }

    @Override
    public void run() {
        try {
            clientName = receiveRequest();
            clientType = getClientType();
            sendResponse("Welcome to ExamDay.");

            switch (clientType) {
                case "timer":
                    int capacity = Integer.parseInt(receiveRequest());
                    int numSeats = Integer.parseInt(receiveRequest());
                    classroom = new Classroom(numSeats,capacity);
                    studentID = 0;
                    timer = new Timer();
                    timer.assignClassroom(classroom);
                    break;
                case "instructor":
                    instructor = new Instructor();
                    instructor.assignClassroom(classroom);
                    break;
                case "student":
                    student = new Student(++studentID);
                    student.assignClassroom(classroom);
                    break;
            }

            String request;
            while ((request = receiveRequest()) != null) {
                if(request.equals("exit")) {
                    break;
                }
                executeMethod(clientType, request);
            }
            socket.close();
            in.close();
            out.close();
            System.out.println("ClientHelper for "+clientName+" terminated");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void executeMethod(String clientType, String methodName) {

        if (clientType.equals(TIMER)) {
            sendResponse("executed " + methodName);
            switch (methodName) {
                case "printScheduledExams":
                    timer.printScheduledExams();
                    return;
                case "notifyStartExam":
                    timer.notifyStartExam();
                    return;
                case "notifyEndExam":
                    timer.notifyEndExam();
                    return;
                case "incrementExamNum":
                    timer.incrementExamNum();
                    return;
            }
        } else if (clientType.equals(INSTRUCTOR)) {
            sendResponse("executed " + methodName);
            switch (methodName) {
                case "unlock":
                    instructor.unlock();
                    return;
                case "initiateExam":
                    instructor.initiateExam();
                    return;
                case "gradeExams":
                    instructor.gradeExams();
                    return;
                case "leave":
                    instructor.leave();
                    return;
            }
        } else if (clientType.equals(STUDENT)) {
            if (classroom.hasCompletedAllExams() && methodName.equals("takeSeat")) {
                sendResponse("executed leave");
                student.leave();
                return;
            }
            sendResponse("executed " + methodName);
            switch (methodName) {
                case "enter":
                    student.enter();
                    return;
                case "takeSeat":
                    student.takeSeat();
                    return;
                case "beginExam":
                    student.beginExam();
                    return;
                case "returnExam":
                    student.returnExam();
                    return;
                case "getGrade":
                    student.getGrade();
                    return;
                case "leave":
                    student.leave();
                    return;
            }
        }
    }

    private String getClientType() {
        String type;
        int i = clientName.indexOf("C");
        type = clientName.substring(0, i);
        return type.toLowerCase();
    }
}
