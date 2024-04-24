package com.example.ptitweb.controller;

import com.example.ptitweb.entity.Test;
import com.example.ptitweb.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ptit/api/tests")
public class TestController {

    @Autowired
    private TestService testService;

    @GetMapping
    public ResponseEntity<List<Test>> getAllTests() {
        List<Test> tests = testService.getAllTests();
        return new ResponseEntity<>(tests, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Test> createTest(@RequestBody Test test) {
        Test createdTest = testService.createTest(test);
        return new ResponseEntity<>(createdTest, HttpStatus.CREATED);
    }

    @GetMapping("/{testId}")
    public ResponseEntity<Test> getTestById(@PathVariable Integer testId) {
        Test test = testService.getTestById(testId);
        return new ResponseEntity<>(test, HttpStatus.OK);
    }

    @GetMapping("/name/{name}")
    public Test fetchTestByName(@PathVariable("name") String name) {
        return testService.fetchTestByName(name);
    }

    @GetMapping("/status/{status}")
    public Test fetchTestByStatus(@PathVariable("status") String status) {
        return testService.fetchTestByStatus(status);
    }
}
