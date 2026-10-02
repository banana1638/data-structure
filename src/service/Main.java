package service;

import adt.CircularLinkedList;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import model.Node;
import model.Student;

public class Main {
    private static final String FILE_NAME = "students.csv";
    private static final Scanner scanner = new Scanner(System.in);
    private static final CircularLinkedList list = new CircularLinkedList();

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("  STUDENT RECORD MANAGEMENT SYSTEM (CLL ADT)      ");
        System.out.println("==================================================");

        // Auto load existing records
        loadFromFile();

        boolean running = true;
        while (running) {
            displayMenu();
            System.out.print("Enter your choice (1 - 8): ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    addRecord();
                    break;
                case "2":
                    deleteRecord();
                    break;
                case "3":
                    searchRecord();
                    break;
                case "4":
                    System.out.println("\n--- Current Records (Ascending Sorted) ---");
                    list.display();
                    break;
                case "5":
                    displayReverseCopy();
                    break;
                case "6":
                    StudentReport.generateReport(list);
                    break;
                case "7":
                    saveToFile();
                    break;
                case "8":
                    System.out.print("Save changes before exit? (Y/N): ");
                    String ans = scanner.nextLine().trim();
                    if (ans.equalsIgnoreCase("Y")) {
                        saveToFile();
                    }
                    System.out.println("Thank you for using the system. Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid selection. Please enter a number between 1 and 8.");
            }
        }
        scanner.close();
    }

    private static void displayMenu() {
        System.out.println("\n----------------- MAIN MENU -----------------");
        System.out.println("1. Add New Student Record");
        System.out.println("2. Delete Student Record");
        System.out.println("3. Search Student Record");
        System.out.println("4. View All Records (Sorted Order)");
        System.out.println("5. Create & Display Reverse Copy");
        System.out.println("6. Generate Student Performance Report");
        System.out.println("7. Save Records to File (students.csv)");
        System.out.println("8. Exit Application");
        System.out.println("---------------------------------------------");
    }

    private static void addRecord() {
        System.out.println("\n--- Add Student Record ---");
        System.out.print("Enter Student ID: ");
        String id = scanner.nextLine().trim();
        if (id.isEmpty()) {
            System.out.println("Error: Student ID cannot be blank.");
            return;
        }

        System.out.print("Enter Student Name: ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) {
            System.out.println("Error: Student Name cannot be blank.");
            return;
        }

        System.out.print("Enter Programme (e.g. CS, SE, IT): ");
        String programme = scanner.nextLine().trim();

        System.out.print("Enter Age: ");
        int age;
        try {
            age = Integer.parseInt(scanner.nextLine().trim());
            if (age <= 0) {
                System.out.println("Error: Age must be a positive integer.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Error: Invalid number format for Age.");
            return;
        }

        System.out.print("Enter CGPA (0.00 - 4.00): ");
        double cgpa;
        try {
            cgpa = Double.parseDouble(scanner.nextLine().trim());
            if (cgpa < 0.00 || cgpa > 4.00) {
                System.out.println("Error: CGPA must be between 0.00 and 4.00.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Error: Invalid number format for CGPA.");
            return;
        }

        boolean success = list.insertSorted(new Student(id, name, programme, age, cgpa));
        if (success) {
            System.out.println("Success: Record for " + name + " added in sorted order.");
        }
    }

    private static void deleteRecord() {
        System.out.println("\n--- Delete Student Record ---");
        if (list.isEmpty()) {
            System.out.println("The list is empty. Nothing to delete.");
            return;
        }

        System.out.print("Enter Student Name to delete: ");
        String name = scanner.nextLine().trim();
        if (list.delete(name)) {
            System.out.println("Success: Student record for " + name + " deleted.");
        }
    }

    private static void searchRecord() {
        System.out.println("\n--- Search Student Record ---");
        if (list.isEmpty()) {
            System.out.println("The list is empty.");
            return;
        }

        System.out.print("Enter Student Name to search: ");
        String name = scanner.nextLine().trim();
        Student found = list.search(name);

        if (found != null) {
            System.out.println("\nRecord Found:");
            System.out.println("+----------+------------------+--------------------+------+-------+");
            System.out.println("| ID       | Student Name     | Programme          | Age  | CGPA  |");
            System.out.println("+----------+------------------+--------------------+------+-------+");
            System.out.println(found);
            System.out.println("+----------+------------------+--------------------+------+-------+");
        } else {
            System.out.println("Student '" + name + "' was not found in the list.");
        }
    }

    private static void displayReverseCopy() {
        System.out.println("\n--- Create & Display Reverse Copy ---");
        if (list.isEmpty()) {
            System.out.println("List is empty. Cannot create reverse copy.");
            return;
        }

        CircularLinkedList reversed = list.createReverseCopy();
        System.out.println("\n>> REVERSED COPY (Independent Circular List):");
        reversed.display();

        System.out.println("\n>> VERIFYING ORIGINAL LIST (Remains strictly sorted):");
        list.display();
    }

    private static void saveToFile() {
        if (list.isEmpty()) {
            System.out.println("List is empty. Nothing to save.");
            return;
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_NAME))) {
            Node current = list.getHead();
            do {
                bw.write(current.data.toCsv());
                bw.newLine();
                current = current.link;
            } while (current != list.getHead());

            System.out.println("Successfully saved " + list.getSize() + " records to " + FILE_NAME);
        } catch (IOException e) {
            System.out.println("Failed to write records to file: " + e.getMessage());
        }
    }

    private static void loadFromFile() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            System.out.println("[Notice] No data file (" + FILE_NAME + ") found. Starting with empty list.");
            return;
        }

        int count = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(",");
                if (parts.length == 5) {
                    String id = parts[0].trim();
                    String name = parts[1].trim();
                    String prog = parts[2].trim();
                    int age = Integer.parseInt(parts[3].trim());
                    double cgpa = Double.parseDouble(parts[4].trim());

                    if (list.insertSorted(new Student(id, name, prog, age, cgpa))) {
                        count++;
                    }
                }
            }
            System.out.println("[Notice] Loaded " + count + " records from " + FILE_NAME);
        } catch (Exception e) {
            System.out.println("Error reading " + FILE_NAME + ": " + e.getMessage());
        }
    }
}
