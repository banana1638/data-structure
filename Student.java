public class Student {
    String id;
    String name;
    String programme;
    int age;
    double cgpa;

    public Student(String id, String name, String programme, int age, double cgpa) {
        this.id = id;
        this.name = name;
        this.programme = programme;
        this.age = age;
        this.cgpa = cgpa;
    }

    public String toString() {
        return "Student [" + id + "]" + name + ", " + programme + ", " + age + ", " + cgpa + "]";
    }
}