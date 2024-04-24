package com.example.ptitweb.controller;

import com.example.ptitweb.entity.StudentResult;
import com.example.ptitweb.repository.StudentResultRepository;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/TrangThongKe")

public class StudentResultController {

    private final StudentResultRepository studentResultRepository;

    @Autowired
    public StudentResultController(StudentResultRepository studentResultRepository) {
        this.studentResultRepository = studentResultRepository;
    }

    @GetMapping("/GetAllStudents")
    public ResponseEntity<List<StudentResult>> getAllStudents() {
        List<StudentResult> students = studentResultRepository.findAll();
        return new ResponseEntity<>(students, HttpStatus.OK);
    }

    @GetMapping("/searchname/{name}")
    public ResponseEntity<List<StudentResult>> getStudentsByName(@PathVariable String name) {
        List<StudentResult> students = studentResultRepository.findByNameContaining(name);
        return new ResponseEntity<>(students, HttpStatus.OK);
    }

    @GetMapping("/searchscore/{averageScore}")
    public ResponseEntity<List<StudentResult>> getStudentsWithHigherAverageScore(@PathVariable double averageScore) {
        List<StudentResult> students = studentResultRepository.findAll();
        List<StudentResult> filteredStudents = new ArrayList<>();

        for (StudentResult student : students) {
            if (student.getAverageScore() > averageScore) {
                filteredStudents.add(student);
            }
        }

        return new ResponseEntity<>(filteredStudents, HttpStatus.OK);
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<StudentResult> updateStudent(@PathVariable int id, @RequestBody StudentResult updatedStudent) {
        StudentResult student = studentResultRepository.findById(id);

        if (student != null) {
            student.setName(updatedStudent.getName());
            student.setAverageScore(updatedStudent.getAverageScore());
            student.setTotalAttempts(updatedStudent.getTotalAttempts());
            student.setCompletionRate(updatedStudent.getCompletionRate());

            student = studentResultRepository.save(student);

            return new ResponseEntity<>(student, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    @GetMapping("/export")
    public ResponseEntity<ByteArrayResource> exportToExcel() {
        List<StudentResult> students = studentResultRepository.findAll();

        Workbook workbook = new XSSFWorkbook();

        Sheet sheet = workbook.createSheet("Student Results");

        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("ID");
        headerRow.createCell(1).setCellValue("Name");
        headerRow.createCell(2).setCellValue("Total Attempts");
        headerRow.createCell(3).setCellValue("Completion Rate");
        headerRow.createCell(4).setCellValue("Average Score");

        int rowNum = 1;
        for (StudentResult student : students) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(student.getId());
            row.createCell(1).setCellValue(student.getName());
            row.createCell(2).setCellValue(student.getTotalAttempts());
            row.createCell(3).setCellValue(student.getCompletionRate());
            row.createCell(4).setCellValue(student.getAverageScore());
        }

        try {
            File excelFile = File.createTempFile("student_results", ".xlsx");
            FileOutputStream fileOutputStream = new FileOutputStream(excelFile);
            workbook.write(fileOutputStream);
            fileOutputStream.close();

            ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(excelFile.toPath()));

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=student_results.xlsx");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}