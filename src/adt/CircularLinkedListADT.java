package adt;

import model.Student;

public interface CircularLinkedListADT {
    boolean addStudent(String name, String programme, int age, double cgpa);

    String getNextStudentId();

    boolean insertSorted(Student student);

    boolean delete(String studentName);

    Student search(String studentName);

    void display();

    CircularLinkedList createReverseCopy();

    boolean isEmpty();

    int getSize();
}
