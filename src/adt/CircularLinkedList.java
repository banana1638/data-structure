package adt;

import model.Node;
import model.Student;

public class CircularLinkedList implements CircularLinkedListADT {
    private Node head;
    private Node tail;
    private int size;

    public CircularLinkedList() {
        this.head = null;
        this.tail = null;
        this.size = 0;
    }

    @Override
    public boolean isEmpty() {
        return head == null;
    }

    @Override
    public int getSize() {
        return size;
    }

    public Node getHead() {
        return head;
    }

    @Override
    public boolean insertSorted(Student student) {
        if (student == null || student.getName().trim().isEmpty()) {
            System.out.println("Error: Student record cannot be empty.");
            return false;
        }

        Node newNode = new Node(student);

        if (head == null) {
            head = newNode;
            tail = newNode;
            newNode.link = head;
            size++;
            return true;
        }

        if (head.data.getName().equalsIgnoreCase(student.getName())) {
            System.out.println("Duplicate rejected: " + student.getName());
            return false;
        }

        if (student.getName().compareToIgnoreCase(head.data.getName()) < 0) {
            newNode.link = head; // New node points to old head
            tail.link = newNode; // Tail points to new node
            head = newNode;
            size++;
            return true;
        }

        Node current = head;
        while (current.link != head
                && current.link.data.getName().compareToIgnoreCase(student.getName()) < 0) {
            current = current.link;
        }

        if (current.link != head && current.link.data.getName().equalsIgnoreCase(student.getName())) {
            System.out.println("Duplicate rejected: " + student.getName());
            return false;
        }

        newNode.link = current.link;
        current.link = newNode;
        size++;
        return true;
    }

}
