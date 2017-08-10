package examday;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class Instructor {

    public static long time = System.currentTimeMillis();
    private Classroom classroom;
    private TreeMap<Integer, ArrayList> gradeBook = new TreeMap<>(); //record student grades

    public void unlock() {
        try {
            Thread.sleep(4500); //Unlocks at 45 min(4.5 sec), exam 1 starts at 60 minutes(6 sec)
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        classroom.unlock(this);
    }

    public void initiateExam() {
        classroom.initiateExam(this);
    }

    public void gradeExams() {
        classroom.gradeExams(this);
    }

    public void leave() {
        printGradeBook();
        msg("Left classroom");
    }

    /**
     * addGradeBook
     * Adds entry to gradebook
     **/
    public void addGradeBookEntry(Student student, int grade) {
        int examNum = classroom.getExamsCompleted() - 1;
        if (!gradeBook.containsKey(student.id)) {
            ArrayList list = new ArrayList();
            for (int i = 0; i < 4; i++) {
                list.add(0);
            }
            gradeBook.put(student.id, list);
        }
        gradeBook.get(student.id).set(examNum, grade);
    }

    /**
     * printGradeBook
     * formats and prints gradebook to console
     **/
    public void printGradeBook() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("<<<<<<<<<<<<<GRADEBOOK>>>>>>>>>>>>");
        System.out.println("Student ## Grades:\tE1\tE2\tE3\tE4");

        for (Map.Entry<Integer, ArrayList> entry : gradeBook.entrySet()) {
            int student = entry.getKey();
            ArrayList grades = entry.getValue();

            String studentNum = student < 10 ? "0" + student : "" + student;
            System.out.print("Student " + studentNum + " Grades:");
            for (int i = 0; i < grades.size(); i++) {
                int grade = (int) grades.get(i);
                String formattedGrade = (int) grades.get(i) < 10 ? "0" + grade : "" + grade;
                System.out.print("\t" + formattedGrade);
            }
            System.out.println();
        }
    }

    public void assignClassroom(Classroom classroom) {
        this.classroom = classroom;
    }

    public void msg(String m) {
        System.out.println("[" + (System.currentTimeMillis() - time) + "] " + getName() + ": " + m);
    }

    private String getName() {
        return "Instructor-";
    }
}
