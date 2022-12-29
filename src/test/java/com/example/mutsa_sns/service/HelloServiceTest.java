package com.example.mutsa_sns.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HelloServiceTest {

    HelloService helloService = new HelloService();

    @Test
    @DisplayName("자리수 합 검증")
    void addNum() {
        assertEquals(21, helloService.add(687));
        assertEquals(0, helloService.add(0));
        assertEquals(10, helloService.add(1234));
    }
}