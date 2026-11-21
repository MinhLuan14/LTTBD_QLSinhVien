package vhu.edu.edusync.data.model;

public class GradeItem {
    private final String subjectName;
    private final String grade;
    private final String credits;

    public GradeItem(String subjectName, String grade, String credits) {
        this.subjectName = subjectName;
        this.grade = grade;
        this.credits = credits;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public String getGrade() {
        return grade;
    }

    public String getCredits() {
        return credits;
    }
}
