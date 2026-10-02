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

    public Node getTail() {
        return tail;
    }

    private static boolean isValidName(String name) {
        return name != null && !name.trim().isEmpty();
    }

    public static int compareNames(String name1, String name2) {
        return name1.compareToIgnoreCase(name2);
    }

    @Override
    public boolean insertSorted(Student student) {
        if (student == null || !isValidName(student.getName())) {
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

        if (compareNames(student.getName(), head.data.getName()) < 0) {
            newNode.link = head; // New node points to old head
            tail.link = newNode; // Tail points to new node
            head = newNode;
            size++;
            return true;
        }

        Node current = head;
        while (current.link != head && current.link.data.getName().compareToIgnoreCase(student.getName()) < 0) {
            current = current.link;
        }

        if (current.link != head && current.link.data.getName().equalsIgnoreCase(student.getName())) {
            System.out.println("Duplicate rejected: " + student.getName());
            return false;
        }

        newNode.link = current.link;
        current.link = newNode;

        if (current == tail) {
            tail = newNode;
        }
        size++;
        return true;
    }

    @Override
    public boolean delete(String studentName) {
        if (isEmpty()) {
            System.out.println("Delete failed: list is empty.");
            return false;
        }

        if (!isValidName(studentName)) {
            System.out.println("Delete failed: invalid name.");
            return false;
        }

        String target = studentName.trim();

        // if target is the head node
        if (head.data.getName().equalsIgnoreCase(target)) {
            if (head == tail) {
                head = null;
                tail = null;
            } else {
                head = head.link;
                tail.link = head;
            }
            size--;
            return true;
        }

        //if target is not the head node, traverse the list to find the target
        Node prev = head;
        while (prev.link != head) {
            int cmp = compareNames(target, prev.link.data.getName());
            if (cmp == 0) {
                Node toDelete = prev.link;
                prev.link = toDelete.link;
                if (toDelete == tail) {
                    tail = prev;
                }
                size--;
                return true;
            }
            if (cmp < 0) {
                break;
            }
            prev = prev.link;
        }
        System.out.println("Delete failed: '" + studentName + "' not found.");
        return false;
    }

    @Override
    public Student search(String studentName) {
        if (isEmpty() || !isValidName(studentName)) {
            return null;
        }

        String target = studentName.trim();
        Node current = head;
        do {
            int cmp = compareNames(target, current.data.getName());
            if (cmp == 0) {
                return current.data;
            }
            if (cmp < 0) {
                break;
            }
            current = current.link;
        } while (current != head);

        return null;
    }

    @Override
    public void display() {
        if (isEmpty()) {
            System.out.println("List is empty.");
            return;
        }

        System.out.println("+----------+------------------+--------------------+------+-------+");
        System.out.println("| ID       | Student Name     | Programme          | Age  | CGPA  |");
        System.out.println("+----------+------------------+--------------------+------+-------+");
        Node current = head;
        do {
            System.out.println(current.data);
            current = current.link;
        } while (current != head);

        System.out.println("+----------+------------------+--------------------+------+-------+");
        System.out.println("Total records: " + size + " (Circular loop verified: tail points to " + tail.link.data.getName() + ")");
    }

    // Helper
    private void insertFront(Student student){
        Node newNode = new Node(student);
        if (head == null){
            head = newNode;
            tail = newNode;
            newNode.link = head;
        } else{
            newNode.link = head;
            tail.link = newNode;
            head = newNode;
        }
        size++;
    }

    @Override
    public CircularLinkedList createReverseCopy() {
        CircularLinkedList reversed = new CircularLinkedList();
        if (isEmpty()) {
            return reversed;
        }

        Node current = head;
        do {
            Student s = current.data;
            Student copy = new Student(s.getId(), s.getName(), s.getProgramme(), s.getAge(), s.getCgpa());
            reversed.insertFront(copy);
            current = current.link;
        } while (current != head);

        return reversed;
    }

}
