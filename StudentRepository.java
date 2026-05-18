package com.example.uniexam.repository;

import com.example.uniexam.models.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByUserId(Long userId);
    Optional<Student> findByStudentNumber(String studentNumber);
    List<Student> findByFacultyId(Long facultyId);
    List<Student> findByCourseId(Long courseId);
}
