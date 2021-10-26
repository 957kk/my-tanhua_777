package com.tanhua.server.controller;

import com.tanhua.common.pojo.AnswerList;
import com.tanhua.common.pojo.SoulPaper;
import com.tanhua.server.service.TestSoulService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("testSoul")
public class TestSoulController {


    @Autowired
    private TestSoulService testSoulService;



    @GetMapping
    public List<SoulPaper> queryTestSoulList() {
         return this.testSoulService.getQuestion();
    }

    @PostMapping
    public ResponseEntity submitPaper(@RequestBody AnswerList answers) {

        return testSoulService.submitPaper(answers);
    }

    @GetMapping("report/{id}")
    public ResponseEntity ViewReport(@PathVariable("id") String reportId) {

        return testSoulService.ViewReport(reportId);
    }

}
