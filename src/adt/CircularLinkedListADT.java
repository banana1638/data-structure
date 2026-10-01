package adt;

import model.Student;

public interface CircularLinkedListADT {
    boolean insertSorted(Student student);

    boolean delete(String studentName);

    Student search(String studentName);

    void display();

    CircularLinkedList createReverseCopy();

    boolean isEmpty();

    int getSize();
}
