package com.example.mutsa_sns.domain;

import com.example.mutsa_sns.domain.dto.AlarmDto;
import com.example.mutsa_sns.domain.dto.PostDto;
import lombok.*;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "alarm")
@Entity
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Alarm extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    //알람 타입 (like or comment)
    @Enumerated(EnumType.STRING)
    private AlarmType alarmType;

    //댓글이나 좋아요를 달은 userId
    private int fromUserId;

    //알람 발생한 postId
    private int targetId;

    private String text;

    public AlarmDto toResponse() {
        return AlarmDto.builder()
                .id(this.getId())
                .alarmType(this.getAlarmType())
                .fromUserId(this.getFromUserId())
                .targetId(this.getTargetId())
                .text(this.getAlarmType().getText())
                .createdAt(this.getCreatedAt())
                .build();
    }

    public static Alarm toEntity(Post post, User user, AlarmType alarmType) {
        return Alarm.builder()
                .user(post.getUser())
                .alarmType(alarmType)
                .fromUserId(user.getId())
                .targetId(post.getId())
                .build();
    }

}
