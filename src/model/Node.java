package src.model;

public class Node {
    public Student data;
    public Node link;

    public Node(Student data) {
        this.data = data;
        this.link = null;
    }
}
