package examday;

import java.util.Random;
import java.util.Vector;

public class Classroom {

    private int numSeatsPerTable;
    private int capacity;

    private int numSeats[]; //current number of numSeats available at table(index)
    private int numStudents; //current number of students in classroom
    private int examsCompleted; //total number of exams completed

    private boolean isUnlocked; //is door unlocked for classroom?
    private boolean isTesting; //is classroom currently testing? (exam in session?)

    private Vector<Object> waitingStudents = new Vector(); //used to notify in FCFS for students waiting grades
    private Vector<Object> waitingGrades = new Vector(); //used to notify in FCFS for students waiting grades
    private Vector<Object> tables = new Vector(); //used to store each table object students wait on
    private Object entrance = new Object(); //students will wait on this object before entering classroom
    private Object examStart = new Object(); //student & instructors will wait on this until exam starts
    private Object examOver = new Object(); //student & instructors will wait on this until exam ends
    private Object gradeExam = new Object(); //instructor will wait on this before he grades any exam

    public Classroom(int numSeats, int capacity) {
        this.numSeatsPerTable = numSeats;
        this.capacity = capacity;
        tables.addElement(new Object()); //add table object
        this.numSeats = new int[capacity / numSeats + 1]; //calculate max number of tables required
        this.numSeats[0] = numSeatsPerTable; //initialize first table with seats
    }

    /**
     * TIMER METHODS
     **/
    public void notifyStartExam() {
        isTesting = true;
        synchronized (examStart) {
            examStart.notify();
        }
    }

    public void notifyEndExam() {
        isTesting = false;
        synchronized (examOver) {
            examOver.notifyAll();
        }
        examsCompleted++;
    }

    /**STUDENT METHODS**/

    /**
     * enter
     * Simulates students attempting to enter classroom
     **/
    public void enter(Student student) {
        synchronized (entrance) {
            while (cannotEnterNow()) { //after instructor unlocks classroom, must recheck capacity
                student.msg(giveReason() + " waits in front of classroom");
                while (true) try {
                    entrance.wait(); //wait for instructor to unlock classroom (notify)
                    break;
                } catch (InterruptedException e) {
                    continue;
                }
            }
        }
        student.msg("Entered classroom");
    }

    /**
     * cannotEnterNow
     * Check three conditions before students can enter classroom
     * If allowed to pass, increment number of students
     **/
    private synchronized boolean cannotEnterNow() {
        if (!isUnlocked || isTesting || capacity == numStudents) { //three conditions to enter classroom
            return true;
        }
        numStudents++; //increase number of students in classroom
        return false;
    }

    /**
     * takeSeat
     * Simulates students taking a seat after entering classroom
     **/
    public void takeSeat(Student student) {
        Object table;
        if (student.hasTableAlready()) {
            table = tables.get(student.tableNum - 1);
        } else {
            table = findTable(student);
        }
        synchronized (table) {
            while (true) try {
                table.wait(); //waits at table for exam to begin
                break;
            } catch (InterruptedException e) {
                continue;
            }
        }
    }

    /**
     * findTable
     * Creates tables as students enter classroom
     * Created tabled are stored in tables(Vector)
     * numSeats keeps track of available seats left at table(index)
     * numSeats.length (calculated in constructor) determines max num. of tables
     **/
    private synchronized Object findTable(Student student) {
        int openTable = 0; //table with available seats
        if (tables.size() == numSeats.length) { //if reached max num. of tables
            for (int i = 0; i < numSeats.length; i++) { //looks for available seats
                if (numSeats[i] > 0) { //if numSeats at table i greater than 0
                    numSeats[i]--;
                    openTable = i;
                    break;
                }
            }
        } else {
            openTable = tables.size() - 1; //assign openTable the last table
            numSeats[openTable]--; //takes one seat
            if (numSeats[openTable] == 0) { //if no seats left
                int nextTable = openTable + 1;
                numSeats[nextTable] = numSeatsPerTable;
                tables.addElement(new Object()); //add new table
            }
        }
        student.tableNum = openTable + 1;
        student.msg("Sat in table " + (openTable + 1));
        return tables.get(openTable);
    }

    /**
     * beginExam
     * Simulates student taking exam
     **/
    public void beginExam(Student student) {
        student.msg("Began exam");
        synchronized (examOver) {
            try {
                examOver.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * returnExam
     * Simulates student checking their notes and then returning exam(notify instructor)
     **/
    public void returnExam(Student student) {
        try {
            student.msg("Is checking their notes");
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        synchronized (gradeExam) {
            gradeExam.notify();
            student.msg("Returned exam");
        }
    }

    /**
     * getGrade
     * Simulates student receiving grade
     * Must wait for instructor to finish grading first
     **/
    public void getGrade(Student student) {
        Object grade = new Object();
        synchronized (grade) {
            while (true) try {
                waitingStudents.addElement(student); //added to access student object
                waitingGrades.addElement(grade); //students will wait in FCFS fashion
                grade.wait();
                break;
            } catch (InterruptedException e) {
                continue;
            }
        }
        student.msg("Got grade: " + student.exam[examsCompleted - 1]);
    }

    /**
     * leave
     * Simulates student leaving classroom
     * Upon leaving student will notify other students waiting at entrance
     **/
    public void leave(Student student) {
        student.msg("Left classroom");
        synchronized (entrance) {
            numStudents--;
            numSeats[student.tableNum - 1]++; //student gives up seat
            entrance.notify();
        }
    }

    /**INSTRUCTOR METHODS**/

    /**
     * unlock
     * Simulates instructor unlocking classroom door and allowing waiting students inside
     **/
    public void unlock(Instructor instructor) {
        instructor.msg("Unlocked the classroom, exam will begin in 15 min(1.5 sec)");
        isUnlocked = true;
        synchronized (entrance) {
            entrance.notifyAll();
        }
    }

    /**
     * initiateExam
     * Simulates instructor initiating exam session
     * When its time to start exam, will notify all tables
     * Then waits for exam to be over
     **/
    public void initiateExam(Instructor instructor) {
        synchronized (examStart) {
            while (true) try {
                examStart.wait();
                break;
            } catch (InterruptedException e) {
                continue;
            }
        }

        instructor.msg("Handed out the exams");
        for (int i = 0; i < tables.size(); i++) {
            synchronized (tables.elementAt(i)) {
                tables.elementAt(i).notifyAll();
            }
        }

        synchronized (examOver) {
            while (true) try {
                examOver.wait();
                break;
            } catch (InterruptedException e) {
                continue;
            }
        }
    }

    /**
     * gradeExams
     * Simulates instructor grading exams completed during the previous exam session
     **/
    public void gradeExams(Instructor instructor) {
        int examsCompleted = numStudents;
        int examsGraded = 0;

        synchronized (gradeExam) {
            while (true) try {
                gradeExam.wait(); //wait for student to return an exam(notify)
                break;
            } catch (InterruptedException e) {
                continue;
            }
        }

        while (examsGraded != examsCompleted) {
            if (!waitingStudents.isEmpty()) {
                try {
                    instructor.msg("Is checking an exam...");
                    Thread.sleep(200);
                    examsGraded++;
                    instructor.msg("Graded an exam (" + examsGraded + ")");
                    assignGrade(instructor);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        synchronized (entrance) {
            entrance.notifyAll();
        }

    }

    /**
     * assignGrade
     * Simulates instructor assigning a grade
     * Assigns random grade and then removes an object from queue
     **/
    private void assignGrade(Instructor instructor) {
        int grade = new Random().nextInt(101);
        Student student;
        student = (Student) waitingStudents.remove(0);
        student.exam[examsCompleted - 1] = grade;
        instructor.addGradeBookEntry(student, grade);

        synchronized (waitingGrades.elementAt(0)) {
            waitingGrades.elementAt(0).notify();
        }
        waitingGrades.removeElementAt(0);

    }

    /**
     * OTHER METHODS
     **/

    public int getExamsCompleted() {
        return examsCompleted;
    }

    /**
     * Used in instructor class
     * Determines if all exams have been administered
     **/
    public boolean hasCompletedAllExams() {
        return examsCompleted == 4;
    }

    /**
     * Used in enter() method
     * One of three reasons for being unable to enter classroom will be stated by student
     **/
    private String giveReason() {
        if (!isUnlocked) {
            return "Door is locked,";
        } else if (isTesting) {
            return "Missed the exam,";
        } else {
            return "Capacity reached,";
        }
    }
}
