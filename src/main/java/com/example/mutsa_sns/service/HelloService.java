package com.example.mutsa_sns.service;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HelloService {

    public int add(int num) {

        int addNum = 0;

        while(num > 0) {
            addNum += num%10;
            num = num/10;
        }

        return addNum;
    }
}
