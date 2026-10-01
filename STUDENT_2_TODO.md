# 📋 Student 2 (System Architect Lead) - TODO 清单与实施指南

---

## 📌 角色与模块职责
- **角色**：Student 2 (System Architect Lead)
- **负责模块**：`Student.java`、Traversal & Copy Logic、Reporting Service (`StudentReport.java`)
- **关联文件**：
  - `src/model/Student.java`
  - `src/service/StudentReport.java`
  - `src/adt/CircularLinkedList.java`

---

## 📝 任务状态与 TODO 列表

- [ ] **TODO 1: 完善 `Student.java` 类**
  - [ ] 类声明添加 `implements Comparable<Student>`
  - [ ] 实现 `@Override public int compareTo(Student other)`（按姓名忽略大小写排序）
  - [ ] 优化 `toString()` 为规范化输出（使用 `String.format` 保证 CGPA 保留 2 位小数）
- [x] **任务 2: 安全的 `display()` 遍历** *(已由团队完成并验证)*
  - 核心实现位于 `src/adt/CircularLinkedList.java`，已使用安全的 `do-while` 循环避免死循环。
- [x] **任务 3: 按姓名 `search()`** *(已由团队完成并验证)*
  - 核心实现位于 `src/adt/CircularLinkedList.java`，比较逻辑与 `cmp < 0` 提前终止已修复。
- [ ] **TODO 4: 确认并优化 `createReverseCopy()`**
  - [ ] 检查是否需要实现 `Student` 对象的深拷贝（Deep Copy）以保证完全独立的副本列表。
- [ ] **TODO 5: 实现报表生成 `StudentReport.java`**
  - [ ] 实现 `generateReport(CircularLinkedList list)` 方法
  - [ ] 准确统计：学生总人数（count）、平均绩点（average CGPA）、最高绩点（max CGPA）及最低绩点（min CGPA）
  - [ ] 完善边界处理：空链表防崩保护（避免除以 0 导致 `NaN`）
  - [ ] 控制台美观格式化输出统计结果

---

## 💻 具体代码实现参考

### 1. `src/model/Student.java` 代码补充

```java
package model;

// 1. 实现 Comparable<Student>
public class Student implements Comparable<Student> {
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

    public String getId() { return id; }
    public String getName() { return name; }
    public String getProgramme() { return programme; }
    public int getAge() { return age; }
    public double getCgpa() { return cgpa; }

    // 2. 实现 compareTo 方法（按名字升序排序）
    @Override
    public int compareTo(Student other) {
        if (other == null) {
            return 1;
        }
        return this.name.compareToIgnoreCase(other.name);
    }

    // 3. 规范化格式输出 toString()
    @Override
    public String toString() {
        return String.format("Student [ID: %-8s | Name: %-15s | Programme: %-5s | Age: %2d | CGPA: %.2f]",
                id, name, programme, age, cgpa);
    }
}
```

---

### 2. `src/service/StudentReport.java` 代码实现

```java
package service;

import adt.CircularLinkedList;
import model.Node;
import model.Student;

public class StudentReport {

    /**
     * 计算并输出学生表现统计报表
     * 涵盖：count, average CGPA, min/max CGPA
     */
    public static void generateReport(CircularLinkedList list) {
        System.out.println("\n==================================================");
        System.out.println("            STUDENT PERFORMANCE REPORT            ");
        System.out.println("==================================================");

        // 边界防护：检查列表是否为空
        if (list == null || list.isEmpty()) {
            System.out.println("No records found in the list to generate report.");
            System.out.println("==================================================\n");
            return;
        }

        int count = 0;
        double sumCgpa = 0.0;
        double minCgpa = Double.MAX_VALUE;
        double maxCgpa = Double.MIN_VALUE;

        Student highestStudent = null;
        Student lowestStudent = null;

        // 安全循环遍历统计
        Node current = list.getHead();
        do {
            Student s = current.data;
            count++;
            sumCgpa += s.getCgpa();

            if (s.getCgpa() > maxCgpa) {
                maxCgpa = s.getCgpa();
                highestStudent = s;
            }

            if (s.getCgpa() < minCgpa) {
                minCgpa = s.getCgpa();
                lowestStudent = s;
            }

            current = current.link;
        } while (current != list.getHead());

        double avgCgpa = sumCgpa / count;

        // 格式化输出
        System.out.printf("Total Students Enrolled : %d\n", count);
        System.out.printf("Average CGPA            : %.2f\n", avgCgpa);
        System.out.printf("Highest CGPA            : %.2f (by %s)\n", maxCgpa, highestStudent.getName());
        System.out.printf("Lowest CGPA             : %.2f (by %s)\n", minCgpa, lowestStudent.getName());
        System.out.println("--------------------------------------------------");
        System.out.println("Report generated successfully.");
        System.out.println("==================================================\n");
    }
}
```

---

### 3. `createReverseCopy()` 独立副本（可选深度拷贝）

在 `src/adt/CircularLinkedList.java` 中：
```java
// 若课程要求完全独立的深拷贝（Deep Copy）：
Node current = head;
do {
    Student s = current.data;
    // 创建全新的 Student 实例，彻底切断与原数据的引用关联
    Student copy = new Student(s.getId(), s.getName(), s.getProgramme(), s.getAge(), s.getCgpa());
    reversed.insertFront(copy);
    current = current.link;
} while (current != head);
```

---

## 🧪 验证与自测步骤

完成上述代码后，可在项目根目录下运行 PowerShell 测试编译：

```powershell
javac -d bin (Get-ChildItem -Recurse -Filter *.java src | ForEach-Object { $_.FullName })
```

如无任何报错，即可在 `src/Main.java` 中编写简单测试调用 `generateReport()` 与 `createReverseCopy()`。
