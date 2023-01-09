package com.example.mutsa_sns.repository;

import com.example.mutsa_sns.domain.Alarm;
import com.example.mutsa_sns.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlarmRepository extends JpaRepository<Alarm, Integer> {
    Page<Alarm> findAllByUser(User user, Pageable pageable);

}
