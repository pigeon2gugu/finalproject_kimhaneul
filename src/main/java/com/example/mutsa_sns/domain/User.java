package com.example.mutsa_sns.domain;

import lombok.*;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import javax.persistence.*;
import java.sql.Timestamp;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Table(name = "user")
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String password;
    private Timestamp registeredAt;
    @Enumerated(EnumType.STRING)
    private UserRole role;
    private Timestamp updatedAt;
    @Column(unique = true)
    private String userName;

}
