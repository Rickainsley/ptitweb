package com.example.ptitweb.controller;

import com.example.ptitweb.entity.Exam;
import com.example.ptitweb.service.ExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ExamController {

    @Autowired
    private ExamService examService;

    @PostMapping("/ptit/exams")
    public Exam saveExam(@RequestBody Exam exam) {
        return examService.saveExam(exam);
    }

    @GetMapping("/ptit/exams")
    public List<Exam> fetchExamList() {
        return examService.fetchExamList();
    }

    @DeleteMapping("/ptit/exams/{id}")
    public String deleteExamById(@PathVariable("id") Long examId){
        examService.deleteExamById(examId);
        return "Exam deleted Successfully";
    }

    @PutMapping("/ptit/exams/{id}")
    public Exam updateExam(@PathVariable("id") Long examId, @RequestBody Exam exam) {
        return examService.updateExam(examId, exam);
    }

    @GetMapping("/ptit/exams/examTitle/{examTitle}")
    public Exam fetchExamByTitle(@PathVariable("examTitle") String examTitle) {
        return examService.fetchExamByTitle(examTitle);
    }

    @GetMapping("/ptit/exams/status/{status}")
    public Exam fetchExamByStatus(@PathVariable("status") String status) {
        return examService.fetchExamByStatus(status);
    }
}
