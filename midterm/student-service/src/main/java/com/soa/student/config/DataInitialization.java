package com.soa.student.config;

import com.soa.student.entity.Semester;
import com.soa.student.entity.Student;
import com.soa.student.entity.StudentSemesterFee;
import com.soa.student.service.SemesterService;
import com.soa.student.service.StudentSemesterFeeService;
import com.soa.student.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitialization implements CommandLineRunner {

    @Autowired
    private StudentService studentService;
    
    @Autowired
    private SemesterService semesterService;
    
    @Autowired
    private StudentSemesterFeeService studentSemesterFeeService;

    @Override
    public void run(String... args) {
        seedSemesters();
        seedStudents();
        seedStudentSemesterFees();
    }

    private void seedStudents() {
        // 10 sinh viên mẫu
        Student[] samples = new Student[] {
                new Student("523H1001","Nguyen Van A","s1001@example.com"),
                new Student("523H1002","Nguyen Van B","s1002@example.com"),
                new Student("523H1003","Tran Thi C","s1003@example.com"),
                new Student("523H1004","Le Van D","s1004@example.com"),
                new Student("523H1005","Pham Thi E","s1005@example.com"),
                new Student("523H1006","Do Van F","s1006@example.com"),
                new Student("523H1007","Bui Thi G","s1007@example.com"),
                new Student("523H1008","Hoang Van H","s1008@example.com"),
                new Student("523H1009","Vo Thi I","s1009@example.com"),
                new Student("523H1010","Phan Van J","s1010@example.com")
        };

        for (Student s : samples) {
            Student existing = studentService.findStudentByStudentId(s.getStudentId());
            if (existing == null) {
                studentService.saveStudent(s);
                System.out.println("✅ Created student: " + s.getStudentId());
            }
        }

        // 2 sinh viên cũ
        if (studentService.findStudentByStudentId("523H0073") == null) {
            studentService.saveStudent(new Student("523H0073","Nguyen Van A","abc@gmail.com"));
            System.out.println("✅ Created student: 523H0073");
        }
        if (studentService.findStudentByStudentId("523H0074") == null) {
            studentService.saveStudent(new Student("523H0074","Nguyen Van B","phanhuyphat6@gmail.com"));
            System.out.println("✅ Created student: 523H0074");
        }
    }

    private void seedSemesters() {
        String[][] semesterData = {
            {"HK1_2024", "Học kỳ 1 năm 2024", "2024", "1"},
            {"HK2_2024", "Học kỳ 2 năm 2024", "2024", "2"},
            {"HK3_2024", "Học kỳ hè năm 2024", "2024", "3"},
            {"HK1_2025", "Học kỳ 1 năm 2025", "2025", "4"},
            {"HK2_2025", "Học kỳ 2 năm 2025", "2025", "5"}
        };

        for (String[] data : semesterData) {
            if (semesterService.getSemesterByCode(data[0]).isEmpty()) {
                Semester semester = new Semester();
                semester.setSemesterCode(data[0]);
                semester.setSemesterName(data[1]);
                semester.setYear(data[2]);
                semester.setSemesterOrder(Integer.parseInt(data[3]));
                semester.setActive(true);
                semesterService.save(semester);
                System.out.println("✅ Created semester: " + data[0]);
            }
        }
    }

    private void seedStudentSemesterFees() {
        Semester[] semesters = semesterService.getAllSemesters().toArray(new Semester[0]);
        
        if (semesters.length == 0) {
            System.out.println("⚠️ No semesters found, skipping StudentSemesterFee seeding");
            return;
        }

        String[] studentIds = {
            "523H1001", "523H1002", "523H1003", "523H1004", "523H1005",
            "523H1006", "523H1007", "523H1008", "523H1009", "523H1010",
            "523H0073", "523H0074"
        };

        double baseFee = 3_500_000;

        for (String studentId : studentIds) {
            Student student = studentService.findStudentByStudentId(studentId);
            if (student == null) continue;

            for (int i = 0; i < semesters.length; i++) {
                Semester semester = semesters[i];
                
                if (studentSemesterFeeService.getFeeByStudentAndSemester(student, semester).isEmpty()) {
                    double fee = baseFee + (studentId.hashCode() % 2_000_000);
                    
                    // Một số sinh viên đã đóng học kỳ cũ
                    boolean isPaid = false;
                    double paidAmount = 0.0;
                    
                    if (i < 2 && studentId.hashCode() % 3 == 0) {
                        isPaid = true;
                        paidAmount = fee;
                    }
                    
                    StudentSemesterFee ssf = new StudentSemesterFee(student, semester, fee);
                    ssf.setPaidAmount(paidAmount);
                    ssf.setPaid(isPaid);
                    studentSemesterFeeService.save(ssf);
                }
            }
        }
        
        System.out.println("✅ Student Service: All data seeded successfully");
    }
}
