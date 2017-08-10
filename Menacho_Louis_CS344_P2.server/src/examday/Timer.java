package examday;

public class Timer {

    public static long time = System.currentTimeMillis();
    private Classroom classroom;
    private int examNum = 1;

    public Timer () {
        msg("It is 12:00 pm now");
    }

    public void printScheduledExams() {
        for (int i = 1; i <= 4; i++) {
            msg("Exam "+ i + " scheduled at "+getTime(i)+" (at "+ (i*12000-6000) +" ms)");
        }
    }

    public void notifyStartExam() {
        try {
            Thread.sleep(6000); // 1 hour for next exam
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        msg("Exam "+ examNum + " has begun");
        classroom.notifyStartExam();
    }

    public void notifyEndExam() {
        try {
            Thread.sleep(6000); // 1 hour exam duration
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        msg("Exam "+ examNum + " has ended");
        classroom.notifyEndExam();
    }

    public void incrementExamNum() {
        examNum++;
    }

    private String getTime(int i) {
        switch (i) {
            case 1: return "01:00 pm";
            case 2: return "03:00 pm";
            case 3: return "05:00 pm";
            case 4: return "07:00 pm";
            default : break;
        }
        return "";
    }

    public void assignClassroom(Classroom classroom) {
        this.classroom = classroom;
    }

    private void msg(String m) {
        System.out.println("["+(System.currentTimeMillis()-time)+"] "+getName()+": "+m);
    }

    private String getName() {
        return "Timer-";
    }


}
