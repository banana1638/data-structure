package model;

public class Student {
    private String id;
    private String name;
    private String programme;
    private int age;
    private double cgpa;

    public Student(String id, String name, String programme, int age, double cgpa) {
        this.id = id;
        this.name = name;
        this.programme = programme;
        this.age = age;
        this.cgpa = cgpa;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getProgramme() {
        return programme;
    }

    public int getAge() {
        return age;
    }

    public double getCgpa() {
        return cgpa;
    }

    public String toString() {
        return "Student [" + id + "] " + name + ", " + programme + ", " + age + ", " + cgpa;
    }
}