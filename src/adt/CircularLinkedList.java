package src.adt;

import model.Node;
import model.Student;

public class CircularLinkedList implements CircularLinkedListADT{
    private Node head;
    private int size;

    public CircularLinkedList(){
        this.head = null;
        this.size = 0;
    }

    @Override 
    public boolean isEmpty(){
        return head == null;
    }

    @Override 
    public int getSize(){
        return size;
    }

    public Node getHead(){
        return head;
    }

    @Override 
    public boolean insertSorted(Student student){
        if(student == null || student.getStudentName().trim().isEmpty()){
            System.out.println("Error: Student record cannot be empty.");
            return false;
        }

        Node newNode = new Node(student);

        if(head == null){
            head = newNode;
            newNode.next = head;
            size++;
            return true;
        }

        if(head.data.getStudentName().equalsTgnoreCase(student.getStudentName())){
            System.out.println("Duplicate rejected: " + student.getStudentName());
            return false;
        }

        if(student.getStudentName().compareToIgnoreCase(head.data.getStudentName()) < 0){
            Node tail = head;
            while(tail.next != head){
                tail = tail.next;
            }
            newNode.next = head; // New node points to old head
            tail.next = newNode; // Tail points to new node
            head = newNode; 
            size++;
            return true;
        }

        Node current = head; 
        while(current.next != head && current.next.data.getStudentName().compareToIgnoreCase(student.getStudentName()) <0){
            current = current.next;
        }

        if(current.next != head && current.next.data.getSudentName().equalsIgnoreCase(student.getStudentName())) {
            System.out.println("Duplicate rejected: " + student.getStudentName());
            return false;
        }

        newNode.next = current.next;
        current.next = newNode;
        size++;
        return true; 
    }



}
