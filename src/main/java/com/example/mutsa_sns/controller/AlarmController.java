package com.example.mutsa_sns.controller;

import com.example.mutsa_sns.domain.dto.AlarmDto;
import com.example.mutsa_sns.domain.dto.Response;
import com.example.mutsa_sns.service.AlarmService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping("/api/v1/alarms")
@RequiredArgsConstructor
public class AlarmController {

    private final AlarmService alarmService;

    @GetMapping
    public Response<Page<AlarmDto>> getAlarm(@PageableDefault(size=20, sort="createdAt", direction = Sort.Direction.DESC) Pageable pageable,
                                             @ApiIgnore Authentication authentication) {
        Page<AlarmDto> alarmDtos = alarmService.getAlarm(pageable, authentication.getName());

        return Response.success(alarmDtos);
    }
}
