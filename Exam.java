package com.example.uniexam.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "exams")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Exam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long moduleId;
    private Long lecturerId;

    @NotBlank
    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String instructions;

    private LocalDateTime scheduledDate;
    private Integer duration; // minutes

    @Column(nullable = false)
    private String status; // "draft", "upcoming", "active", "completed"

    private Integer passMark;
    private Integer totalMarks;

    // JSON string of questions array
    @Column(columnDefinition = "CLOB")
    private String questionsJson;

    // JSON string of submissions array
    @Column(columnDefinition = "CLOB")
    private String submittedByJson;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) status = "draft";
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
