package model;

public class Student implements Comparable<Student> {
    private String id;
    private String name;
    private String programme;
    private int age;
    private double cgpa;

    public Student(String id, String name, String programme, int age, double cgpa) {
        this.id = (id != null) ? id.trim() : "";
        this.name = (name != null) ? name.trim() : "";
        this.programme = (programme != null) ? programme.trim() : "";
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

    @Override 
    public int compareTo(Student other){
        if(other == null){
            return 1;
        }
        return this.name.compareToIgnoreCase(other.name);
    }


    @Override
    public String toString() {
        return String.format("| %-8s | %-16s | %-18s | %-4d | %-5.2f |", 
                id, name, programme, age, cgpa);
    }

    public String toCsv(){
        return String.format("%s,%s,%s,%d,%.2f", id, name, programme, age, cgpa);
    }
}