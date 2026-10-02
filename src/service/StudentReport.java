package service;

import adt.CircularLinkedList;
import model.Node;
import model.Student;

public class StudentReport {

    public static void generateReport(CircularLinkedList list) {
        System.out.println("\n==================================================");
        System.out.println("           STUDENT PERFORMANCE REPORT             ");
        System.out.println("==================================================");

        if (list == null || list.isEmpty()) {
            System.out.println("No records found in the list to generate report.");
            System.out.println("==================================================\n");
            return;
        }

        int count = list.getSize();
        double sumCgpa = 0.0;

        Node headNode = list.getHead();
        Node tailNode = list.getTail();

        Student firstStudent = headNode.data;
        Student lastStudent = tailNode.data;

        double maxCgpa = headNode.data.getCgpa();
        double minCgpa = headNode.data.getCgpa();
        Student highestStudent = headNode.data;
        Student lowestStudent = headNode.data;

        int deansListCount = 0; // CGPA >= 3.75
        int firstClassCount = 0; // 3.50 <= CGPA < 3.75
        int secondClassCount = 0; // 3.00 <= CGPA < 3.50
        int passCount = 0; // CGPA < 3.00

        Node current = headNode;
        do {
            Student s = current.data;
            double cgpa = s.getCgpa();
            sumCgpa += cgpa;

            if (cgpa > maxCgpa) {
                maxCgpa = cgpa;
                highestStudent = s;
            }

            if (cgpa < minCgpa) {
                minCgpa = cgpa;
                lowestStudent = s;
            }

            if (cgpa >= 3.75) {
                deansListCount++;
            } else if (cgpa >= 3.50) {
                firstClassCount++;
            } else if (cgpa >= 3.00) {
                secondClassCount++;
            } else {
                passCount++;
            }

            current = current.link;
        } while (current != headNode);

        double avgCgpa = sumCgpa / count;

        System.out.printf("Total Number of Records : %d\n", count);
        System.out.printf("First Record (Sorted)   : %s (ID: %s)\n", firstStudent.getName(), firstStudent.getId());
        System.out.printf("Last Record (Sorted)    : %s (ID: %s)\n", lastStudent.getName(), lastStudent.getId());
        System.out.println("--------------------------------------------------");
        System.out.printf("Cohort Average CGPA     : %.2f\n", avgCgpa);
        System.out.printf("Highest CGPA in Cohort  : %.2f (by %s)\n", maxCgpa, highestStudent.getName());
        System.out.printf("Lowest CGPA in Cohort   : %.2f (by %s)\n", minCgpa, lowestStudent.getName());
        System.out.println("--------------------------------------------------");
        System.out.println("Academic Performance Summary:");
        System.out.printf(" • Dean's List (CGPA >= 3.75)       : %d student(s)\n", deansListCount);
        System.out.printf(" • First Class (3.50 <= CGPA < 3.75): %d student(s)\n", firstClassCount);
        System.out.printf(" • Second Upper (3.00 <= CGPA < 3.5): %d student(s)\n", secondClassCount);
        System.out.printf(" • Pass / Others (CGPA < 3.00)      : %d student(s)\n", passCount);
        System.out.println("==================================================\n");
    }
}
