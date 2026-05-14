package com.example.uniexam.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "lecturers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lecturer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @Email
    @Column(nullable = false)
    private String email;

    @NotBlank
    @Column(unique = true, nullable = false)
    private String employeeId;

    private String phone;
    private Long facultyId;

    // Stored as comma-separated module IDs
    @Column(length = 1000)
    private String moduleIds;

    private Integer joinedYear;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
