package com.example.ptitweb.controller;

import com.example.ptitweb.entity.Student;
import com.example.ptitweb.repository.StudentRepository;
import com.example.ptitweb.service.StudentService;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/TrangXemKetQua")

public class StudentController {
    private final StudentRepository studentRepository;
    private final StudentService studentService;

    @Autowired
    public StudentController(StudentRepository studentRepository, StudentService studentService) {
        this.studentRepository = studentRepository;
        this.studentService = studentService;
    }

    @GetMapping("/searchname/{name}")
    public ResponseEntity<List<Student>> getStudentByName(@PathVariable String name) {
        List<Student> students = studentRepository.findByNameContaining(name);
        return new ResponseEntity<>(students, HttpStatus.OK);
    }

    @GetMapping("/searchname/{name}/export")
    public ResponseEntity<byte[]> exportStudentToExcel(@PathVariable String name) {
        List<Student> students = studentRepository.findByNameContaining(name);

        Workbook workbook = new XSSFWorkbook();

        Sheet sheet = workbook.createSheet("Students");

        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("ID");
        headerRow.createCell(1).setCellValue("Name");
        headerRow.createCell(2).setCellValue("Exams");

        int rowNum = 1;
        for (Student student : students) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(student.getId());
            row.createCell(1).setCellValue(student.getName());
            row.createCell(2).setCellValue(student.getExams());
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            workbook.write(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "students.xlsx");

        return new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);
    }
}