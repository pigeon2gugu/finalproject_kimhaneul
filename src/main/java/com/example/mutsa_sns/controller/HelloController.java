package com.example.mutsa_sns.controller;
import com.example.mutsa_sns.service.HelloService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.criteria.CriteriaBuilder;

@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
public class HelloController {

    private final HelloService helloService;

    @GetMapping("/hello")
    @ApiOperation(value = "hello")
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok().body("김하늘");
    }

    @GetMapping("/hello/{num}")
    @ApiOperation(value ="자리수 합")
    public int helloNum(@PathVariable int num) {
        int addNum = helloService.add(num);

        return addNum;
    }
}