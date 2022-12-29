package com.example.mutsa_sns.controller;

import com.example.mutsa_sns.service.HelloService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HelloController.class)
class HelloControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    HelloService helloService;

    @Test
    @DisplayName("/hello 출력 값 검증")
    @WithMockUser
    void name() throws Exception {

        mockMvc.perform(get("/api/v1/hello")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("김하늘"))
                .andDo(print());
    }

    @Test
    @DisplayName("/hello/num")
    @WithMockUser
    void addNum() throws Exception {

        int num = 1234;

        when(helloService.add(num)).thenReturn(10);

        mockMvc.perform(get("/api/v1/hello/"+num)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("10"))
                .andDo(print());

    }
}