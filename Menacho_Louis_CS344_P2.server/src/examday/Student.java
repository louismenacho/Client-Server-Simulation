package examday;

import java.util.Random;

public class Student {

    public static long time = System.currentTimeMillis();
    public int id;
    public int tableNum; //table sat at
    public int exam[]; //exam grades
    private Classroom classroom;

    public Student(int id) {
        this.id = id;
        exam = new int[4];
    }

    public void enter() {
        try {
            Thread.sleep(new Random().nextInt((6000)) + 2000); // arrive between 20 - 80 minutes
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        classroom.enter(this);
    }

    public void takeSeat() {
        classroom.takeSeat(this);
    }

    public void beginExam() {
        classroom.beginExam(this);
    }

    public void returnExam() {
        classroom.returnExam(this);
    }

    public void getGrade() {
        classroom.getGrade(this);
    }

    public void leave() {
        classroom.leave(this);
    }

    public boolean hasTableAlready() {
        return tableNum > 0;
    }

    public void assignClassroom(Classroom classroom) {
        this.classroom = classroom;
    }

    public void msg(String m) {
        System.out.println("[" + (System.currentTimeMillis() - time) + "] " + getName() + ": " + m);
    }

    public String getName() {
        return id > 9 ? "Student-" + id : "Student-0" + id;
    }
}
